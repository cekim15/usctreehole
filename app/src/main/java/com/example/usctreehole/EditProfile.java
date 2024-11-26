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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";

    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private StorageReference storageRef;
    private ImageView profileImageView;
    Uri profilepicuri;

    String old_name, old_ID, old_role, old_profileUrl;
    boolean changed_pic;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        profilepicuri = data.getData();
                        profileImageView.setImageURI(profilepicuri);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Log.d(TAG, "creating edit profile activity");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(EditProfile.this, Login.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        setContentView(R.layout.activity_edit_profile);

        setUpToolbar();

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> {
            dl.openDrawer(GravityCompat.END);
        });

        Intent oldInfo = getIntent();
        populateWithOld(oldInfo);

        storageRef = FirebaseStorage.getInstance().getReference("profile_pics");
        View uploadImageButton = findViewById(R.id.uploadNewImageButton);
        uploadImageButton.setOnClickListener(view -> openFileChooser());

        // go back to profile page without saving changes
        Button cancelEdit = findViewById(R.id.cancel_profile_edit);
        cancelEdit.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, Profile.class);
            startActivity(intent);
            finish();
        });

        // try and save changes to the database
        Button saveEdit = findViewById(R.id.save_profile_edit);
        saveEdit.setOnClickListener(view -> saveProfileEdits());
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

            Map<String, Object> changedFields = new HashMap<>();
            getChangedFields(changedFields);

            db.collection("users").document(uid)
                    .update(changedFields)
                    .addOnSuccessListener(aVoid -> {
                        if (changed_pic) {
                            db.collection("users").document(uid).update("profilePicVersion", FieldValue.increment(1));
                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String old_pfp = documentSnapshot.getString("profilePicUrl");
                                            if (old_pfp != null) {
                                                StorageReference old_file = FirebaseStorage.getInstance().getReferenceFromUrl(old_pfp);
                                                old_file.delete()
                                                        .addOnSuccessListener(oldDelete -> {
                                                            Log.d(TAG, "Old profile picture deleted successfully");
                                                            //byte[] image_data = resizeImage();
                                                            StorageReference fileReference = storageRef.child(uid + ".jpg");
                                                            fileReference.putFile(profilepicuri)
                                                                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                                        String urlToSave = uri.toString();
                                                                        db.collection("users").document(uid)
                                                                                .update("profilePicUrl", urlToSave)
                                                                                .addOnSuccessListener(upload_new -> {
                                                                                    Log.d(TAG, "uploaded new pfp to database");
                                                                                    Log.d("TAG", "DocumentSnapshot successfully updated!");
                                                                                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                                                                    Intent intent = new Intent(EditProfile.this, Profile.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                })
                                                                                .addOnFailureListener(e -> {
                                                                                    Log.w(TAG, "Error updating document with new profile picture", e);
                                                                                    Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                                                                });
                                                                    }))
                                                                    .addOnFailureListener(e -> {
                                                                        Log.e(TAG, "Error uploading new profile picture: ", e);
                                                                        Toast.makeText(EditProfile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e(TAG, "Error deleting old profile picture: ", e);
                                                            //byte[] image_data = resizeImage();
                                                            StorageReference fileReference = storageRef.child(uid + ".jpg");
                                                            fileReference.putFile(profilepicuri)
                                                                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                                                        String urlToSave = uri.toString();
                                                                        db.collection("users").document(uid)
                                                                                .update("profilePicUrl", urlToSave)
                                                                                .addOnSuccessListener(upload_new -> {
                                                                                    Log.d(TAG, "uploaded new pfp to database");
                                                                                    Log.d("TAG", "DocumentSnapshot successfully updated!");
                                                                                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                                                                                    Intent intent = new Intent(EditProfile.this, Profile.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                })
                                                                                .addOnFailureListener(ughhh -> {
                                                                                    Log.w(TAG, "Error updating document with new profile picture", e);
                                                                                    Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                                                                });
                                                                    }))
                                                                    .addOnFailureListener(sobbb -> {
                                                                        Log.e(TAG, "Error uploading new profile picture: ", e);
                                                                        Toast.makeText(EditProfile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error fetching profile data: ", e);
                                        Toast.makeText(EditProfile.this, "Failed to fetch profile data", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                            Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(EditProfile.this, Profile.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("TAG", "Error updating document", e);
                        Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    void getChangedFields(Map<String, Object> changedFields) {
        String editedName = ((EditText) findViewById(R.id.enterName)).getText().toString();
        String editedID = ((EditText) findViewById(R.id.enterID)).getText().toString();
        Spinner roleSelect = findViewById(R.id.roleSelect);
        String editedRole = roleSelect.getSelectedItem().toString();
        String newProfileUrl;
        changed_pic = false;
        if (profilepicuri != null) {
            newProfileUrl = profilepicuri.toString();
            if (!old_profileUrl.equals(newProfileUrl)) {
                changed_pic = true;
                Log.d(TAG, "Changed pfp");
            }
        }

        if (!old_name.equals(editedName)) {
            changedFields.put("name",editedName);
            Log.d(TAG, "Name changed to: " + editedName);
        }
        if (!old_ID.equals(editedID)) {
            changedFields.put("uscID", editedID);
            Log.d(TAG, "ID changed to: " + editedID);
        }
        if (!old_role.equals(editedRole)) {
            changedFields.put("role", editedRole);
            Log.d(TAG, "Role changed to: " + editedRole);
        }
    }

    private void populateWithOld(Intent oldInfo) {
        old_name = oldInfo.getStringExtra("name");
        old_ID = oldInfo.getStringExtra("uscID");
        old_role = oldInfo.getStringExtra("role");
        old_profileUrl = oldInfo.getStringExtra("profilePicUrl");
        Log.d(TAG, "old profile url: " + old_profileUrl);

        ((EditText) findViewById(R.id.enterName)).setText(old_name);
        ((EditText) findViewById(R.id.enterID)).setText(old_ID);
        Spinner roleSelect = findViewById(R.id.roleSelect);

        switch (old_role) {
            case "Graduate Student":
                roleSelect.setSelection(1);
                break;
            case "Faculty":
                roleSelect.setSelection(2);
                break;
            case "Staff":
                roleSelect.setSelection(3);
                break;
        }

        profileImageView = findViewById(R.id.imageViewProfilePic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileImageView != null) {
            profileImageView.setImageDrawable(null);
        }
    }

    private void setUpToolbar() {
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
    }

}