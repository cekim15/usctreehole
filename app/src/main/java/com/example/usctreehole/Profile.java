package com.example.usctreehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView profileImageView;
    private TextView nameTextView, uscIdTextView, roleTextView;
    private SwitchCompat lifeNotifications, academicNotifications, eventNotifications;
    private Intent editIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editIntent = new Intent(Profile.this, EditProfile.class);

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
            if (id == R.id.nav_home) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });

        profileImageView = findViewById(R.id.profileImageView);
        nameTextView = findViewById(R.id.nameTextView);
        uscIdTextView = findViewById(R.id.uscIdTextView);
        roleTextView = findViewById(R.id.roleTextView);

        lifeNotifications = findViewById(R.id.lifeNotifications);
        academicNotifications = findViewById(R.id.academicNotifications);
        eventNotifications = findViewById(R.id.eventNotifications);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(Profile.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            String uid = currentUser.getUid();
            loadUserInfo(uid);

            manageNotificationSettings(uid);
        }

        Button editProfileButton = findViewById(R.id.editProfileButton);
        editProfileButton.setOnClickListener(view -> {
            startActivity(editIntent);
            finish();
        });
    }

    private void loadUserInfo(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String uscID = documentSnapshot.getString("uscID");
                        String role = documentSnapshot.getString("role");
                        String profilePicUrl = documentSnapshot.getString("profilePicUrl");

                        editIntent.putExtra("name", name);
                        editIntent.putExtra("uscID", uscID);
                        editIntent.putExtra("role", role);
                        editIntent.putExtra("profilePicUrl", profilePicUrl);

                        nameTextView.setText(name);
                        uscIdTextView.setText(uscID);
                        roleTextView.setText(role);

                        boolean lifeSubscribed = Boolean.TRUE.equals(documentSnapshot.getBoolean("lifeSubscription"));
                        lifeNotifications.setChecked(lifeSubscribed);

                        boolean academicSubscribed = Boolean.TRUE.equals(documentSnapshot.getBoolean("academicSubscription"));
                        academicNotifications.setChecked(academicSubscribed);

                        boolean eventSubscribed = Boolean.TRUE.equals(documentSnapshot.getBoolean("eventSubscription"));
                        eventNotifications.setChecked(eventSubscribed);

                        if (profilePicUrl != null) {
                            try {
                                Glide.with(this)
                                        .load(profilePicUrl)
                                        .override(100, 100)
                                        .placeholder(R.drawable.blank_profile_pic)
                                        .error(R.drawable.blank_profile_pic)
                                        .into(profileImageView);
                            } catch (Exception e) {
                                Log.e(TAG, "Out of memory error while loading image", e);
                                Toast.makeText(this, "Failed to load image. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(Profile.this, "User info not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving user info: ", e);
                    Toast.makeText(Profile.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                });
    }

    private void manageNotificationSettings(String uid) {
        lifeNotifications.setOnCheckedChangeListener((buttonView, on) -> db.collection("users").document(uid)
                .update("lifeSubscription", on)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Life notifications updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update life notifications", e)));

        academicNotifications.setOnCheckedChangeListener((buttonView, on) -> db.collection("users").document(uid)
                .update("academicSubscription", on)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Academic notifications updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update academic notifications", e)));

        eventNotifications.setOnCheckedChangeListener((buttonView, on) -> db.collection("users").document(uid)
                .update("eventSubscription", on)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event notifications updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update event notifications", e)));
    }
}