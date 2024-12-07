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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CreatePost extends AppCompatActivity {

    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "CreatePost";
    private String collection;
    private Spinner categorySelect;
    private String category;
    private static final String CHANNEL_ID = "post_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

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
        setUpToolbar();

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> {
            fetchNotifications();
            dl.openDrawer(GravityCompat.END);
        });

        Intent fromMain = getIntent();
        String viewing = fromMain.getStringExtra("viewing");
        categorySelect = findViewById(R.id.categorySelect);
        if (viewing.equals("academicPosts")) {
            categorySelect.setSelection(1);
        } else if (viewing.equals("eventPosts")) {
            categorySelect.setSelection(2);
        }

        Button cancelPost = findViewById(R.id.cancel_post);
        cancelPost.setOnClickListener(view -> {
            Intent intent = new Intent(CreatePost.this, MainActivity.class);
            category = categorySelect.getSelectedItem().toString();
            intent.putExtra("category", category);
            startActivity(intent);
        });

        Button submitPost = findViewById(R.id.submit_post);
        submitPost.setOnClickListener(view -> {
            String title = ((EditText) findViewById(R.id.enterPostTitle)).getText().toString();
            String content = ((EditText) findViewById(R.id.enterPostContent)).getText().toString();
            categorySelect = findViewById(R.id.categorySelect);
            category = categorySelect.getSelectedItem().toString();

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
            Timestamp timestamp = Timestamp.now();
            Post post = new Post(uid, title, content, timestamp);

            db.collection(collection)
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show();
                        notifySubscribedUsers(category, title);

                        Intent intent = new Intent(CreatePost.this, MainActivity.class);
                        intent.putExtra("category", category);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "Could not add post");
                        Toast.makeText(this, "Error adding post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void notifySubscribedUsers(String category, String post_title) {
        String subscriptionField = "";
        if (category.equals("Life")) {
            subscriptionField = "lifeSubscription";
        } else if (category.equals("Academic")) {
            subscriptionField = "academicSubscription";
        } else {
            subscriptionField = "eventSubscription";
        }
        db.collection("users")
                .whereEqualTo(subscriptionField, true)
                .get()
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            String subscriber_uid = document.getId();
                            String subscriber_name = document.getString("name");
                            Log.d(TAG, "notifying user: " + subscriber_name);

                            sendNotification(subscriber_uid, post_title);
                        }
                    } else {
                        Log.w(TAG, "error fetching subscribers", task.getException());
                    }
                });
        }

    private void sendNotification(String subscriber_uid, String title) {
        String message = "New post: " + title;
        Timestamp timestamp = Timestamp.now();
        Notification notification = new Notification(message, timestamp);
        db.collection("users")
                .document(subscriber_uid)
                .collection("notifications")
                .add(notification)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification added for user: " + subscriber_uid);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding notification for user: " + subscriber_uid, e);
                });
    }

    private void fetchNotifications() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Notification notification = document.toObject(Notification.class);
                            notifications.add(notification);
                        }
                        updateNotificationRecyclerView(notifications);
                    } else {
                        Log.w(TAG, "Error getting notifications.", task.getException());
                    }
                });
    }

    private void updateNotificationRecyclerView(List<Notification> notifications) {
        RecyclerView notificationRecyclerView = findViewById(R.id.notification_recycler_view);
        NotificationAdapter adapter = new NotificationAdapter(notifications, this);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationRecyclerView.setAdapter(adapter);
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

        NavigationView notification = findViewById(R.id.notification_menu);
        ActionBarDrawerToggle notificationToggle = new ActionBarDrawerToggle(
                this, dl, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        dl.addDrawerListener(notificationToggle);
        toggle.syncState();

        notification.setNavigationItemSelectedListener(item -> {
            return true;
        });
    }
}