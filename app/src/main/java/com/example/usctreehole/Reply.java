package com.example.usctreehole;

import com.google.firebase.Timestamp;

public class Reply {
    private String uid;
    private String pid;
    private String rid;
    private String author;
    private String profileUrl;
    private String content;
    private Timestamp timestamp;
    private boolean anonymous;

    public Reply() {}

    public Reply(String content, String author, String profileUrl, Timestamp timestamp) {
        this.content = content;
        this.author = author;
        this.profileUrl = profileUrl;
        this.timestamp = timestamp;
    }
}
