package com.example.usctreehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreatePost extends AppCompatActivity {

    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "CreatePost";
    private String collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(CreatePost.this, Login.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        setContentView(R.layout.activity_create_post);

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
                Intent intent = new Intent(CreatePost.this, Profile.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                Intent intent = new Intent(CreatePost.this, Login.class);
                startActivity(intent);
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(CreatePost.this, MainActivity.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> openNotifications());

        Button cancelPost = findViewById(R.id.cancel_post);
        cancelPost.setOnClickListener(view -> {
            Intent intent = new Intent(CreatePost.this, MainActivity.class);
            startActivity(intent);
        });

        Button submitPost = findViewById(R.id.submit_post);
        submitPost.setOnClickListener(view -> {
            String title = ((EditText) findViewById(R.id.enterPostTitle)).getText().toString();
            String content = ((EditText) findViewById(R.id.enterPostContent)).getText().toString();
            Spinner categorySelect = findViewById(R.id.categorySelect);
            String category = categorySelect.getSelectedItem().toString();

            if (category.equals("Life")) {
                collection = "lifePosts";
            } else if (category.equals("Academic")) {
                collection = "academicPosts";
            } else {
                collection = "eventPosts";
            }

            writePostToDatabase(title, content);
        });
    }

    private void writePostToDatabase(String title, String content) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(CreatePost.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            String uid = currentUser.getUid();

            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String uname = documentSnapshot.getString("name");

                            Timestamp timestamp = Timestamp.now();
                            Post post = new Post(uid, uname, title, content, timestamp);

                            db.collection(collection)
                                    .add(post)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CreatePost.this, MainActivity.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d(TAG, "Could not add post");
                                        Toast.makeText(this, "Error adding post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error getting user name: ", e));
        }
    }

    private void openNotifications() {
        // open notifications screen?
        Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
    }
}