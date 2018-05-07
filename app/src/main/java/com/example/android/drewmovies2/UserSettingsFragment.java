package com.example.android.drewmovies2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class UserSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_movie_results);
    }
}