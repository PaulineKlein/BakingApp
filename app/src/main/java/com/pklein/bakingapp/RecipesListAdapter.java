package com.pklein.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pklein.bakingapp.data.recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipesListAdapter  extends RecyclerView.Adapter<RecipesListAdapter.RecipesListAdapterViewHolder> {

    private static final String TAG = RecipesListAdapter.class.getSimpleName();
    private List<recipe> mRecipesData;

    public RecipesListAdapter() {
    }

    public class RecipesListAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_recipe_name) TextView recipeNameTv;
        @BindView(R.id.image_iv_thumbnail) ImageView recipeImageIv;

        public RecipesListAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public RecipesListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i(TAG, "Start RecipesListAdapterViewHolder");
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_card;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RecipesListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipesListAdapterViewHolder recipesListAdapterViewHolder, int position) {

        final Context context = recipesListAdapterViewHolder.itemView.getContext();
        final recipe RecipeSelected = mRecipesData.get(position);

        recipesListAdapterViewHolder.recipeNameTv.setText(RecipeSelected.getmName());

        Log.i(TAG, "Start onBindViewHolder "+RecipeSelected.getmImage());
        if(RecipeSelected.getmImage().equals(""))
        {
             RecipeSelected.setmImage("R.drawable.recipe_default_small");
        }
        Picasso.with(context)
                .load(RecipeSelected.getmImage())
                .error(R.drawable.recipe_default_small)
                .into(recipesListAdapterViewHolder.recipeImageIv);


        recipesListAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startChildActivityIntent = new Intent(context, AllRecipeStepsActivity.class);
                if(RecipeSelected != null)
                {
                    startChildActivityIntent.putExtra("Recipe", RecipeSelected);
                }
                context.startActivity(startChildActivityIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mRecipesData) return 0;
        return mRecipesData.size();
    }

    public void setRecipesData(List<recipe> recipeData) {
        mRecipesData = recipeData;
        notifyDataSetChanged();
    }
}
