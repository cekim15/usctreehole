package com.example.usctreehole;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.google.firebase.Timestamp;
import java.util.Date;

public class ReplyTestJUnit {

    private Reply reply;
    private Timestamp timestamp;

    @Before
    public void setUp() {
        timestamp = new Timestamp(new Date());
        reply = new Reply("user123", "This is a reply", timestamp, true, "AnonymousUser", true, "parent123");
    }

    @Test
    public void testReplyConstructor() {
        assertEquals("user123", reply.getUid());
        assertEquals("This is a reply", reply.getContent());
        assertEquals(timestamp, reply.getTimestamp());
        assertTrue(reply.isAnonymous());
        assertEquals("AnonymousUser", reply.getAnonymous_name());
        assertTrue(reply.isNested());
        assertEquals("parent123", reply.getParent_reply_id());
    }

    @Test
    public void testSetRid() {
        reply.setRid("reply123");
        assertEquals("reply123", reply.getRid());
    }

    @Test
    public void testGetTimestampAsDate() {
        Date timestampAsDate = reply.getTimestampAsDate();
        assertNotNull(timestampAsDate);
        assertEquals(timestamp.toDate(), timestampAsDate);
    }

    @Test
    public void testIsAnonymous() {
        reply.setAnonymous(false);
        assertFalse(reply.isAnonymous());
    }

    @Test
    public void testSetName() {
        reply.setName("John Doe");
        assertEquals("John Doe", reply.getName());
    }

    @Test
    public void testSetNested() {
        reply.setNested(false);
        assertFalse(reply.isNested());
    }

    @Test
    public void testSetParentReplyId() {
        reply.setParent_reply_id("parentReplyId");
        assertEquals("parentReplyId", reply.getParent_reply_id());
    }

    @Test
    public void testAnonymousName() {
        reply.setAnonymous_name("AnonymousUser2");
        assertEquals("AnonymousUser2", reply.getAnonymous_name());
    }

    @Test
    public void testGetTimestampAsDateNull() {
        // Create a reply object with a null timestamp
        Reply replyWithNullTimestamp = new Reply("user123", "Test reply", null, true, "AnonymousUser", true, "parent123");

        // Get the timestamp as Date
        Date timestampAsDate = replyWithNullTimestamp.getTimestampAsDate();

        // Assert that the timestampAsDate is null
        assertNull(timestampAsDate);
    }

    @Test
    public void testAnonymousNameWhenNotAnonymous() {
        // Set 'anonymous' to false, but set 'anonymous_name' to a value
        reply.setAnonymous(false);
        reply.setAnonymous_name("John Doe");

        // Check that 'anonymous_name' is still set correctly
        assertEquals("John Doe", reply.getAnonymous_name());
    }

    @Test
    public void testSetNestedReply() {
        // Set nested_reply to true and check isNested()
        reply.setNested(true);
        assertTrue(reply.isNested());

        // Set nested_reply to false and check isNested()
        reply.setNested(false);
        assertFalse(reply.isNested());
    }
}
