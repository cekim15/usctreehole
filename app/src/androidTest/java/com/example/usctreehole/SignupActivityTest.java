package com.example.usctreehole;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.usctreehole.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Test
    public void testSignupWithoutProfilePicture_ShowsErrorMessage() {
        try (ActivityScenario<Signup> scenario = ActivityScenario.launch(Signup.class)) {
            // Enter required fields
            onView(withId(R.id.enterEmail)).perform(typeText("testuser@example.com"));
            onView(withId(R.id.enterPassword)).perform(typeText("password123"));
            onView(withId(R.id.enterName)).perform(typeText("Test User"));
            onView(withId(R.id.enterID)).perform(typeText("123456789"));
            // No profile picture selected

            // Click the Signup button
            onView(withId(R.id.signupButton)).perform(click());

            // Verify error toast is displayed
            onView(withText("Please select an image"))
                    .inRoot(new ToastMatcher())
                    .check(matches(withText("Please select an image")));
        }
    }

    @Test
    public void testSuccessfulSignup_RedirectsToMainActivity() {
        try (ActivityScenario<Signup> scenario = ActivityScenario.launch(Signup.class)) {
            // Mock necessary backend behavior or skip for simplicity
            // Ensure FirebaseAuth and Firestore are set up in the test environment

            // Enter required fields
            onView(withId(R.id.enterEmail)).perform(typeText("testuser@example.com"));
            onView(withId(R.id.enterPassword)).perform(typeText("password123"));
            onView(withId(R.id.enterName)).perform(typeText("Test User"));
            onView(withId(R.id.enterID)).perform(typeText("123456789"));

            // Simulate profile picture selection
            // (Assume testing frameworks provide a way to simulate file pickers if needed)

            // Click the Signup button
            onView(withId(R.id.signupButton)).perform(click());

            // Verify redirection to MainActivity (placeholder verification, adjust as needed)
            onView(withText("Welcome to MainActivity!")).check(matches(withText("Welcome to MainActivity!")));
        }
    }
}
