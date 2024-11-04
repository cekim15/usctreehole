package com.example.usctreehole;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private static final String TAG = "PostAdapter";
    private final List<Post> posts;
    private final Context context;
    private final String collection;
    private FirebaseFirestore db;

    public PostAdapter(List<Post> posts, Context context, String collection) {
        this.posts = posts;
        this.context = context;
        this.collection = collection;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.title.setText(post.getTitle());

        db.collection("users")
                .document(post.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String name = document.getString("name");
                            holder.author.setText(name);
                        }
                    }
                });

        holder.content.setText(post.getContent());
        holder.timestamp.setText(String.valueOf(post.getTimestampAsDate()));

        holder.view_replies.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewReplies.class);
            intent.putExtra("postID", post.getPid());
            intent.putExtra("collection", collection);
            Log.d(TAG, collection);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "number of posts: " + posts.size());
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, content, timestamp, view_replies;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
            author = itemView.findViewById(R.id.post_author);
            content = itemView.findViewById(R.id.post_content);
            timestamp = itemView.findViewById(R.id.post_timestamp);
            view_replies = itemView.findViewById(R.id.post_view_replies);
        }
    }
}