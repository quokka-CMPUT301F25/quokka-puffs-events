package com.example.quokkapuffevents.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.quokkapuffevents.R;

public class NotificationHelper {

    /**
     * Displays a notification with the provided title and message.
     * If running on Android Oreo (API 26) or higher, a notification channel is created
     * before sending the notification. A unique notification ID based on the current
     * timestamp is used to ensure each notification is shown separately.
     *
     * @param context The context used to access system services.
     * @param title   The title displayed in the notification.
     * @param message The body text displayed in the notification.
     */
    public static void showNotification(Context context, String title, String message) {
        String CHANNEL_ID = "notificationChannelID";
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Quokka Puff Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon) // your icon
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .build();

        manager.notify((int) System.currentTimeMillis(), notification);
    }
}
