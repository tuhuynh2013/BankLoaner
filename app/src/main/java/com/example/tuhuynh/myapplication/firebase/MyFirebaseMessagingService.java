package com.example.tuhuynh.myapplication.firebase;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.tuhuynh.myapplication.customer.CustomerHomeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("NEW_TOKEN", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                handleDataMessage(remoteMessage.getData());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    private void handleNotification(String title, String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent resultIntent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
            showNotificationMessage(getApplicationContext(), title, message, null, resultIntent);
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        try {
            String title = data.get("title");
            String message = data.get("message");
            String imageUrl = data.get("imageUrl");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "imageUrl: " + imageUrl);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, null, resultIntent);
                } else {
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, null, resultIntent, imageUrl);
                }
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), CustomerHomeActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, null, resultIntent);
                } else {
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, null, resultIntent, imageUrl);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


}
