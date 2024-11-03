package com.example.usctreehole;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView rv;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private String viewing;
    private TabLayout categoryTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, Login.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpTabs();

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> openNotifications());

        rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(posts, this, viewing);
        rv.setAdapter(postAdapter);

        fetchPosts();

        FloatingActionButton createPost = findViewById(R.id.create_post);
        createPost.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreatePost.class);
            startActivity(intent);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchPosts() {
        db.collection(viewing)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        posts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            String pid = document.getId();
                            post.setPid(pid);
                            Log.d(TAG, pid);
                            posts.add(post);
                            Log.d(TAG, "Adding post: " + post.getTitle());
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting posts.", task.getException());
                        Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                    }
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
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setUpTabs() {
        categoryTabs = findViewById(R.id.change_category);
        categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewing = "lifePosts"; // Update viewing variable
                        break;
                    case 1:
                        viewing = "academicPosts"; // Update viewing variable
                        break;
                    case 2:
                        viewing = "eventPosts"; // Update viewing variable
                        break;
                }
                fetchPosts(); // Fetch posts based on the updated viewing variable
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        categoryTabs.addTab(categoryTabs.newTab().setText("Life"));
        categoryTabs.addTab(categoryTabs.newTab().setText("Academic"));
        categoryTabs.addTab(categoryTabs.newTab().setText("Event"));
    }

    private void openNotifications() {
        // open notifications screen?
        Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
    }
}
