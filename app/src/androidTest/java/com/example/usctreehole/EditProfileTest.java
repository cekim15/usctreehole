package com.example.usctreehole;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class EditProfileTest {

    @Rule public ActivityScenarioRule<Login> activityScenarioRule
            = new ActivityScenarioRule<>(Login.class);

    @BeforeClass
    public static void setUp(){
        ActivityScenario.launch(EditProfile.class);
        try {
            Thread.sleep(5352);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.enterEmail),
                        childAtPosition(
                                allOf(withId(R.id.main),
                                        childAtPosition(
                                                withId(R.id.drawer_layout),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.enterEmail),
                        childAtPosition(
                                allOf(withId(R.id.main),
                                        childAtPosition(
                                                withId(R.id.drawer_layout),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(typeText("test5@usc.edu"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.enterPassword),
                        childAtPosition(
                                allOf(withId(R.id.main),
                                        childAtPosition(
                                                withId(R.id.drawer_layout),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(typeText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.main),
                                        childAtPosition(
                                                withId(R.id.drawer_layout),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());
    }

    @Test
    public void testSaveProfileEdits_CancelUpdate() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Automate clicking on the nav drawer to open it
        // Assuming a button or gesture that opens the drawer, for example:
        ViewInteraction drawerToggle = onView(withContentDescription("Open navigation drawer"));
        drawerToggle.perform(click());

        // Now that the drawer is open, perform a click on a specific item in the NavigationView
        // Example: Click on the "Profile" menu item in the drawer menu
        ViewInteraction profileMenuItem = onView(withId(R.id.nav_profile));
        profileMenuItem.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        editProfileButton
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.editProfileButton),
                        isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Edit name
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.enterName),
                        isDisplayed()));
        appCompatEditText.perform(click());
        appCompatEditText.perform(clearText(), typeText("James"), closeSoftKeyboard());

//        Edit ID
        appCompatEditText = onView(
                allOf(withId(R.id.enterID),
                        isDisplayed()));
        appCompatEditText.perform(click());
        appCompatEditText.perform(clearText(), typeText("1123"), closeSoftKeyboard());

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//       edit role
        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.roleSelect),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        onView(withText("Undergraduate Student")).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        edit profile picture
//        imageViewProfilePic

//        appCompatButton = onView(
//                allOf(withId(R.id.uploadNewImageButton),
//                        isDisplayed()));
//        appCompatButton.perform(click());


//        save edits
        appCompatButton = onView(
                allOf(withId(R.id.cancel_profile_edit),
                        isDisplayed()));
        appCompatButton.perform(click());


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveProfileEdits_SuccessfulUpdate() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Automate clicking on the nav drawer to open it
        // Assuming a button or gesture that opens the drawer, for example:
        ViewInteraction drawerToggle = onView(withContentDescription("Open navigation drawer"));
        drawerToggle.perform(click());

        // Now that the drawer is open, perform a click on a specific item in the NavigationView
        // Example: Click on the "Profile" menu item in the drawer menu
        ViewInteraction profileMenuItem = onView(withId(R.id.nav_profile));
        profileMenuItem.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        editProfileButton
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.editProfileButton),
                        isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        Edit name
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.enterName),
                        isDisplayed()));
        appCompatEditText.perform(click());
        appCompatEditText.perform(clearText(), typeText("James"), closeSoftKeyboard());

//        Edit ID
        appCompatEditText = onView(
                allOf(withId(R.id.enterID),
                        isDisplayed()));
        appCompatEditText.perform(click());
        appCompatEditText.perform(clearText(), typeText("1123"), closeSoftKeyboard());

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//       edit role
        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.roleSelect),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        onView(withText("Undergraduate Student")).perform(click());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        edit profile picture
//        imageViewProfilePic

//        appCompatButton = onView(
//                allOf(withId(R.id.uploadNewImageButton),
//                        isDisplayed()));
//        appCompatButton.perform(click());


//        save edits
        appCompatButton = onView(
                allOf(withId(R.id.cancel_profile_edit),
                        isDisplayed()));
        appCompatButton.perform(click());


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
