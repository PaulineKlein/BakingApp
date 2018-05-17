package com.pklein.bakingapp.settings;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.pklein.bakingapp.R;
import com.pklein.bakingapp.widget.RecipeWidgetProvider;


//https://stackoverflow.com/questions/6136770/android-dynamic-array-listpreference?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = this.getSupportActionBar();

        // Set the action bar back button to look like an up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(TAG, "getItemId : "+id);

        //UPDATE WIDGET :
        //With the Help of : https://stackoverflow.com/questions/3455123/programmatically-update-widget-from-activity-service-receiver?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        Intent intent = new Intent(this, RecipeWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), RecipeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);

        // When the home button is pressed, take the user back to the MainActivity
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
