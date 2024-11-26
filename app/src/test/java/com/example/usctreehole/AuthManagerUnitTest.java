package com.example.usctreehole;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuthManagerUnitTest {

    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Mock
    private Task<AuthResult> mockTask;

    @Mock
    private FirebaseUser mockFirebaseUser;

    private AuthManager authManager;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authManager = new AuthManager(mockFirebaseAuth);
    }

    @Test
    public void testSignIn() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        when(mockFirebaseAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask);

        // Act
        Task<AuthResult> result = authManager.signIn(email, password);

        // Assert
        assertNotNull(result);
        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password);
    }

    @Test
    public void testGetCurrentUser() {
        // Arrange
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);

        // Act
        FirebaseUser currentUser = authManager.getCurrentUser();

        // Assert
        assertNotNull(currentUser);
        verify(mockFirebaseAuth).getCurrentUser();
    }
}

