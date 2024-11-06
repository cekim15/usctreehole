package com.example.usctreehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewReplies extends AppCompatActivity implements ReplyAdapter.ReplyingToReplyListener {
    private static final String TAG = "ViewReplies";
    private DrawerLayout dl;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String collection;
    private String postID;
    private TextView title, author, content, timestamp;
    private RecyclerView rrv;
    private ReplyAdapter replyAdapter;
    private List<Reply> replies = new ArrayList<>();
    private boolean rtr;
    private Reply parentReply;
    private EditText replyEditText;
    private boolean anonymous;
    private SwitchCompat anonymous_toggle;
    private String uid;
    private String anonymous_name;
    private AnonymousNameGenerator name_generator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_replies);
        setUpToolbar();
//
//        ImageView notifications = findViewById(R.id.notification_bell);
//        notifications.setOnClickListener(v -> openNotifications());

        ImageView notifications = findViewById(R.id.notification_bell);
        notifications.setOnClickListener(v -> {
            // Open right-side menu (notification drawer)
            dl.openDrawer(GravityCompat.END);
        });

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        postID = getIntent().getStringExtra("postID");
        collection = getIntent().getStringExtra("collection");

        title = findViewById(R.id.post_title);
        author = findViewById(R.id.post_author);
        content = findViewById(R.id.post_content);
        timestamp = findViewById(R.id.post_timestamp);
        rtr = false;

        Log.d(TAG, collection);
        displayPost();

        anonymous_toggle = findViewById(R.id.anonymous_reply_toggle);
        anonymous_toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            anonymous = isChecked;
        });
        name_generator = new AnonymousNameGenerator();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent loginIntent = new Intent(ViewReplies.this, Login.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            uid = currentUser.getUid();
        }

        rrv = findViewById(R.id.replies_recycler_view);
        rrv.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(replies, this, collection, postID, this);
        rrv.setAdapter(replyAdapter);
        fetchReplies();

        replyEditText = findViewById(R.id.reply_edit_text);

        ImageView post_reply = findViewById(R.id.reply_to_post);
        post_reply.setOnClickListener(v -> {
            String newHint = "Replying to post...";
            replyEditText.setHint(newHint);
            replyEditText.requestFocus();
            rtr = false;
        });

        TextView back = findViewById(R.id.back_to_posts);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(ViewReplies.this, MainActivity.class);
            intent.putExtra("collection", collection);
            Log.d(TAG, collection);
            startActivity(intent);
            finish();
        });

        ImageButton send_reply = findViewById(R.id.send_reply_button);
        send_reply.setOnClickListener(view -> {
            createReply();
        });
    }

    private void fetchReplies() {
        Log.d(TAG, "fetching replies in " + collection + " for post " + postID);
        repliedBefore();
        db.collection(collection)
                .document(postID)
                .collection("replies")
                .orderBy("timestamp", Query.Direction.ASCENDING) // i think it's more intuitive for replies to be oldest to newest
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        replies.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reply reply = document.toObject(Reply.class);
                            String rid = document.getId();
                            reply.setRid(rid);
                            if (reply.isAnonymous()) {
                                String used_anon_name = reply.getAnonymous_name();
                                name_generator.removeUsedName(used_anon_name);
                                Log.d(TAG, "Removing used anonymous name " + used_anon_name + " from generator");
                            }
                            replies.add(reply);
                            Log.d(TAG, "Loading reply: " + reply.getRid() + " for which nested is " + reply.isNested());
                        }
                        replyAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting replies.", task.getException());
                        Toast.makeText(ViewReplies.this, "Failed to load replies", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onReplyToReply(Reply reply) {
        Log.d(TAG, "Replying to reply with ID: " + reply.getRid());
        String newHint = "Replying to ";
        if (reply.isAnonymous()) {
            newHint += reply.getAnonymous_name() + "...";
        }
        else {
            newHint += reply.getName() + "...";
        }
        replyEditText.setHint(newHint);
        replyEditText.requestFocus();
        rtr = true;
        parentReply = reply;
    }

    private void createReply() {
        db.collection(collection).document(postID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String content = ((EditText) findViewById(R.id.reply_edit_text)).getText().toString();
                        Timestamp timestamp = Timestamp.now();
                        String parent_reply_id = "";
                        if (rtr) {
                            parent_reply_id = parentReply.getRid();
                        }
                        if (anonymous && anonymous_name.isEmpty()) {
                            anonymous_name = name_generator.generateRandomAnonymousName();
                        }
                        Reply reply = new Reply(uid, content, timestamp, anonymous, anonymous_name, rtr, parent_reply_id);

                        db.collection(collection)
                                .document(postID)
                                .collection("replies")
                                .add(reply)
                                .addOnSuccessListener(documentReference -> {
                                    reply.setRid(documentReference.getId());
                                    documentReference.update("rid", documentReference.getId());
                                    Toast.makeText(this, "Reply posted successfully", Toast.LENGTH_SHORT).show();
                                    ((EditText) findViewById(R.id.reply_edit_text)).setText("");
                                    fetchReplies();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "Could not add reply");
                                    Toast.makeText(this, "Error adding reply: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error: ", e));
    }

    private void displayPost() {
        db.collection(collection).document(postID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Post post = documentSnapshot.toObject(Post.class);
                        title.setText(post.getTitle());
                        db.collection("users")
                                .document(post.getUid())
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            String name = document.getString("name");
                                            author.setText(name);
                                        }
                                    }
                                });
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

    private void repliedBefore() {
        db.collection(collection)
                .document(postID)
                .collection("replies")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Reply prev_reply = document.toObject(Reply.class);
                            handleRepliedBefore(prev_reply.isAnonymous());
                            anonymous_name = prev_reply.getAnonymous_name();
                        }
                        else {
                            anonymous_name = "";
                        }
                    }
                });
    }

    private void handleRepliedBefore(boolean anonymous_previously) {
        TextView anonymous_reply_label = findViewById(R.id.anonymous_reply_label);
        if (anonymous_previously) {
            anonymous_toggle.setChecked(true);
            String established_anon = "You have chosen to remain anonymous under this post.";
            anonymous_reply_label.setText(established_anon);
        } else {
            anonymous_toggle.setChecked(false);
            String established_not_anon = "You have chosen not to remain anonymous under this post.";
            anonymous_reply_label.setText(established_not_anon);
        }
        anonymous_toggle.setEnabled(false);
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

    private void openNotifications() {
        // open notifications screen?
        Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
    }
}