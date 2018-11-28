package com.bytes.fightr.client.settings;


import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: SettingsController</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">SettingsController
 * API Guide</a> for more information on developing a SettingsController UI.
 */
public class SettingsActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsController()).commit();
    }
}
