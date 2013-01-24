package com.augmentari.roadworks.sensorlogger.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import com.augmentari.roadworks.sensorlogger.R;

/**
 * Activity for settings (preferences here).
 */
public class PrefActivity extends Activity {
    public static final String KEY_PREF_API_BASE_URL = "pref_api_base_url";

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            Preference connectionPref = findPreference(KEY_PREF_API_BASE_URL);
            connectionPref.setSummary(getPreferenceScreen().getSharedPreferences().getString(KEY_PREF_API_BASE_URL, ""));
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (KEY_PREF_API_BASE_URL.equals(key)) {
                Preference connectionPref = findPreference(key);
                connectionPref.setSummary(sharedPreferences.getString(key, ""));
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
