package com.example.usctreehole;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.firebase.Timestamp;

import org.junit.Test;

import java.util.Date;

public class PostBlackBoxTest {

    @Test
    public void testPostConstructorAndGetters() {
        // Arrange
        String uid = "user123";
        String title = "Sample Post";
        String content = "This is a test post.";
        Timestamp timestamp = Timestamp.now();

        // Act
        Post post = new Post(uid, title, content, timestamp);

        // Assert
        assertEquals("UID should match", uid, post.getUid());
        assertEquals("Title should match", title, post.getTitle());
        assertEquals("Content should match", content, post.getContent());
        assertEquals("Timestamp should match", timestamp, post.getTimestamp());
    }

    @Test
    public void testPidSetAndGet() {
        // Arrange
        Post post = new Post();
        String pid = "post456";

        // Act
        post.setPid(pid);

        // Assert
        assertEquals("PID should match", pid, post.getPid());
    }

    @Test
    public void testTimestampAsDate() {
        // Arrange
        Timestamp timestamp = Timestamp.now();
        Post post = new Post("user123", "Sample Post", "This is a test post.", timestamp);

        // Act
        Date date = post.getTimestampAsDate();

        // Assert
        assertNotNull("Date should not be null", date);
        assertEquals("Date should match the timestamp", timestamp.toDate(), date);
    }
}
