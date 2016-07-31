package com.andrutyk.beeper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.soundpool.OnLoadResourceCallBack;
import com.andrutyk.beeper.soundpool.SoundPoolHelper;

/**
 * Created by admin on 28.07.2016.
 */
public class BeeperService extends Service implements OnLoadResourceCallBack{

    private final static String BEEP_WAKE_LOCK = "BeepWakeLock";

    private SoundPoolHelper soundPoolHelper;

    private WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        initWkeLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        soundPoolHelper = new SoundPoolHelper(getBaseContext(), this);
        soundPoolHelper.loadSource();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        soundPoolHelper.cancelBeep();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLoadResource(int status) {
        if (status == 0){
            soundPoolHelper.playBeep();
        } else {
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.recource_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initWkeLock(){
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BEEP_WAKE_LOCK);
        wakeLock.acquire();
    }
}
