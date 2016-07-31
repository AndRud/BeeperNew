package com.andrutyk.beeper.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.service.BeeperService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, BeeperService.class));
    }

    public void onClick(View view) {
        stopService(new Intent(this, BeeperService.class));
    }
}