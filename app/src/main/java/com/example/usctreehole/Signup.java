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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignupActivity";
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView profilepic;
    private Uri profilepicuri;

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

        View goToLogin = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
        });

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
                        String uid = mAuth.getCurrentUser().getUid();
                        StorageReference fileRef = storageRef.child(uid + ".jpg");

                        fileRef.putFile(profilepicuri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("name", name);
                                        userInfo.put("uscID", uscID);
                                        userInfo.put("role", role);
                                        userInfo.put("profilePicUrl", uri.toString());

                                        db.collection("users").document(uid)
                                                .set(userInfo)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(Signup.this, "User info saved", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    mAuth.getCurrentUser().delete()
                                                            .addOnCompleteListener(deleteTask -> {
                                                                if (deleteTask.isSuccessful()) {
                                                                    Toast.makeText(Signup.this, "User creation failed and deleted: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(Signup.this, "Failed to delete user: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    mAuth.signOut();
                                                });
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // If the image upload fails, delete the user as well
                                    mAuth.getCurrentUser().delete()
                                            .addOnCompleteListener(deleteTask -> {
                                                if (deleteTask.isSuccessful()) {
                                                    Toast.makeText(Signup.this, "Upload failed and user deleted: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(Signup.this, "Failed to delete user: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    mAuth.signOut();
                                });
                    } else {
                        Toast.makeText(Signup.this, "User creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Log.d(TAG, "calling reload");
        reload(mAuth.getCurrentUser());
    }


    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload(currentUser);
        }
    }

    private void reload(FirebaseUser user) {
        Log.d(TAG, "called reload");
        if (user != null) {
            Intent intent = new Intent (Signup.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}