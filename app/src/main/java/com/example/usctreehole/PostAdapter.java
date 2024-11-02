package com.example.usctreehole;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final List<Post> posts;
    private static final String TAG = "PostAdapter";

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
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
        holder.author.setText(post.getUname());
        holder.content.setText(post.getContent());
        holder.timestamp.setText(String.valueOf(post.getTimestampAsDate()));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "number of posts: " + posts.size());
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, content, timestamp;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.post_title);
            author = itemView.findViewById(R.id.post_author);
            content = itemView.findViewById(R.id.post_content);
            timestamp = itemView.findViewById(R.id.post_timestamp);
        }
    }
}