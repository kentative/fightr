package com.bytes.fightr.client.settings;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.bytes.fightr.R;
import com.bytes.fightr.client.logic.GameState;
import com.bytes.fightr.client.service.comm.WebSocketService;

/**
 * A simple {@link PreferenceFragment} subclass.
 */
public class SettingsController extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static String KEY_PREF_CONNECT = "pref_connect";
    private static String KEY_PREF_SERVER_URL = "pref_server_url";

    /**
     * Required empty public constructor
     */
    public SettingsController() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_PREF_CONNECT)) {
            Preference connectionPref = findPreference(key);
            String connectionStatus = (sharedPreferences.getBoolean(key, false))
                    ? getString(R.string.pref_conn_active)
                    : getString(R.string.pref_conn_inactive);
            connectionPref.setSummary(connectionStatus);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Convenience method
     * @param context the application context
     * @return the flag indicating whether connection is enabled
     */
    public static boolean isConnected(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_PREF_CONNECT, false);
    }

    /**
     * Get the preferred server URL.
     * @param context the application context
     * @return the URL of the host server
     */
    public static String getServerUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_PREF_SERVER_URL, context.getString(R.string.default_server_url));
    }

}
