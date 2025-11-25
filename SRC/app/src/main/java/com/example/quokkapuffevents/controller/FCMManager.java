package com.example.quokkapuffevents.fcm;

import android.util.Log;
import androidx.annotation.NonNull;

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
        if (!message.getData().isEmpty()) {
            String title = message.getData().get("title");
            String body = message.getData().get("message");

            NotificationHelper.showNotification(getApplicationContext(), title, body);
        }
    }
}
