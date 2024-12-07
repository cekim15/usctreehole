package com.example.usctreehole;
import static org.mockito.Mockito.*;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

public class FetchPostsTest {

    private FirebaseFirestore mockFirestore;
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        // Mock Firestore
        mockFirestore = mock(FirebaseFirestore.class);

        // Mocked instance of MainActivity
        mainActivity = new MainActivity() {
            @Override
            public void Log(String tag, String message) {
                System.out.println(tag + ": " + message); // Log output to console for manual verification
            }
        };

        // Inject Firestore mock
        mainActivity.db = mockFirestore;
    }

    /*@Test
    public void testViewingNullLogsCorrectMessage() {
        // Set viewing to null
        mainActivity.viewing = null;

        // Call fetchPosts
        mainActivity.fetchPosts();

        // Manual verification via console
        System.out.println("Check console output for: \"can't fetch, viewing null\"");
    } */
}
