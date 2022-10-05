package com.example.simpletimer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.timer_preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for(int i =0; i<count;i++){
            Preference preference = preferenceScreen.getPreference(i);
            if(!(preference instanceof SwitchPreferenceCompat)){
                String value = sharedPreferences.getString(preference.getKey(),"");
                setPreferenceLabel(preference,value);
            }
        }
        Preference preference = findPreference("default_interval");
        preference.setOnPreferenceChangeListener(this);
    }
    private void setPreferenceLabel(Preference preference, String value){
        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            if(index >=0){
                listPreference.setSummary(listPreference.getEntries()[index]);
            }

        }
        if(preference instanceof EditTextPreference){
            preference.setSummary(value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if(!(preference instanceof SwitchPreferenceCompat)) {
            String value = sharedPreferences.getString(preference.getKey(), "");
            setPreferenceLabel(preference,value);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if(preference.getKey().equals("default_interval")){
            String defaultIntervalString = (String) o;

            try{
                int defaultInterval = Integer.parseInt(defaultIntervalString);
                if(defaultInterval<0){
                    Toast.makeText(getContext(), "Enter Valid Numbers", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            catch (NumberFormatException ne){
                if(defaultIntervalString.equals("")){
                    Toast.makeText(getContext(), "Put a Value", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else{
                    Toast.makeText(getContext(), "Enter Valid Numbers", Toast.LENGTH_SHORT).show();
                    return false;}
            }

        }
        return true;
    }
}
