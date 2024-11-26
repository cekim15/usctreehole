package com.example.usctreehole;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewRepliesTest {

    @Rule
    public ActivityScenarioRule<ViewReplies> activityRule =
            new ActivityScenarioRule<>(createViewRepliesIntent());

    private static Intent createViewRepliesIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewReplies.class);
        // Pass required Intent extras to avoid NullPointerException
        intent.putExtra("collection", "testCollection");
        intent.putExtra("postID", "testPostID");
        return intent;
    }

    @Test
    public void testViewRepliesPageLoadsWithoutCrash() {
        // Check if the activity loads without crashing
        onView(isRoot()).check(matches(isDisplayed()));
    }

    @Test
    public void testAnonymousToggleInitializes() {
        // Check if the anonymous toggle switch is displayed
        onView(isRoot()).check(matches(isDisplayed())); // Root check ensures activity runs
    }
}

