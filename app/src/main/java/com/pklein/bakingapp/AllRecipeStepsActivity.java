package com.pklein.bakingapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.pklein.bakingapp.data.ingredient;
import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.data.step;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllRecipeStepsActivity extends AppCompatActivity implements AllRecipeStepsFragment.OnStepClickListener{

    private static final String TAG = AllRecipeStepsActivity.class.getSimpleName();
    private boolean mTwoPane; // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    private String mRecipeName;
    private recipe mrecipe;
    private Parcelable mSavedRecyclerViewState;
    private static final String RECYCLER_STATE = "recycler";

    @BindView(R.id.tv_ingredient_name)    TextView mingredientNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipe_steps);
        ButterKnife.bind(this);

        mTwoPane = false;

        if(getIntent().hasExtra("Recipe")) {
            mrecipe = getIntent().getExtras().getParcelable("Recipe");
            mRecipeName = mrecipe.getmName();

            ingredient ing=mrecipe.getmIngredients().get(0);
            String ingredients="<b>"+ing.getmQuantity()+" "+ ing.getmMeasure()+"</b> "+ing.getmIngredient();

            for (int i = 1; i < mrecipe.getmIngredients().size (); i++)
            {
                ing = mrecipe.getmIngredients().get(i);
                ingredients=ingredients+"<br/><b>"+ing.getmQuantity()+" "+ ing.getmMeasure()+"</b> "+ing.getmIngredient();
            }

            mingredientNameTV.setText(Html.fromHtml(ingredients));
        }
    }

    public void onStepSelected(step StepSelected) {

        if(!mTwoPane) {
            Log.i(TAG, "OK " + StepSelected.getmShortDescription());
            Intent startChildActivityIntent = new Intent(this, OneRecipeStepActivity.class);
            if (StepSelected != null) {
                startChildActivityIntent.putExtra("Step", StepSelected);
                startChildActivityIntent.putExtra("Name", mRecipeName);
                Log.i(TAG, "NAME  :"+mRecipeName);
            }
            this.startActivity(startChildActivityIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // outState.putParcelable(RECYCLER_STATE,mLayoutManager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //It will restore recycler view at same position
     /*   if (savedInstanceState != null) {
            mSavedRecyclerViewState = savedInstanceState.getParcelable(RECYCLER_STATE);
        }*/
    }
}