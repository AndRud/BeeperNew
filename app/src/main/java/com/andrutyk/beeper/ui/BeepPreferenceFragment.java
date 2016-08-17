package com.andrutyk.beeper.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.utils.IntEditTextPreference;

/**
 * Created by admin on 16.08.2016.
 */
public class BeepPreferenceFragment extends PreferenceFragment {

    ListPreference listSounds;

    private int minValue = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        listSounds = (ListPreference) findPreference("listSounds");
        listSounds.setEntries(getEntriesValues());
        listSounds.setEntryValues(getEntriesValues());
        IntEditTextPreference etpTimeToBeep = (IntEditTextPreference)findPreference(
                getActivity().getResources().getString(R.string.time_to_beep_key));

        etpTimeToBeep.getEditText().setFilters(new InputFilter[]{
                getInputFilter()
        });

        IntEditTextPreference etpCountBeepKey = (IntEditTextPreference)findPreference(
                getActivity().getResources().getString(R.string.count_beep_key));

        etpCountBeepKey.getEditText().setFilters(new InputFilter[]{
                getInputFilter()
        });
    }

    private InputFilter getInputFilter() {
        return new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
                try {
                    int input = Integer.parseInt(dest.toString() + source.toString());
                    if (input > minValue)
                        return null;
                } catch (NumberFormatException nfe) { }
                return "";
            }
        };
    }

    private CharSequence[] getEntriesValues(){
        Field[] fields = R.raw.class.getFields();
        ArrayList<String> strings = new ArrayList<>();
        for (Field field : fields) {
            if (!field.isSynthetic()) {
                strings.add(field.getName());
            }
        }
        CharSequence[] entries = strings.toArray(new CharSequence[strings.size()]);
        return entries;
    }
}
