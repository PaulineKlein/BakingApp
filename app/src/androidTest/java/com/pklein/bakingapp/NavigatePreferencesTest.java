package com.pklein.bakingapp;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class NavigatePreferencesTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);


    //with the help of https://developer.android.com/training/testing/espresso/recipes
    @Test
    public void clickOn_Menu() {

        // Open the options menu :
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // Click the item.
        onView(withText("Set Widget"))
                .perform(click());

        // check the preference screen :
        onView(withId(R.id.activity_settings))
                .check(matches(isCompletelyDisplayed()));

    }
}
