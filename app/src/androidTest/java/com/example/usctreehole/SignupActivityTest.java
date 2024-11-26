package com.example.usctreehole;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Rule
    public ActivityScenarioRule<Signup> activityRule =
            new ActivityScenarioRule<>(Signup.class);

    @Test
    public void testSignupPageLoads() {
        // Check if the email field is displayed
        onView(withId(R.id.enterEmail)).check(matches(isDisplayed()));

        // Check if the password field is displayed
        onView(withId(R.id.enterPassword)).check(matches(isDisplayed()));

        // Check if the name field is displayed
        onView(withId(R.id.enterName)).check(matches(isDisplayed()));

        // Check if the USC ID field is displayed
        onView(withId(R.id.enterID)).check(matches(isDisplayed()));

        // Check if the role spinner is displayed
        onView(withId(R.id.roleSelect)).check(matches(isDisplayed()));

        // Check if the signup button is displayed
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));

        // Check if the upload image button is displayed
        onView(withId(R.id.uploadImageButton)).check(matches(isDisplayed()));
    }
}

