package com.example.usctreehole;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    public interface ReplyingToReplyListener {
        void onReplyToReply(Reply reply);
    }

    private static final String TAG = "ReplyAdapter";
    private final List<Reply> replies;
    private final Context context;
    private final String collection;
    private final String pid;
    private final ReplyingToReplyListener rtrListener;
    private FirebaseFirestore db;

    public ReplyAdapter(List<Reply> replies, Context context, String collection, String pid, ReplyingToReplyListener listener) {
        this.replies = replies;
        this.context = context;
        this.collection = collection;
        this.pid = pid;
        this.rtrListener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Reply reply = replies.get(position);
        holder.timestamp.setText(String.valueOf(reply.getTimestampAsDate()));

        holder.reply_to_reply.setOnClickListener(v -> {
            Log.d(TAG, "Replying to reply");
            rtrListener.onReplyToReply(reply);
        });

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (reply.isNested()) {
            Log.d(TAG, "loading nested reply with content " + reply.getContent());
            db.collection(collection)
                    .document(pid)
                    .collection("replies")
                    .document(reply.getParent_reply_id())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot parent_reply = task.getResult();
                            if (parent_reply.exists()) {
                                Reply parent = parent_reply.toObject(Reply.class);
                                String parentContent = parent.getContent();

                                if (parent.isAnonymous()) {
                                    String newContent = "[ Replying to " + parent.getAnonymous_name() + ": \"" + parentContent + "\" ]: " + reply.getContent();
                                    holder.content.setText(newContent);
                                }
                                else {
                                    String parent_uid = parent.getUid();
                                    db.collection("users")
                                            .document(parent_uid)
                                            .get()
                                            .addOnCompleteListener(parent_nametask -> {
                                                if (parent_nametask.isSuccessful()) {
                                                    DocumentSnapshot document = parent_nametask.getResult();
                                                    if (document != null && document.exists()) {
                                                        String parent_name = document.getString("name");
                                                        String newContent = "[ Replying to " + parent_name + ": \"" + parentContent + "\" ]: " + reply.getContent();
                                                        holder.content.setText(newContent);
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.e(TAG, "Error fetching parent reply", task.getException());
                        }
                    });
        } else {
            holder.content.setText(reply.getContent());
        }

        if (!reply.isAnonymous()) {
            db.collection("users")
                    .document(reply.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String name = document.getString("name");
                                holder.author.setText(name);
                                reply.setName(name);

                                String pfpUrl = document.getString("profilePicUrl");
                                if (pfpUrl != null) {
                                    Glide.with(context)
                                            .load(pfpUrl)
                                            .placeholder(R.drawable.blank_profile_pic)
                                            .override(40, 40)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .error(R.drawable.blank_profile_pic)
                                            .into(holder.pfp);
                                    Log.d("ReplyAdapter", "loaded pfp");
                                } else {
                                    holder.pfp.setImageResource(R.drawable.blank_profile_pic);
                                    Log.d("ReplyAdapter", "couldn't load pfp");
                                }
                            }
                        }
                    });
        } else {
            holder.author.setText(reply.getAnonymous_name());
        }
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "number of replies: " + replies.size());
        return replies.size();
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView pfp, reply_to_reply;
        TextView author, content, timestamp;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            pfp = itemView.findViewById(R.id.replier_pfp);
            author = itemView.findViewById(R.id.reply_author);
            content = itemView.findViewById(R.id.reply_content);
            timestamp = itemView.findViewById(R.id.reply_timestamp);
            reply_to_reply = itemView.findViewById(R.id.reply_to_reply);
        }
    }
}