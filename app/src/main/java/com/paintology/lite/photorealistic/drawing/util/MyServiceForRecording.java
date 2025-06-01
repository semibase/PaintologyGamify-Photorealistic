package com.paintology.lite.photorealistic.drawing.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.minipaint.PaintActivity;

public class MyServiceForRecording extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        return START_NOT_STICKY;
    }

    private void showNotification() {
        try {
            Intent notificationIntent = new Intent(this, PaintActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getActivity(this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE);

            String NOTIFICATION_CHANNEL_ID = "com.paintology.lite";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "My Background Service";
                NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                assert manager != null;
                if (manager != null) {
                    manager.createNotificationChannel(chan);

                    Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("Recording Service")
                            .setContentText("Running")
                            .setSmallIcon(R.drawable.p_logo_small_red)
                            .setContentIntent(pendingIntent)
                            .build();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
                    } else {
                        startForeground(1, notification);
                    }
                }
            } else {
                Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle("Recording Service")
                        .setContentText("Running")
                        .setSmallIcon(R.drawable.p_logo_small_red)
                        .setContentIntent(pendingIntent)
                        .build();
                startForeground(1, notification);
            }

//            startForeground(1, notification);
        } catch (Exception e) {
            Log.e("TAG", "Exception at start  MyServiceForRecording " + e.getMessage());
        }
    }
}
