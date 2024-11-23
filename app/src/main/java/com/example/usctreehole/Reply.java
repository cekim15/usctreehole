package com.example.usctreehole;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Reply {
    private String uid;
    @Exclude
    private String rid;
    private String content;
    private Timestamp timestamp;
    private boolean anonymous;
    private String anonymous_name;
    @Exclude
    private String name;
    private boolean nested_reply;
    private String parent_reply_id;

    public Reply() {}

    public Reply(String uid, String content, Timestamp timestamp, boolean anonymous, String anonymous_name, boolean nested_reply, String parent_reply_id) {
        this.uid = uid;
        this.content = content;
        this.timestamp = timestamp;
        this.anonymous = anonymous;
        this.anonymous_name = anonymous_name;
        this.nested_reply = nested_reply;
        this.parent_reply_id = parent_reply_id;
    }

    public String getUid() { return this.uid; }

    public void setRid(String rid) {
        this.rid = rid;
    }
    public String getRid() {return this.rid; }

    public String getContent() { return this.content; }

    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
    public boolean isAnonymous() { return this.anonymous; }

    public void setAnonymous_name(String anonymous_name) { this.anonymous_name = anonymous_name; }
    public String getAnonymous_name() { return this.anonymous_name; }

    @Exclude
    public void setName(String name) {this.name = name; }
    @Exclude
    public String getName() {return this.name; }

    public void setNested(boolean nested) { this.nested_reply = nested; }
    public boolean isNested() { return this.nested_reply; }

    public void setParent_reply_id(String pri) { this.parent_reply_id = pri; }
    public String getParent_reply_id() { return this.parent_reply_id; }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Exclude
    public Date getTimestampAsDate() {
        return timestamp != null ? timestamp.toDate() : null;
    }
}
