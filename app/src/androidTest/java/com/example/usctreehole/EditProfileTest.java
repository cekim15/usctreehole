package com.example.usctreehole;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.action.ViewActions;

import com.example.usctreehole.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EditProfileTest {

    @Rule
    public ActivityTestRule<EditProfile> activityRule =
            new ActivityTestRule<>(EditProfile.class);

    @Test
    public void testSaveProfileEdits_SuccessfulUpdate() {
        // Simulate user editing their profile
        onView(withId(R.id.enterName)).perform(ViewActions.typeText("John Doe"));
        onView(withId(R.id.enterID)).perform(ViewActions.typeText("123456789"));
        onView(withId(R.id.roleSelect)).perform(ViewActions.click());
        // (Assume selecting a role is handled via a spinner dialog)

        // Simulate clicking the Save button
        onView(withId(R.id.save_profile_edit)).perform(ViewActions.click());

        // Verify the success toast message is displayed
        // Replace "Profile updated successfully" with your exact string resource or literal string
        onView(withText("Profile updated successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(withText("Profile updated successfully")));
    }
}
