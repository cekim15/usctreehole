package com.example.usctreehole;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignupActivity";
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView profilepic;
    private Uri profilepicuri;
    private DrawerLayout dl;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        profilepicuri = data.getData();
                        profilepic.setImageURI(profilepicuri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");
        profilepic = findViewById(R.id.imageViewProfilePic);
        View uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(view -> openFileChooser());

        setUpToolbar();

        View signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.enterEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.enterPassword)).getText().toString();
            String name = ((EditText) findViewById(R.id.enterName)).getText().toString();
            String uscID = ((EditText) findViewById(R.id.enterID)).getText().toString();
            Spinner roleSelect = findViewById(R.id.roleSelect);
            String role = roleSelect.getSelectedItem().toString();

            if (profilepicuri != null) {
                createUser(email, password, name, uscID, role);
            } else {
                Toast.makeText(Signup.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void createUser(String email, String password, String name, String uscID, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        StorageReference fileRef = storageRef.child(uid + ".jpg");

                        fileRef.putFile(profilepicuri)
                                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("name", name);
                                    userInfo.put("uscID", uscID);
                                    userInfo.put("role", role);
                                    userInfo.put("profilePicUrl", uri.toString());
                                    userInfo.put("profilePicVersion", 0);

                                    // subscription settings
                                    userInfo.put("lifeSubscription", false);
                                    userInfo.put("academicSubscription", false);
                                    userInfo.put("eventSubscription", false);

                                    db.collection("users").document(uid)
                                            .set(userInfo)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d(TAG, "User info saved");
                                                Intent intent = new Intent(Signup.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(this::deleteUserAndHandleError);
                                }))
                                .addOnFailureListener(this::deleteUserAndHandleError);
                    } else {
                        Log.d(TAG, "User creation failed");
                    }
                });
    }

    /*private byte[] resizeImage() {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(profilepicuri);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file not found exception " + e.getMessage());
        }
        Bitmap og_bitmap = BitmapFactory.decodeStream(inputStream);
        int target_width = 300;
        int target_height = (og_bitmap.getHeight() * target_width) / og_bitmap.getWidth();
        Bitmap resized = Bitmap.createScaledBitmap(og_bitmap, target_width, target_height, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    } */

    private void deleteUserAndHandleError(Exception e) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener(deleteTask -> {
                if (deleteTask.isSuccessful()) {
                    Toast.makeText(Signup.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Couldn't delete user");
                }
            });
        }
    }

    public void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        dl = findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, dl, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_login) {
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(Signup.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}