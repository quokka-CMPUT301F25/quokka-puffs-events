package com.example.quokkapuffevents.controller;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.quokkapuffevents.R;
import com.example.quokkapuffevents.model.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.example.quokkapuffevents.model.Database;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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
     * and displays a notification.
     *
     * @param message The incoming FCM message containing data and/or notification payload.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Database db = Database.getInstance();
        db.GetUser(db.GetCurrentUserID(), user -> {
            if (user.getSendNotifications()) {
                SendNotification(message, user);
            }
        });
    }

    public void SendNotification(RemoteMessage message, User user) {
        if (user == null) {
            Log.d("FCM_TEST", "Message received: " + message.getData());

            // Confirm that the message arrived
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "FCM RECEIVED!", Toast.LENGTH_SHORT).show()
            );

            String title = message.getData().get("title");
            String text = message.getData().get("message");
            if (title == null) title = "New Notification";
            if (text == null) text = "No message";

            // CHANNEL – MUST exist before notify()
            String channelId = "EventNotificationChannel";
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Event Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(channel);

            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EventNotificationChannel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)           // SAFE icon
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_MAX)              // MUST be max
                    .setCategory(NotificationCompat.CATEGORY_CALL)            // CALL = stronger display
                    .setFullScreenIntent(pendingIntent, true)                 // FORCE popup
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL);                   // sound + vibration


            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.w("FCM_TEST", "Permission missing – returning.");
                return;
            }

            managerCompat.notify(10001, builder.build());
        }

    }
}
