package com.example.usctreehole;

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
    FirebaseFirestore db;
    private RecyclerView rv;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    private static final String TAG = "MainActivity";
    String viewing;
    private TabLayout categoryTabs;
    boolean lifePosts = false;
    boolean eventPosts = false;
    boolean academicPosts = false;
    List<Post> notificationPosts = new ArrayList<Post>();

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseFirestore getdb() {
        return db;
    }

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
        getViewing();
        setUpTabs();
        Log.d(TAG, "On create viewing is: " + viewing);

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> {
            fetchNotifications();
            dl.openDrawer(GravityCompat.END);
        });

        rv = findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(new ArrayList<>(), this, viewing);
        rv.setAdapter(postAdapter);

        FloatingActionButton createPost = findViewById(R.id.create_post);
        createPost.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreatePost.class);
            intent.putExtra("viewing", viewing);
            startActivity(intent);
        });
    }

    public void fetchPosts() {
        if (viewing != null) {
            Log.d(TAG, "fetching posts in " + viewing);
        }
        else {
            Log.d(TAG, "can't fetch, viewing null");
        }
        db.collection(viewing)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "finished fetching");
                    if (task.isSuccessful()) {
                        posts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = document.toObject(Post.class);
                            String pid = document.getId();
                            post.setPid(pid);
                            Log.d(TAG, pid);
                            posts.add(post);
                            Log.d(TAG, "Adding " + viewing + " post: " + post.getTitle());
                        }
                        postAdapter = new PostAdapter(posts, this, viewing);
                        rv.setAdapter(postAdapter);
                    } else {
                        Log.w(TAG, "Error getting posts.", task.getException());
                        Toast.makeText(MainActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void getViewing() {
        Log.d(TAG, "Called selected category");
        viewing = "lifePosts";
        Intent coming_from = getIntent();
        String viewed_post_in = coming_from.getStringExtra("collection");
        if (viewed_post_in != null) {
            viewing = viewed_post_in;
        }
        String just_posted_to = coming_from.getStringExtra("category");
        if (just_posted_to != null) {
            Log.d(TAG, just_posted_to);
            if (just_posted_to.equals("Academic")) {
                viewing = "academicPosts";
            } else if (just_posted_to.equals("Event")) {
                viewing = "eventPosts";
            }
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "calling on resume");
        super.onResume();
        Log.d(TAG, "calling fetch posts from on resume");
        fetchPosts();
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

    private void setUpTabs() {
        categoryTabs = findViewById(R.id.change_category);

        categoryTabs.addTab(categoryTabs.newTab().setText("Life"));
        categoryTabs.addTab(categoryTabs.newTab().setText("Academic"));
        categoryTabs.addTab(categoryTabs.newTab().setText("Event"));

        categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewing = "lifePosts";
                        break;
                    case 1:
                        viewing = "academicPosts";
                        break;
                    case 2:
                        viewing = "eventPosts";
                        break;
                }
                Log.d(TAG, "calling fetch posts from setuptabs");
                fetchPosts();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (viewing.equals("lifePosts")) {
            categoryTabs.selectTab(categoryTabs.getTabAt(0));
        } else if (viewing.equals("academicPosts")) {
            categoryTabs.selectTab(categoryTabs.getTabAt(1));
        } else if (viewing.equals("eventPosts")) {
            categoryTabs.selectTab(categoryTabs.getTabAt(2));
        }
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

    public void Log(String tag, String message) {
//        does not do anything since it is overridden
        return;
    }
}
