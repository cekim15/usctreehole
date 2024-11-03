package com.example.usctreehole;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    public interface ReplyingToReplyListener {
        void onReplyToReply(Reply reply);
    }

    private static final String TAG = "PostAdapter";
    private final List<Reply> replies;
    private final Context context;
    private final String pid;
    private final ReplyingToReplyListener rtrListener;

    public ReplyAdapter(List<Reply> replies, Context context, String pid, ReplyingToReplyListener listener) {
        this.replies = replies;
        this.context = context;
        this.pid = pid;
        this.rtrListener = listener;
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
        holder.content.setText(reply.getContent());
        holder.timestamp.setText(String.valueOf(reply.getTimestampAsDate()));

        holder.reply_to_reply.setOnClickListener(v -> {
            Log.d(TAG, "Replying to reply");
            rtrListener.onReplyToReply(reply);
        });

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        if (reply.isNested()) {
            int margin_dp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
            layoutParams.setMarginStart(margin_dp);
            holder.itemView.setLayoutParams(layoutParams);
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