package com.pklein.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pklein.bakingapp.data.ingredient;
import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.settings.SettingsActivity;
import com.pklein.bakingapp.tools.SimpleIdlingResource;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Parcelable mSavedRecyclerViewState;
    private static final String RECYCLER_STATE = "recycler";

    @BindView(R.id.recyclerview_recipe) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;

    private RecipesListAdapter mRecipesListAdapter;
    private GridLayoutManager mLayoutManager;
    private List<recipe> mListRecipe;

    // Only called FROM Tests : null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLayoutManager= new GridLayoutManager(this,numberOfColumns());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecipesListAdapter = new RecipesListAdapter();
        mRecyclerView.setAdapter(mRecipesListAdapter);

        // Get the IdlingResource instance
        getIdlingResource();
    }

    /**
     * Call loadRecipeData() from onStart instead of onCreate to ensure there is enougth time to register IdlingResource if the download is done too early
     */
    @Override
    protected void onStart() {
        super.onStart();
        loadRecipeData();
    }


    public class FetchMyDataTaskCompleteListener implements AsyncTaskCompleteListener<List<recipe>>
    {
        @Override
        public void onTaskComplete(List<recipe> recipesData)
        {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (recipesData != null) {
                mListRecipe = recipesData;
                // if a scroll position is saved, read it.
                if(mSavedRecyclerViewState!=null) {
                    mLayoutManager.onRestoreInstanceState(mSavedRecyclerViewState);
                }
                else { // create a new layout to return to top of the screen
                    mLayoutManager= new GridLayoutManager(getApplicationContext(),numberOfColumns());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                }
                showRecipeListView();
                mRecipesListAdapter.setRecipesData(recipesData);
            } else {
                showErrorMessage();
            }
        }
    }

    private void loadRecipeData() {
        showRecipeListView();
        showLoadingIndicator();
        new FetchRecipesTask(this, new FetchMyDataTaskCompleteListener()).execute();
    }

    // SHOW VIEWS OR NOT :
    private void showRecipeListView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public void showLoadingIndicator(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // recyclerview position :
        outState.putParcelable(RECYCLER_STATE,mLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //It will restore recycler view at same position
        if (savedInstanceState != null) {
            mSavedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_STATE);
        }
    }

    // with the Help of : https://stackoverflow.com/questions/15055458/detect-7-inch-and-10-inch-tablet-programmatically?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        float scaleFactor = displayMetrics.density;
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth > 600) {//Device is a 7" tablet or more
            return 3;
        }
        else  {//Device is a phone
            return 1;
        }

    }


    /**
     * Methods for setting up the menu
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.widget_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            CharSequence entries[] = new String[mListRecipe.size()];
            CharSequence entryValues[] = new String[mListRecipe.size()];
            int i = 0;
            for (recipe rec : mListRecipe) {
                entries[i] = rec.getmName();

                ingredient ing=rec.getmIngredients().get(0);
                String ingredients="<b>"+rec.getmName()+"</b><br/><b>"+ing.getmQuantity()+" "+ ing.getmMeasure()+"</b> "+ing.getmIngredient();

                for (int j = 1; j < rec.getmIngredients().size (); j++)
                {
                    ing = rec.getmIngredients().get(j);
                    ingredients=ingredients+"<br/><b>"+ing.getmQuantity()+" "+ ing.getmMeasure()+"</b> "+ing.getmIngredient();
                }
                entryValues[i] = ingredients;

                i++;
            }

            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startSettingsActivity.putExtra("entries", entries);
            startSettingsActivity.putExtra("entryValues", entryValues);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
