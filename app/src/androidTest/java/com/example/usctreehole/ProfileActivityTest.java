package com.example.usctreehole;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Rule
    public ActivityScenarioRule<Profile> activityRule =
            new ActivityScenarioRule<>(Profile.class);

    @Test
    public void testProfilePageLoads() {
        // Check if the main layout of the activity is displayed
        onView(isRoot()).check(matches(isDisplayed()));
    }
}

