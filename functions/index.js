const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");
const {google} = require("googleapis");

admin.initializeApp();

// REPLACE THIS WITH YOUR ACTUAL PROJECT ID
const PROJECT_ID = "quokka-puff-events";
const SCOPES = ["https://www.googleapis.com/auth/firebase.messaging"];

// LOAD THE SERVICE ACCOUNT FILE YOU DOWNLOADED
const auth = new google.auth.GoogleAuth({
  keyFilename: "quokka-puff-events-firebase-adminsdk-fbsvc-a99c0e86dd.json",
  scopes: SCOPES,
});

exports.sendNotificationOnCreate = onDocumentCreated("notifications/{notifId}",
    async (event) => {
      const notif = event.data.data();
      if (!notif) return;

      // Get recipient's FCM token
      const userDoc = await admin.firestore()
          .collection("users")
          .doc(notif.recipient)
          .get();

      if (!userDoc.exists) return console.log("User not found.");

      const fcmToken = userDoc.data().fcmToken;
      if (!fcmToken) return console.log("No FCM token for user.");

      // Authorization for HTTP v1 API
      const client = await auth.getClient();
      const accessToken = await client.getAccessToken();

      const message = {
        message: {
          token: fcmToken,
          data: {
            title: notif.title,
            message: notif.message,
          },
        },
      };

      // Send the push notification
      await fetch(
          `https://fcm.googleapis.com/v1/projects/${PROJECT_ID}/messages:send`,
          {
            method: "POST",
            headers: {
              "Authorization": `Bearer ${accessToken.token}`,
              "Content-Type": "application/json",
            },
            body: JSON.stringify(message),
          },
      );

      console.log("FCM sent to:", notif.recipient);
    });
