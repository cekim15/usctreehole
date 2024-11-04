package com.example.usctreehole;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.activity.result.ActivityResultLauncher;
import android.Manifest;
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
        notifications.setOnClickListener(v -> openNotifications());

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
    }

    private void openNotifications() {
        // open notifications screen?
        Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
    }



    //Post Notifications
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void showPostNotification(String textTitle, String textContent){
        String CHANNEL_ID = "post_notification_channel";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_bell)
                .setContentTitle("New Post Created")
                .setContentText("A new post was added to your feed")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
       ) != PackageManager.PERMISSION_GRANTED
   ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                        int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE
            );

                return;
            }
            // notificationId is a unique int for each notification that you must define.
            notificationManager.notify(NOTIFICATION_ID, builder.build());


    }

}