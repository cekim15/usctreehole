package com.example.usctreehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewReplies extends AppCompatActivity {
    private static final String TAG = "ViewReplies";
    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String collection;
    private String postID;
    private TextView title, author, content, timestamp;
    private RecyclerView repliesRecyclerView;
    //private ReplyAdapter replyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_replies);
        setUpToolbar();

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        postID = getIntent().getStringExtra("postID");
        collection = getIntent().getStringExtra("collection");

        title = findViewById(R.id.post_title);
        author = findViewById(R.id.post_author);
        content = findViewById(R.id.post_content);
        timestamp = findViewById(R.id.post_timestamp);

        Log.d(TAG, collection);
        displayPost();
    }

    private void displayPost() {
        db.collection(collection).document(postID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Post post = documentSnapshot.toObject(Post.class);
                        title.setText(post.getTitle());
                        author.setText(post.getUname());
                        content.setText(post.getContent());
                        timestamp.setText(String.valueOf(post.getTimestampAsDate()));
                    } else {
                        Toast.makeText(this, "Post does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching post details", e);
                    Toast.makeText(this, "Failed to load post details", Toast.LENGTH_SHORT).show();
                });
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
                Intent intent = new Intent(ViewReplies.this, Profile.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                Intent intent = new Intent(ViewReplies.this, Login.class);
                startActivity(intent);
            } else if (id == R.id.nav_home) {
                Intent intent = new Intent(ViewReplies.this, MainActivity.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}