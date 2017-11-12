package com.app.veraxe;

/**
 * Created by seocor1 on 9/14/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.app.veraxe.activities.Splash;
import com.app.veraxe.utils.AppUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by Hemanta on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static int BadgeCount=0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCM", "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData() != null) {
            //  Log.d("FCM", "Notification Message Body: " + remoteMessage.getNotification().getBody());
            Log.e("body", "*" + remoteMessage.getData());
            BadgeCount = AppUtils.getBadgeCount(getApplicationContext());
            BadgeCount++;
            AppUtils.setBadgeCount(getApplicationContext(), BadgeCount);
            AppUtils.setBadge(getApplicationContext(), AppUtils.getBadgeCount(getApplicationContext()));
            sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
//            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTag());
        }
    }


    private void sendNotification(String messageBody, String title,String body) {
        PendingIntent pendingIntent;
        Log.e("title", "**" + title);
        Random r = new Random();
        int when = r.nextInt(1000);
        Intent intent2 = new Intent(this, Splash.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent2,
                PendingIntent.FLAG_ONE_SHOT);

       // String title = "Veraxe";
        int currentAPIVersion = Build.VERSION.SDK_INT;
        int icon = 0;
        if (currentAPIVersion == Build.VERSION_CODES.KITKAT) {
            icon = R.drawable.ic_launcher;

        } else if (currentAPIVersion >= Build.VERSION_CODES.LOLLIPOP) {
            icon = R.drawable.ic_launcher;
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(when, notificationBuilder.build());
    }
}