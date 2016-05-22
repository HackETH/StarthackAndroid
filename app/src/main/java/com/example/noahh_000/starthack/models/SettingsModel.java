package com.example.noahh_000.starthack.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by NoahH_000 on 03.05.2016.
 */
 /* This Model keeps track of all the settings saved in the system
 *
 * The settings are saved in a local file
 */

public class SettingsModel {
    private SharedPreferences preferenceEditor;
    private Context context;

    public enum Setting {
        FIRSTRUN;
    }

    public SettingsModel(Context context)
    {
        this.context = context;
        this.preferenceEditor = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getSettingB(Setting setting)
    {
        return preferenceEditor.getBoolean(setting.name(), true);
    }

    public void setSettingB(Setting setting, boolean value)
    {
        SharedPreferences.Editor editor = preferenceEditor.edit();
        editor.putBoolean("FIRSTRUN", value);
        editor.commit();
    }
}
