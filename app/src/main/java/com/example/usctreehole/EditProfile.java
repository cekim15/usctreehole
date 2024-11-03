package com.example.usctreehole;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "EditProfile";
    private StorageReference storageRef;
    private ImageView profileImageView;
    private Uri profilepicuri;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        profileImageView.setImageURI(null);
                        profilepicuri = data.getData();

                        try {
                            Glide.with(this)
                                    .load(profilepicuri)
                                    .override(100, 100)
                                    .placeholder(R.drawable.blank_profile_pic)
                                    .fitCenter()
                                    .skipMemoryCache(true)
                                    .into(profileImageView);
                        } catch (Error e) {
                            Log.e(TAG, "Out of memory error while loading image", e);
                            Toast.makeText(this, "Failed to load image. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(EditProfile.this, Login.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        setContentView(R.layout.activity_edit_profile);

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
            if (id == R.id.nav_profile) {
                Intent intent = new Intent(EditProfile.this, Profile.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                Intent intent = new Intent(EditProfile.this, Login.class);
                startActivity(intent);
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> openNotifications());

        Intent oldInfo = getIntent();

        String name = oldInfo.getStringExtra("name");
        String uscID = oldInfo.getStringExtra("uscID");
        String role = oldInfo.getStringExtra("role");
        String profilePicUrl = oldInfo.getStringExtra("profilePicUrl");

        ((EditText) findViewById(R.id.enterName)).setText(name);
        ((EditText) findViewById(R.id.enterID)).setText(uscID);
        Spinner roleSelect = findViewById(R.id.roleSelect);

        if (role.equals("Graduate Student")) {
            roleSelect.setSelection(1);
        } else if (role.equals("Faculty")) {
            roleSelect.setSelection(2);
        } else if (role.equals("Staff")) {
            roleSelect.setSelection(3);
        }

        profileImageView = findViewById(R.id.imageViewProfilePic);

        if (profilePicUrl != null) {
            try {
                Glide.with(this)
                        .load(profilePicUrl)
                        .override(100, 100)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.blank_profile_pic)
                        .into(profileImageView);
            } catch (Error e) {
                Log.e(TAG, "Out of memory error while loading image", e);
                Toast.makeText(this, "Failed to load image. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");
        View uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(view -> openFileChooser());

        Button cancelEdit = findViewById(R.id.cancel_profile_edit);
        cancelEdit.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, Profile.class);
            startActivity(intent);
            finish();
        });

        Button saveEdit = findViewById(R.id.save_profile_edit);
        saveEdit.setOnClickListener(view -> {
            saveProfileEdits();
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void saveProfileEdits() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(EditProfile.this, Login.class);
            startActivity(loginIntent);
            finish();
        } else {
            String uid = currentUser.getUid();
            String editedName = ((EditText) findViewById(R.id.enterName)).getText().toString();
            String editedID = ((EditText) findViewById(R.id.enterID)).getText().toString();
            Spinner roleSelect = findViewById(R.id.roleSelect);
            String editedRole = roleSelect.getSelectedItem().toString();

            Map<String, Object> newProfile = new HashMap<>();
            newProfile.put("name", editedName);
            newProfile.put("uscID", editedID);
            newProfile.put("role", editedRole);

            if (profilepicuri != null) {
                db.collection("users").document(uid).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String old_pfp = documentSnapshot.getString("profilePicUrl");
                                if (old_pfp != null) {
                                    deleteOldPfp(old_pfp);
                                }

                                StorageReference fileReference = storageRef.child(uid + ".jpg");
                                fileReference.putFile(profilepicuri)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            fileReference.getDownloadUrl().addOnSuccessListener(uri -> {

                                                String profilePicUrl = uri.toString();
                                                newProfile.put("profilePicUrl", profilePicUrl);

                                                db.collection("users").document(uid)
                                                        .set(newProfile, SetOptions.merge())
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(EditProfile.this, Profile.class);
                                                            startActivity(intent);
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e(TAG, "Error updating profile: ", e);
                                                            Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                                        });
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Error uploading new profile picture: ", e);
                                            Toast.makeText(EditProfile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error fetching profile data: ", e);
                            Toast.makeText(EditProfile.this, "Failed to fetch profile data", Toast.LENGTH_SHORT).show();
                        });
            } else {
                db.collection("users").document(uid)
                        .set(newProfile, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditProfile.this, Profile.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error updating profile: ", e);
                            Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }


    private void deleteOldPfp(String old_pfp) {
        StorageReference old_file = FirebaseStorage.getInstance().getReferenceFromUrl(old_pfp);
        old_file.delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Old profile picture deleted successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting old profile picture: ", e));
    }

    private void openNotifications() {
        // open notifications screen?
        Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
    }

}