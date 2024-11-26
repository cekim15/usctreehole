package com.example.usctreehole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private DrawerLayout dl;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authManager = new AuthManager(FirebaseAuth.getInstance());
        //mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        dl = findViewById(R.id.drawer_layout);
        NavigationView nav = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, dl, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_signup) {
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
            dl.closeDrawer(GravityCompat.START);
            return true;
        });

        View loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.enterEmail)).getText().toString();
            String password = ((EditText) findViewById(R.id.enterPassword)).getText().toString();

            authManager.signIn(email, password).addOnCompleteListener(Login.this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = authManager.getCurrentUser();
                    reload(user);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Login.this, "Incorrect username or password.",
                            Toast.LENGTH_SHORT).show();
                    reload(null);
                }
            });
        });
    }

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = authManager.getCurrentUser();
        if (currentUser != null) {
            reload(currentUser);
        }
    }

    void reload(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }
}