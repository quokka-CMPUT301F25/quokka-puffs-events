package com.example.quokkapuffevents.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.controller.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.quokkapuffevents.model.Database;
import com.google.firebase.firestore.FirebaseFirestore;

public class FCMManager extends FirebaseMessagingService {


    /**
     * Called when a new FCM token is generated for this device/application instance.
     * This token is used to uniquely identify the device for sending push notifications.
     * The token is sent to Firestore and stored in the current user's document.
     *
     * @param token The token used for sending messages to this application instance.
     *              This token is the same as the one retrieved by
     *              {@link FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "New token: " + token);

        String userId = Database.getInstance().GetCurrentUserID();
        if (userId != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update("fcmToken", token);
        }
    }

    /**
     * Called when an FCM message is received while the app is in the foreground.
     * If the message contains a notification payload, Android shows it automatically.
     * If it contains a data payload, this method manually extracts the title and body
     * and displays a notification using {@link NotificationHelper}.
     *
     * @param message The incoming FCM message containing data and/or notification payload.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // If notification payload exists, Android will show it automatically.
        // If data payload exists, handle it manually:
        if (message.getNotification() != null) {
            String title = message.getData().get("title");
            String body = message.getData().get("message");

            //showNotification(title, body);
        }
    }

//    public static void showNotification(String title, String body) {
//        String channelId = "default_channel";
//        NotificationManager notificationManager = (NotificationManager) requireConext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.notifications_icon) // Replace with your notification icon
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true);
//
//        notificationManager.notify(0, builder.build());
//    }
}
