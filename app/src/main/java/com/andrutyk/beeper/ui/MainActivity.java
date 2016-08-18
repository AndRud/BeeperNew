package com.andrutyk.beeper.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrutyk.beeper.R;
import com.flurry.android.FlurryAgent;

public class MainActivity extends AppCompatActivity{

    private final static String FRAGMENT_TAG = "main_fragment";
    private final static String FLURRY_API_KEY = "8H9MT5PC4W6MQBQGTZWD";

    private Fragment fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlurryAgent.setLogEnabled(false);
        FlurryAgent.init(this, FLURRY_API_KEY);
        addFragment();
    }

    private void addFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentMain = getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragmentMain == null){
            fragmentMain = new MainFragment();
            fragmentTransaction.add(R.id.fragmentContent, fragmentMain, FRAGMENT_TAG);
        } else {
            fragmentTransaction.replace(R.id.fragmentContent, fragmentMain);
        }
        fragmentTransaction.commit();
    }
}