package com.example.usctreehole;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.google.firebase.Timestamp;
import java.util.Date;

public class PostTestJUnit {
    private Post post;
    private final String uid = "user123";
    private final String title = "Test Title";
    private final String content = "Test content";
    private Timestamp timestamp;

    @Before
    public void setUp() {
        timestamp = Timestamp.now();
        post = new Post(uid, title, content, timestamp);
    }

    @Test
    public void testConstructorAndGetters() {
        assertEquals("UID should match", uid, post.getUid());
        assertEquals("Title should match", title, post.getTitle());
        assertEquals("Content should match", content, post.getContent());
        assertEquals("Timestamp should match", timestamp, post.getTimestamp());
    }

    @Test
    public void testSetPidAndGetPid() {
        String pid = "post123";
        post.setPid(pid);
        assertEquals("PID should match", pid, post.getPid());
    }

    @Test
    public void testGetTimestampAsDate() {
        Date timestampAsDate = post.getTimestampAsDate();
        assertNotNull("Timestamp as Date should not be null", timestampAsDate);
        assertEquals("Converted timestamp should match the original", timestamp.toDate(), timestampAsDate);
    }

    @Test
    public void testGetTimestampAsDateWhenNull() {
        Post postWithoutTimestamp = new Post(uid, title, content, null);
        assertNull("Timestamp as Date should be null when timestamp is null", postWithoutTimestamp.getTimestampAsDate());
    }
}
