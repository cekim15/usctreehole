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
public class ViewRepliesTest {

    @Test
    public void testCreateReply_ShowsSuccessMessage() {
        // Launch the activity
        try (ActivityScenario<ViewReplies> scenario = ActivityScenario.launch(ViewReplies.class)) {
            // Enter reply text
            onView(withId(R.id.reply_edit_text)).perform(typeText("This is a test reply."));

            // Click the send button
            onView(withId(R.id.send_reply_button)).perform(click());

            // Verify success message is displayed
            onView(withText("Reply posted successfully"))
                    .inRoot(new ToastMatcher())
                    .check(matches(withText("Reply posted successfully")));
        }
    }
}
