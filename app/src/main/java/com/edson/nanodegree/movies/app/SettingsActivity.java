 package com.edson.nanodegree.movies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.os.LocaleListCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

 /**
 * Created by edson on 27/07/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragment  {

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.layout_settings, container, false);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Set<String> values = sharedPref.getStringSet("multi_select_list_genres", null);

            if(values != null) {
                for (String s : values) {
                    Log.i(LOG_TAG, " > values: " + s);
                }
            }

            Preference prefAboutVersion = findPreference(getResources().getString(R.string.preference_about_version_key));
            try {
                prefAboutVersion.setSummary(String.valueOf(prefAboutVersion.getSummary()).replace("{0}", appVersion()));
            } catch (PackageManager.NameNotFoundException e) {
                Log.i(LOG_TAG, "Can't find the version: " + e.getMessage());
            }

            Preference genListPreference = findPreference(getString(R.string.preference_genres_list_key));
            if (genListPreference != null) {
                genListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    // 1. Logic to handle the change
                    Log.i(LOG_TAG, "Genres preference changed. Restarting stack to refresh lists.");

                    // 2. Restart the Task Stack
                    // This clears all activities (including DetailActivity) and returns to MainActivity
                    // forcing a fresh load of the Discovery Service with new genre IDs.
                    Intent intent = new Intent(getActivity(), MoviesMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    // Optional: Close settings so the user sees the refresh immediately
                    getActivity().finish();

                    return true;
                });
            }

            Preference languagePreference = findPreference(getString(R.string.preference_language_key));
            if (languagePreference != null) {
                languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String languageCode = (String) newValue;

                    if ("local".equals(languageCode)) {
                        // Reset to system default
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
                    } else {
                        // Force English (or any other ISO code provided in your values array)
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
                    }
                    // Return true to allow the preference to be saved
                    return true;
                });
            }
        }

        public String appVersion() throws PackageManager.NameNotFoundException {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            return pInfo.versionName;
        }
    }
}