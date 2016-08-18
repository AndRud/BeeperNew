package com.andrutyk.beeper.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.soundpool.OnLoadResourceCallBack;
import com.andrutyk.beeper.soundpool.SoundPoolHelper;
import com.andrutyk.beeper.ui.MainActivity;

/**
 * Created by admin on 28.07.2016.
 */
public class BeeperService extends Service implements OnLoadResourceCallBack {

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
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        soundPoolHelper.cancelBeep();
        wakeLock.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLoadResource(int status) {
        if (status == 0) {
            soundPoolHelper.playBeep();
        } else {
            Toast.makeText(getApplicationContext(), getStringRecource(R.string.recource_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initWkeLock() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BEEP_WAKE_LOCK);
        wakeLock.acquire();
    }

    @NonNull
    private String getStringRecource(int id) {
        return getApplicationContext().getString(id);
    }
}
