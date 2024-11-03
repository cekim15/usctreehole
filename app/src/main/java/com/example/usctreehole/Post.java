package com.example.usctreehole;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Post {
    private String uid;
    @Exclude
    private String pid;
    private String uname;
    private String title;
    private String content;
    private Timestamp timestamp;

    public Post() {}

    public Post(String uid, String uname, String title, String content, Timestamp timestamp) {
        this.uid = uid;
        this.uname = uname;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Exclude
    public void setPid(String pid) {
        this.pid = pid;
    }
    @Exclude
    public String getPid() {
        return pid;
    }

    public String getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Exclude
    public Date getTimestampAsDate() {
        return timestamp != null ? timestamp.toDate() : null;
    }
}
