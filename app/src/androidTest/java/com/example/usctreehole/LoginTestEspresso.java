package com.example.usctreehole;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTestEspresso {

    @Rule public ActivityScenarioRule<Login> activityScenarioRule
            = new ActivityScenarioRule<>(Login.class);

    @Rule
    public IntentsRule intentsTestRule = new IntentsRule();

    @Test
    public void loginWithoutExistingAccount() {
        onView(withId(R.id.enterEmail))
                .perform(typeText("fakeaccount@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.enterPassword))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginWithWrongPassword() {
        onView(withId(R.id.enterEmail))
                .perform(typeText("test5@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.enterPassword))
                .perform(typeText("wrong password"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loginCorrectly() {
        onView(withId(R.id.enterEmail))
                .perform(typeText("test5@usc.edu"), closeSoftKeyboard());
        onView(withId(R.id.enterPassword))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intended(hasComponent(MainActivity.class.getName()));
    }

}