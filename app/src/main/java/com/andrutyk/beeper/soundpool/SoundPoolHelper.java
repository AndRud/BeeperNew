package com.andrutyk.beeper.soundpool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.andrutyk.beeper.notification.BeepNotific;
import com.andrutyk.beeper.utils.PrefUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 28.07.2016.
 */
public class SoundPoolHelper implements OnLoadCompleteListener {

    private static final int MAX_STREAMS = 1;

    public static final int DEF_CONT_BEEP = 0;
    public static final int DEF_TIME_TO_BEEP = 5;

    private static final String SOURCE_FOLDER_NAME = "raw";
    private static final String DEF_SOUND_NAME = "hangouts_message";
    private static final long VIBRATE_LENGTH = 500;

    public static final String PREF_SOUND_NAME = "listSounds";
    public static final String PREF_COUNT_BEEP = "countBeep";
    public static final String PREF_TIME_TO_BEEP = "timeToBeep";
    public static final String PREF_VIBRATE_KEY = "vibrateKey";

    private SoundPool spBeep;
    private int soundID;
    private TimerTask taskBeep;
    private Timer timerBeep;
    private OnLoadResourceCallBack onLoadResourceCallBack;

    private Context context;

    private AudioManager audioManager;
    //private AFListener afListener;

    private SharedPreferences prefs;

    public static final String COUNTDOWN_BR = "com.andrutyk.beeper.countdown_br";
    public static final String EXTRA_COUNTDOWN = "countdown";

    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdtTimeToBeep = null;
    Vibrator vibrator;

    private BeepNotific beepNotific;

    public SoundPoolHelper(Context context, OnLoadResourceCallBack onLoadResourceCallBack) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.onLoadResourceCallBack = onLoadResourceCallBack;
        timerBeep = new Timer();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        beepNotific = new BeepNotific(context);
        createSoundPool();
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            spBeep = new SoundPool.Builder().setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build())
                    .build();
        } else {
            spBeep = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        spBeep.setOnLoadCompleteListener(this);
    }

    public void loadSource() {
        String soundBeep = prefs.getString(PREF_SOUND_NAME, DEF_SOUND_NAME);
        soundID = spBeep.load(context, getResID(context, SOURCE_FOLDER_NAME, soundBeep), 1);
    }

    public void cancelBeep() {
        timerBeep.cancel();

        if (taskBeep != null) {
            taskBeep.cancel();
        }

        if (cdtTimeToBeep != null) {
            cdtTimeToBeep.cancel();
        }

        sendBroadcastUpdate(0);
        beepNotific.cancelBeepNotific();
    }

    private int getResID(Context context, String folderName, String resName) {
        return context.getResources().getIdentifier(folderName + "/" + resName, folderName, context.getPackageName());
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
        onLoadResourceCallBack.onLoadResource(i1);
    }

    public void playBeep() {
        if (taskBeep != null) taskBeep.cancel();

        final long timeToBeep = prefs.getInt(SoundPoolHelper.PREF_TIME_TO_BEEP,
                SoundPoolHelper.DEF_TIME_TO_BEEP) * 1000;

        cdtTimeToBeep = getCdtTimeToBeep(timeToBeep);

        taskBeep = getTaskBeep(timeToBeep);

        try {
            timerBeep.schedule(taskBeep, 0, timeToBeep);
            beepNotific.sendNotification("00:00:00");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private CountDownTimer getCdtTimeToBeep(long timeToBeep) {
        return new CountDownTimer(timeToBeep + 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < (millisUntilFinished + 1000) * 1000) {
                    bi.putExtra(EXTRA_COUNTDOWN, getStringTimeToBeep(millisUntilFinished));
                }
                context.sendBroadcast(bi);
                beepNotific.sendNotification(getStringTimeToBeep(millisUntilFinished));
            }

            @Override
            public void onFinish() {}
        };
    }

    private TimerTask getTaskBeep(final long timeToBeep) {
        return new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int countBeep = prefs.getInt(PREF_COUNT_BEEP, DEF_CONT_BEEP) -1;
                if (countBeep < 1) {
                    countBeep = 0;
                };

                sendBroadcastUpdate(timeToBeep);
                spBeep.play(soundID,  1.0f, 1.0f, 1, countBeep, 1f);
                vibrate();
                cdtTimeToBeep.start();
            }
        };

    }

    private void sendBroadcastUpdate(long timeToBeep) {
        bi.putExtra(EXTRA_COUNTDOWN, getStringTimeToBeep(timeToBeep));
        context.sendBroadcast(bi);
    }

    private String getStringTimeToBeep(long timeToBeep) {
        DateTime dateTime = new DateTime(timeToBeep);
        return dateTime.withZone(DateTimeZone.UTC).toString("HH:mm:ss");
    }

    private void vibrate() {
        if (prefs.getBoolean(PREF_VIBRATE_KEY, true)) {
            vibrator.vibrate(VIBRATE_LENGTH);
        }
    }

    /*private void requestFocusForBeep() {
        afListener = new AFListener(spBeep);
        int requestResult = audioManager.requestAudioFocus(afListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
    }

    class AFListener implements AudioManager.OnAudioFocusChangeListener {

        private SoundPool sp;

        public AFListener(SoundPool sp) {
            this.sp = sp;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    }*/
}
