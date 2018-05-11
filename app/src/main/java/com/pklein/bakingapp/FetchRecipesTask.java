package com.pklein.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.pklein.bakingapp.data.recipe;
import com.pklein.bakingapp.tools.JsonUtils;
import com.pklein.bakingapp.tools.NetworkUtils;

import java.net.URL;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/*
 * Help from http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
 */

public class FetchRecipesTask extends AsyncTask<String, Void,List<recipe>> {
    private static final String TAG = "FetchRecipesTask";

    private Context context;
    private AsyncTaskCompleteListener<List<recipe>> listener;
    private List<recipe> mListRecipes;

    public FetchRecipesTask(Context ctx, AsyncTaskCompleteListener<List<recipe>> listener)
    {
        this.context = ctx;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<recipe> doInBackground(String... params) {

        /* If there's no filter, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
         String filter = params[0];*/

        // if there is no internet connection show error message
        if(!NetworkUtils.isconnected((ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE)))
        {
            return null;
        }
        else {
            URL recipesRequestUrl = NetworkUtils.buildListUrl();
            try {
                String jsonBakingResponse = NetworkUtils.getResponseFromHttpUrl(recipesRequestUrl);
                mListRecipes = JsonUtils.parseBakingJson(jsonBakingResponse);
                return mListRecipes;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onPostExecute(List<recipe> recipesData) {
        super.onPostExecute(recipesData);
        listener.onTaskComplete(recipesData);
    }
}
