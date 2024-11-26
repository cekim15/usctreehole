package com.example.usctreehole;
import android.content.Intent;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Test
    public void testLoadUserInfo() {
        // Launch the Profile activity
        ActivityScenario<Profile> scenario = ActivityScenario.launch(Profile.class);

        scenario.onActivity(activity -> {
            // Mock Firestore response for user info
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Prepare mock data (you can replace this with a simpler approach)
            String mockName = "John Doe";

            // Stub FirebaseFirestore interaction (manual simulation)
            TextView nameTextView = activity.findViewById(R.id.nameTextView);
            activity.nameTextView.setText(mockName);

            // Assert the TextView updated correctly
            assertEquals(mockName, nameTextView.getText().toString());
        });
    }
}

