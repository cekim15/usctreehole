package com.example.usctreehole;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class MainActivityTest {

    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private Intent mockIntent;

    private MainActivity mainActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mainActivity = new MainActivity();
        FirebaseAuth auth = mainActivity.getAuth();
        FirebaseFirestore db = mainActivity.getdb();

        // Set up a mock current user
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockIntent.getStringExtra("collection")).thenReturn("academicPosts");
        when(mockIntent.getStringExtra("category")).thenReturn("Event");

        // Mock other necessary components if required
    }

    @Test
    public void testGetViewing() {
        // Mock intent data and call the method
        mainActivity.getViewing();

        // Verify the viewing logic
        assertEquals("eventPosts", mainActivity.getViewing());
    }

    @Test
    public void testFetchPosts() {
        // Initialize necessary mocks
        when(mockFirestore.collection(anyString())).thenReturn(mock(FirebaseFirestore.CollectionReference.class));

        // Call the fetchPosts method
        mainActivity.fetchPosts();

        // Assertions or verifications for fetchPosts
        // For example, verify if the correct collection is fetched
        verify(mockFirestore).collection("lifePosts");
    }

    @Test
    public void testFetchNotificationPosts_NoUserLoggedIn() {
        // Mock the user as null
        when(mockAuth.getCurrentUser()).thenReturn(null);

        // Call the method
        mainActivity.fetchNotificationPosts();

        // Assert that the Toast message is shown or a warning log is called
        // Note: Toasts or logs require additional setups to verify
    }

    @Test
    public void testSetUpTabs() {
        // Call the method
        mainActivity.setUpTabs();

        // Verify the tabs are correctly initialized
        // This may involve checking internal state or mock interactions
        assertNotNull(mainActivity.categoryTabs);
        assertEquals(3, mainActivity.categoryTabs.getTabCount());
    }

    @Test
    public void testUpdateNotificationRecyclerView() {
        // Mock a RecyclerView with an adapter
        RecyclerView mockRecyclerView = mock(RecyclerView.class);
        RecyclerView.Adapter mockAdapter = mock(RecyclerView.Adapter.class);

        // Set the mocked adapter to the RecyclerView
        when(mockRecyclerView.getAdapter()).thenReturn(mockAdapter);
        mainActivity.rv = mockRecyclerView;

        // Call the method
        mainActivity.updateNotificationRecyclerView();

        // Verify that notifyDataSetChanged was called
        verify(mockAdapter).notifyDataSetChanged();
    }

    @Test
    public void testHandleSubscriptions() {
        // Call the method with mock subscription flags
        mainActivity.handleSubscriptions(true, false, true);

        // Assert that the internal state is updated correctly
        assertTrue(mainActivity.academicPosts);
        assertFalse(mainActivity.eventPosts);
        assertTrue(mainActivity.lifePosts);
    }

    @Test
    public void testLifecycle_OnResume() {
        // Call the onResume method
        mainActivity.onResume();

        // Verify that fetchPosts is called
        // This requires mocking internal dependencies or spying on the activity
    }
}
