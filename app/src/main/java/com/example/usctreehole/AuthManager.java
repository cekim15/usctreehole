package com.example.usctreehole;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private final FirebaseAuth firebaseAuth;

    public AuthManager(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public Task<AuthResult> signIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
