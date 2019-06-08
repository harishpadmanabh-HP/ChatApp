package com.hp.hp.chatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class Firbasemessagingservice extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
               // handleNow();
            }

        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, remoteMessage.getNotification().getBody())
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle(remoteMessage.getNotification().getBody())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        try {
            issueNotification(remoteMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void scheduleJob() {
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    void makeNotificationChannel(String id, String name, int importance)
    {
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true); // set false to disable badges, Oreo exclusive

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
    void issueNotification(RemoteMessage remoteMessage) throws JSONException {

        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel("CHANNEL_1", "Example channel", NotificationManager.IMPORTANCE_DEFAULT);
        }
        // the check ensures that the channel will only be made
        // if the device is running Android 8+

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, "CHANNEL_1");
        // the second parameter is the channel id.
        // it should be the same as passed to the makeNotificationChannel() method
        // String questionTitle = .get("questionTitle").toString();

        //String d=object.getString("message");
        // JSONObject json = new JSONObject(remoteMessage.getData().toString());
        //  String d=json.getString("msg");
        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        String callNumber = object.getString("message");

// Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
// Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_launcher_foreground);
        notification
                .setSmallIcon(R.mipmap.ic_launcher) // can use any other icon
                .setContentTitle("SMART AMBULANCE")
//                .setContentText(remoteMessage.getData().toString())
                .setContentText(callNumber)
                .setContentIntent(resultPendingIntent)
                .setNumber(3); // this shows a number in the notification dots

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, notification.build());
        // it is better to not use 0 as notification id, so used 1.
    }
}
