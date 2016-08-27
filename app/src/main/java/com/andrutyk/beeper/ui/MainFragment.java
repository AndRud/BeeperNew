package com.andrutyk.beeper.ui;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.service.BeeperService;
import com.andrutyk.beeper.soundpool.SoundPoolHelper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by admin on 17.08.2016.
 */

public class MainFragment extends Fragment implements TextSwitcher.ViewFactory, View.OnClickListener{

    private final static String DEF_TIME = "00:00:00";

    private ActivityManager manager;

    private TextSwitcher tsTimeToBeep;
    private Button btnBeep;
    private ImageButton imgBtnSetting;

    SharedPreferences sharedPreferences;

    private String timeToBeep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setRetainInstance(true);
        timeToBeep = DEF_TIME;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);

        tsTimeToBeep = (TextSwitcher) view.findViewById(R.id.tsTimeToBeep);
        tsTimeToBeep.setFactory(this);
        initAnimation();

        btnBeep = (Button) view.findViewById(R.id.btnBeep);
        btnBeep.setOnClickListener(this);
        imgBtnSetting = (ImageButton) view.findViewById(R.id.imgBtnSetting);
        imgBtnSetting.setOnClickListener(this);

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnBeep):
                if (!isBeepServiceRunning(BeeperService.class, manager)) {
                    getActivity().startService(new Intent(getActivity(), BeeperService.class)
                            .setAction(BeeperService.ACTION_START));
                } else {
                    getActivity().stopService(new Intent(getActivity(), BeeperService.class)
                            .setAction(BeeperService.ACTION_STOP));
                }
                break;
            case (R.id.imgBtnSetting):
                startActivity(new Intent(getActivity(), BeepPreferenceActivity.class));
                break;
            default:
                break;
        }
    }

    private static boolean isBeepServiceRunning(Class<?> serviceClass, ActivityManager manager){
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void initAnimation() {
        Animation inAnimation = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_in);
        Animation outAnimation = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_out);

        tsTimeToBeep.setInAnimation(inAnimation);
        tsTimeToBeep.setOutAnimation(outAnimation);
        setTextTimeToBeep(timeToBeep);
    }

    private BroadcastReceiver timeToBeepReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            timeToBeep = intent.getStringExtra(SoundPoolHelper.EXTRA_COUNTDOWN);
            setTextTimeToBeep(timeToBeep);
        }
    };

    private void setTextTimeToBeep(String timeToBeep) {
        tsTimeToBeep.setText(timeToBeep);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(timeToBeepReceiver, new IntentFilter(SoundPoolHelper.COUNTDOWN_BR));
    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(timeToBeepReceiver);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(timeToBeepReceiver);
        super.onPause();
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.btnBackgroundColor));
        return textView;
    }

    public void setTimeToBeep(String timeToBeep) {
        this.timeToBeep = timeToBeep;
    }

    public String getTimeToBeep() {
        return timeToBeep;
    }
}
