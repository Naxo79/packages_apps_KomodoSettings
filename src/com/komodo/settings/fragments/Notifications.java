/*
 * Copyright (C) 2018 Havoc-OS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.komodo.settings.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.komodo.settings.preferences.CustomSeekBarPreference;
import com.komodo.settings.preferences.GlobalSettingMasterSwitchPreference;

public class Notifications extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "Notifications";
    private static final String HEADS_UP_NOTIFICATIONS_ENABLED = "heads_up_notifications_enabled";
    private static final String PULSE_AMBIENT_LIGHT_COLOR = "pulse_ambient_light_color";

    private GlobalSettingMasterSwitchPreference mHeadsUpEnabled;
    private ColorPickerPreference mEdgeLightColorPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.komodo_settings_notifications);
        PreferenceScreen prefScreen = getPreferenceScreen();
        final Resources res = getResources();

        mHeadsUpEnabled = (GlobalSettingMasterSwitchPreference) findPreference(HEADS_UP_NOTIFICATIONS_ENABLED);
        mHeadsUpEnabled.setOnPreferenceChangeListener(this);
        int headsUpEnabled = Settings.Global.getInt(getContentResolver(),
                HEADS_UP_NOTIFICATIONS_ENABLED, 1);
        mHeadsUpEnabled.setChecked(headsUpEnabled != 0);
        mEdgeLightColorPreference = (ColorPickerPreference) findPreference(PULSE_AMBIENT_LIGHT_COLOR);
        int edgeLightColor = Settings.System.getInt(getContentResolver(),
                Settings.System.PULSE_AMBIENT_LIGHT_COLOR, 0xFF3980FF);
        mEdgeLightColorPreference.setNewPreviewColor(edgeLightColor);
        mEdgeLightColorPreference.setAlphaSliderEnabled(false);
        String edgeLightColorHex = String.format("#%08x", (0xFF3980FF & edgeLightColor));
        if (edgeLightColorHex.equals("#ff3980ff")) {
            mEdgeLightColorPreference.setSummary(R.string.default_string);
        } else {
            mEdgeLightColorPreference.setSummary(edgeLightColorHex);
        }
        mEdgeLightColorPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHeadsUpEnabled) {
            boolean value = (Boolean) newValue;
            Settings.Global.putInt(getContentResolver(),
		            HEADS_UP_NOTIFICATIONS_ENABLED, value ? 1 : 0);
            return true;
        } else if (preference == mEdgeLightColorPreference) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            if (hex.equals("#ff3980ff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.PULSE_AMBIENT_LIGHT_COLOR, intHex);
            return true;
	}
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.KOMODO_SETTINGS;
    }
}
