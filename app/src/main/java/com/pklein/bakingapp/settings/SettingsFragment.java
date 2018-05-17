package com.pklein.bakingapp.settings;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import com.pklein.bakingapp.R;


public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        //With the Help of :
        // https://medium.com/@arasthel92/dynamically-creating-preferences-on-android-ecc56e4f0789
        //https://vogella.developpez.com/tutoriels/android/persitance-preferences-fichiers/
        addPreferencesFromResource(R.xml.pref_widget);

        if(getActivity().getIntent().hasExtra("entries") && getActivity().getIntent().hasExtra("entryValues")) {
            CharSequence entries[] =getActivity().getIntent().getExtras().getCharSequenceArray("entries");
            CharSequence entryValues[] =getActivity().getIntent().getExtras().getCharSequenceArray("entryValues");

            ListPreference listPreferenceCategory = (ListPreference) findPreference("menu_key");
            listPreferenceCategory.setEntries(entries);
            listPreferenceCategory.setEntryValues(entryValues);

            Log.i(TAG, "entries  :"+entries[0]);
            Log.i(TAG, "entryValues  :"+entryValues[0]);

        }
    }
}