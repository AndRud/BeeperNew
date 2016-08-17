package com.andrutyk.beeper.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrutyk.beeper.R;

public class BeepPreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beep_preference);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new BeepPreferenceFragment()).commit();
    }
}
