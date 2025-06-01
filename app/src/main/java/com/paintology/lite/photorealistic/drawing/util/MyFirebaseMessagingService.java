package com.paintology.lite.photorealistic.drawing.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.paintology.lite.photorealistic.drawing.Chat.ChatActivity;
import com.paintology.lite.photorealistic.drawing.Community.ShowPostFromNotification;
import com.paintology.lite.photorealistic.drawing.Model.NotificationModel;
import com.paintology.lite.photorealistic.drawing.R;
import com.paintology.lite.photorealistic.drawing.gallery.GalleryDashboard;
import com.paintology.lite.photorealistic.drawing.minipaint.FirstScreen;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;
    private int numChatMessages = 0;

    StringConstants _constant = new StringConstants();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (GalleryDashboard.mActivity == null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Map<String, String> data = remoteMessage.getData();
            try {
                sendNotification(notification, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


       /* boolean isFromChat = false;
        try {
            String sented = remoteMessage.getData().get("sented");
            if (sented != null && !sented.isEmpty()) {
                isFromChat = true;
            }

            boolean isFromComment = false;


            if (remoteMessage.getData().get("isFromComment") != null && !remoteMessage.getData().get("isFromComment").isEmpty() && remoteMessage.getData().get("isFromComment").equalsIgnoreCase("true")) {
                isFromComment = true;
            }
            Log.e("TAGG", "Notification received instance " + (ChatActivity._objInterface == null) + " isFromChat " + isFromChat + " remoteMessage " + remoteMessage.getData());
            if (ChatActivity._objInterface == null && sented != null && !sented.isEmpty() && MyApplication._realTimeDbUtils.getCurrentUser() != null && sented.equals(MyApplication._realTimeDbUtils.getCurrentUser().getUid())) {
                sendMsgNotification(remoteMessage, isFromComment);
                return;
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception at receive " + e.getMessage());
        }
        if (!isFromChat) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Map<String, String> data = remoteMessage.getData();
            Log.e("TAGGG", "Data Payload: " + remoteMessage.getData().size());
            try {
                sendNotification(notification, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


    @Override
    public void onNewToken(@NonNull String refreshtoken) {
        FireUtils.updateToken(this,refreshtoken);
    }

    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {

        try {
            String notification_id = "";
            String target_type = "", target_name = "", target_id = "";
            String title = "", body = "";

            boolean hasData = false;

            try {
                JSONObject _obj = new JSONObject(data);

                if (_obj.has("title"))
                    title = _obj.getString("title");

                if (_obj.has("body"))
                    title = _obj.getString("body");

                if (_obj.has("notification_id")) {
                    notification_id = _obj.getString("notification_id");
                }

                if (_obj.has("target_type")) {
                    hasData = true;
                    target_type = _obj.getString("target_type");
                }

                if (_obj.has("target_name")) {
                    hasData = true;
                    target_name = _obj.getString("target_name");
                }

                if (_obj.has("target_id")) {
                    hasData = true;
                    target_id = _obj.getString("target_id");
                }

            } catch (Exception e) {
                Log.e("TAGG", "Exception " + e.getMessage());
            }


            Intent intent = new Intent(this, FirstScreen.class);
            if (hasData) {
                Bundle bundle = new Bundle();
                bundle.putString("notification_id", notification_id);
                bundle.putString("target_type", target_type);
                bundle.putString("target_name", target_name);
                bundle.putString("target_id", target_id);
                intent.putExtras(bundle);
            }

            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getActivity(this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


            if (title.equalsIgnoreCase("")) {
                title = getString(R.string.app_name);
            }

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentInfo(getString(R.string.app_name))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.p_logo_small_red))
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setLights(Color.RED, 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSound(uri)
                    .setNumber(++numMessages)
                    .setSmallIcon(R.drawable.p_logo_small_red);
            notificationBuilder.setContentIntent(pendingIntent);

            try {
                String picture = data.get(FCM_PARAM);
                if (picture != null && !picture.isEmpty()) {
                    URL url = new URL(picture);
                    Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    notificationBuilder.setStyle(
                            new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        getString(R.string.default_notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESC);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }


            assert notificationManager != null;
            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception e) {
            Log.e("TAG", "Excetption at sendNoti " + e.getMessage());
        }
    }


    public void sendMsgNotification(RemoteMessage remoteMessage, Boolean isFromComment) {
        try {
            Log.e("TAG", "Call Notification to comment and chat ");
            String user = remoteMessage.getData().get("user");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String post_id = remoteMessage.getData().get("post_id");

            int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
            PendingIntent pendingIntent;
            if (isFromComment) {
                Bundle bundle = new Bundle();
                bundle.putString("post_id", post_id);
                Intent intent = new Intent(this, ShowPostFromNotification.class);
                intent.putExtras(bundle);
//                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    pendingIntent = PendingIntent.getActivity(this,
                            0,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    pendingIntent = PendingIntent.getActivity(this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                }
            } else {
                Intent intent = new Intent(this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userUid", user);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
                pendingIntent = PendingIntent.getActivity(this,
                        j,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);
            }

            /*Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(isFromComment ? "Community" : "Chat")
                    .setContentText(isFromComment ? body : title + " says... " + body)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.p_logo_small_red))
                    .setSmallIcon(R.drawable.p_logo_small_red)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setNumber(++numChatMessages)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        getString(R.string.app_name), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESC);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            int i = 0;
            if (j > 0) {
                i = j;
            }
            notificationManager.notify(i, builder.build());*/

            String contentTitle = isFromComment ? "Community" : "Chat";
            String contentText = isFromComment ? TextUtils.isEmpty(body) ? "Someone commented on your post" : body : title + " says... " + body;

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setContentTitle("Paintology")
                    .setContentText(title)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.p_logo_small_red))
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setLights(Color.RED, 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSound(uri)
                    .setNumber(++numMessages)
                    .setSmallIcon(R.drawable.p_logo_small_red);

            notificationBuilder.setContentIntent(pendingIntent);

            NotificationModel model = new NotificationModel(
                    contentTitle,
                    contentText,
                    isFromComment ? NotificationType.COMMENT : NotificationType.CHAT,
                    isFromComment ? post_id : null,
                    isFromComment ? null : user
            );

            AppUtils.saveNotificationLocally(getApplicationContext(), model);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        getString(R.string.default_notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESC);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }


            assert notificationManager != null;
            notificationManager.notify(0, notificationBuilder.build());

        } catch (Exception e) {
            Log.e("TAG", "Exception at show noti " + e.getMessage());
        }
    }

}
