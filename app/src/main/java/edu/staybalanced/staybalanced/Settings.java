package edu.staybalanced.staybalanced;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

/**
 * The values of a Preference Fragment is stored in a SharedPreferences object.  Using the keys
 * defined in the corresponding PreferenceScreen XML file, we can reference these values in other
 * Activities and Fragments.  This is done with:
 *   SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
 *   [PreferenceDataType] val = sharedPrefs.get____("key", defaultValue);
 *
 * See https://developer.android.com/guide/topics/ui/settings/use-saved-values
 */
public class Settings extends PreferenceFragmentCompat {

    public Settings() {
        // Required empty public constructor
    }

    // PreferenceFragmentCompat's onCreate() equivalent
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        // Display dynamic explanatory text underneath the "Silent Mode" switch to show what changing
        // the preference does
        SwitchPreferenceCompat silent = findPreference("silent");
        if (silent != null) {
            silent.setSummaryProvider(new Preference.SummaryProvider<SwitchPreferenceCompat>() {
                @Override
                public CharSequence provideSummary(SwitchPreferenceCompat preference) {
                    if (preference.isChecked()) {
                        return getString(R.string.set_silentOn);
                    } else {
                        return getString(R.string.set_silentOff);
                    }
                }
            });
        }
    }
}