package com.example.usctreehole;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.contrib.DrawerActions;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.android.material.tabs.TabLayout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;

public class MainActivityTestEspresso {
    private CountingIdlingResource idlingResource;

    @Rule
    public ActivityScenarioRule<Login> activityScenarioRule = new ActivityScenarioRule<>(Login.class);

    @Rule
    public IntentsRule intentsTestRule = new IntentsRule();

    @Before
    public void setUp() {
        // TODO
    }

    @Test
    public void changePostCategories() {
        onView(withId(R.id.change_category)).perform(selectTab(1));
        onView(withId(R.id.change_category)).perform(selectTab(0));
    }


    public static ViewAction selectTab(final int tabIndex) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TabLayout.class);
            }

            @Override
            public String getDescription() {
                return "Select tab at index " + tabIndex;
            }

            @Override
            public void perform(UiController uiController, View view) {
                TabLayout tabLayout = (TabLayout) view;
                TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
                if (tab != null) {
                    tab.select();
                } else {
                    throw new IllegalArgumentException("Tab at index " + tabIndex + " does not exist.");
                }
            }
        };
    }

    @Test
    public void navigateToProfile() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_profile)).perform(click());

        intended(hasComponent(Profile.class.getName()));
    }
}