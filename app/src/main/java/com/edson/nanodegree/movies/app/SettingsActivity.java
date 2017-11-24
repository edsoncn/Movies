 package com.edson.nanodegree.movies.app;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

        }

        public String appVersion() throws PackageManager.NameNotFoundException {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            return version;
        }
    }
}