package com.example.usctreehole;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import com.example.usctreehole.Reply;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import java.util.Date;

import static org.junit.Assert.*;
public class ReplyUnitTest {

    @BeforeClass
    public static void mockLog() {
        MockedStatic<android.util.Log> mockedLog = Mockito.mockStatic(android.util.Log.class);

        // Mock Log.d behavior
        mockedLog.when(() -> android.util.Log.d(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
        mockedLog.when(() -> android.util.Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
    }

    private Reply reply;

    @Before
    public void setUp() {
        // Set up a Reply object for testing
        reply = new Reply(
                "testUid",
                "This is a test reply",
                Timestamp.now(),
                true,
                "AnonymousUser",
                true,
                "parentReplyId123"
        );
    }

    @Test
    public void testConstructorAndGetters() {
        // Test constructor and getter methods
        assertEquals("testUid", reply.getUid());
        assertEquals("This is a test reply", reply.getContent());
        assertTrue(reply.isAnonymous());
        assertEquals("AnonymousUser", reply.getAnonymous_name());
        assertTrue(reply.isNested());
        assertEquals("parentReplyId123", reply.getParent_reply_id());
        assertNotNull(reply.getTimestamp());
        assertNotNull(reply.getTimestampAsDate());
    }

    @Test
    public void testSetAndGetRid() {
        // Test setting and getting the reply ID
        reply.setRid("testRid123");
        assertEquals("testRid123", reply.getRid());
    }

    @Test
    public void testSetAndGetAnonymous() {
        // Test setting and getting the anonymous flag
        reply.setAnonymous(false);
        assertFalse(reply.isAnonymous());
        reply.setAnonymous(true);
        assertTrue(reply.isAnonymous());
    }

    @Test
    public void testSetAndGetAnonymousName() {
        // Test setting and getting the anonymous name
        reply.setAnonymous_name("NewAnonymousUser");
        assertEquals("NewAnonymousUser", reply.getAnonymous_name());
    }

    @Test
    public void testSetAndGetNested() {
        // Test setting and getting the nested flag
        reply.setNested(false);
        assertFalse(reply.isNested());
        reply.setNested(true);
        assertTrue(reply.isNested());
    }

    @Test
    public void testSetAndGetParentReplyId() {
        // Test setting and getting the parent reply ID
        reply.setParent_reply_id("newParentReplyId");
        assertEquals("newParentReplyId", reply.getParent_reply_id());
    }

    @Test
    public void testSetAndGetName() {
        // Test setting and getting the name (excluded field)
        reply.setName("Test User");
        assertEquals("Test User", reply.getName());
    }

    @Test
    public void testTimestampAsDate() {
        // Test conversion of timestamp to date
        Timestamp timestamp = Timestamp.now();
        reply = new Reply(
                "testUid",
                "This is a test reply",
                timestamp,
                true,
                "AnonymousUser",
                true,
                "parentReplyId123"
        );

        Date expectedDate = timestamp.toDate();
        assertEquals(expectedDate, reply.getTimestampAsDate());
    }
}
