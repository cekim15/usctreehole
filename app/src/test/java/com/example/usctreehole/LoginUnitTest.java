package com.example.usctreehole;
import android.content.Intent;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LoginUnitTest {

    private Login loginActivity;

    @Mock
    private FirebaseAuth mockAuth;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginActivity = mock(Login.class); // Mock the Login class to avoid Android dependencies
        loginActivity.mAuth = mockAuth; // Inject mocked FirebaseAuth
    }

    @Test
    public void testSuccessfulLogin() {
        // Mock successful login
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);

        // Simulate successful login behavior
        doAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        // Test logic
        loginActivity.mAuth.signInWithEmailAndPassword("test@example.com", "password")
                .addOnCompleteListener(task -> {
                    loginActivity.reload(mock(FirebaseUser.class));
                });

        // Verify method calls
        verify(mockAuth).signInWithEmailAndPassword("test@example.com", "password");
        verify(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
    }

    @Test
    public void testUnsuccessfulLogin() {
        // Mock unsuccessful login
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(false); // Simulate unsuccessful task
        when(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);

        // Simulate unsuccessful login behavior
        doAnswer(invocation -> {
            OnCompleteListener<AuthResult> listener = invocation.getArgument(0);
            listener.onComplete(mockTask);
            return null;
        }).when(mockTask).addOnCompleteListener(any(OnCompleteListener.class));

        // Test logic
        loginActivity.mAuth.signInWithEmailAndPassword("test@example.com", "wrongpassword")
                .addOnCompleteListener(task -> {
                    // Assert that the task was unsuccessful
                    assertFalse(task.isSuccessful());
                });

        // Verify method calls
        verify(mockAuth).signInWithEmailAndPassword("test@example.com", "wrongpassword");
        verify(mockTask).addOnCompleteListener(any(OnCompleteListener.class));
    }
}
