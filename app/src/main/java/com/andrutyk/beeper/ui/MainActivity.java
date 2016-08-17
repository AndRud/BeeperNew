package com.andrutyk.beeper.ui;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.service.BeeperService;
import com.andrutyk.beeper.soundpool.SoundPoolHelper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;


public class MainActivity extends AppCompatActivity implements TextSwitcher.ViewFactory{

    private ActivityManager manager;

    private TextSwitcher tsTimeToBeep;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        tsTimeToBeep = (TextSwitcher) findViewById(R.id.tsTimeToBeep);
        tsTimeToBeep.setFactory(this);

        initAnimation();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private BroadcastReceiver timeToBeepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long timeToBeep = intent.getLongExtra(SoundPoolHelper.EXTRA_COUNTDOWN, 0);
            DateTime dateTime = new DateTime(timeToBeep);
            String time = dateTime.withZone(DateTimeZone.UTC).toString("HH:mm:ss");
            tsTimeToBeep.setText(String.valueOf(time));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(timeToBeepReceiver, new IntentFilter(SoundPoolHelper.COUNTDOWN_BR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(timeToBeepReceiver);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(timeToBeepReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    private void initAnimation() {
        Animation inAnimation = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        Animation outAnimation = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        tsTimeToBeep.setInAnimation(inAnimation);
        tsTimeToBeep.setOutAnimation(outAnimation);
        //tsTimeToBeep.setText("00:00:00");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnBeep):
                if (!isBeepServiceRunning(BeeperService.class, manager)) {
                    startService(new Intent(this, BeeperService.class));
                } else {
                    stopService(new Intent(this, BeeperService.class));
                }
                break;
            case (R.id.imgBtnSetting):
                startActivity(new Intent(this, BeepPreferenceActivity.class));
                break;
            default:
                break;
        }
    }

    public static boolean isBeepServiceRunning(Class<?> serviceClass, ActivityManager manager){
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(this);
        textView.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
        textView.setTextColor(ContextCompat.getColor(this, R.color.btnBackgroundColor));
        return textView;
    }
}