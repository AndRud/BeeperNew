package com.andrutyk.beeper.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.andrutyk.beeper.R;

public class MainActivity extends AppCompatActivity{

    private final static String FRAGMENT_TAG = "main_fragment";
    private Fragment fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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