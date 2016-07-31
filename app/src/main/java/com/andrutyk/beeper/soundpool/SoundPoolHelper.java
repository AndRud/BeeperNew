package com.andrutyk.beeper.soundpool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;

import com.andrutyk.beeper.utils.PrefUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 28.07.2016.
 */
public class SoundPoolHelper implements OnLoadCompleteListener {

    private static final int MAX_STREAMS = 1;

    private static final int DEF_CONT_BEEP = 0;
    private static final int DEF_TIME_TO_BEEP = 5;

    private static final String SOURCE_FOLDER_NAME = "raw";
    private static final String DEF_SOUND_NAME = "hangouts_message";

    private SoundPool spBeep;
    private int soundID;
    private TimerTask taskBeep;
    private Timer timerBeep;
    private OnLoadResourceCallBack onLoadResourceCallBack;

    private Context context;

    private final PrefUtils prefs;

    private AudioManager audioManager;
    //private AFListener afListener;

    public SoundPoolHelper(Context context, OnLoadResourceCallBack onLoadResourceCallBack) {
        this.context = context;
        this.prefs = new PrefUtils(context);
        this.onLoadResourceCallBack = onLoadResourceCallBack;
        timerBeep = new Timer();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
        String soundBeep = prefs.getCurrSoundName(DEF_SOUND_NAME);
        soundID = spBeep.load(context, getResID(context, SOURCE_FOLDER_NAME, soundBeep), 1);
    }

    public void cancelBeep() {
        timerBeep.cancel();
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
        final int countBeep = prefs.getCurrCountBeep(DEF_CONT_BEEP);
        taskBeep = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                spBeep.play(soundID,  1.0f, 1.0f, 1, countBeep, 1f);
            }
        };

        try {
            timerBeep.schedule(taskBeep, 0, prefs.getCurrTimeToBeep(DEF_TIME_TO_BEEP) * 1000);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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
