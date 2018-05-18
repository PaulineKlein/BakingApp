package com.pklein.bakingapp;


import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.pklein.bakingapp.data.ingredient;
import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class NavigateBetweenStepsTest {

    private recipe mrecipe;

    @Rule
    public ActivityTestRule<AllRecipeStepsActivity> mActivityTestRule =
            new ActivityTestRule<AllRecipeStepsActivity>(AllRecipeStepsActivity.class){

        @Override
        protected Intent getActivityIntent() {
            List<ingredient> ing = new ArrayList<>();
            ing.add(new ingredient("500", "G", "Chocolate"));
            List<step> step = new ArrayList<>();
            step.add(new step(0,0, "test", "test", "", ""));
            mrecipe = new recipe(0, 0, "Brownies", 8, "", ing, step);
            Intent intent = new Intent();
            intent.putExtra("Recipe", mrecipe);
            return intent;
        }
    };

    @Test
    public void clickOn_oneStep() {

        // Click on first item in recyclerView :
        onView(withId(R.id.recyclerview_steps))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //check if the next page is open : as the video is blank, an image must be displayed :
        onView(withId(R.id.image_iv_replaceVideo))
                .check(matches(isDisplayed()));
    }
}
