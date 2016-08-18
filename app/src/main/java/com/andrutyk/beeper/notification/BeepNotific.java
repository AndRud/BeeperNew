package com.andrutyk.beeper.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.andrutyk.beeper.R;
import com.andrutyk.beeper.service.BeeperService;
import com.andrutyk.beeper.ui.MainActivity;

/**
 * Created by admin on 18.08.2016.
 */
public class BeepNotific {

    private final static int NOTIFIC_ID = 1;

    private Context context;
    private NotificationManager notificationManager;

    public BeepNotific(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void sendNotification(String timeToBeep) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);

        Intent intentStopBeep = new Intent(context, BeeperService.class);
        intentStopBeep.setAction(BeeperService.ACTION_STOP);
        PendingIntent pendingIntentStopBeep = PendingIntent.getService(context,
                0, intentStopBeep, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.notification)
                .setColor(context.getResources().getColor(R.color.windowBackground))
                .setLargeIcon(bitmap)
                .setContentText(getStringRecource(R.string.notification_content) + " " + timeToBeep)
                .setContentTitle(getStringRecource(R.string.app_name))
                .setContentIntent(pendingIntent)
                .addAction(R.mipmap.stop_beep,
                        getStringRecource(R.string.notification_action_stop), pendingIntentStopBeep);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(NOTIFIC_ID, notification);
    }

    @NonNull
    private String getStringRecource(int id) {
        return context.getString(id);
    }

    public void cancelBeepNotific() {
        notificationManager.cancel(NOTIFIC_ID);
    }
}
