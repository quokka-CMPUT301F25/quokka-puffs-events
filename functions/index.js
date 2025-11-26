// index.js (Firebase v2 Cloud Function)

const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");

initializeApp();

exports.sendNotificationOnCreate = onDocumentCreated("notifications/{notifId}",
    async (event) => {
      const notif = event.data.data(); // this is your Firestore document data

      if (!notif || !notif.recipient) {
        console.log("No recipient found — skipping");
        return;
      }

      const db = getFirestore();

      // Fetch recipient token
      const userDoc = await db.collection("users").doc(notif.recipient).get();
      if (!userDoc.exists) {
        console.log("User not found:", notif.recipient);
        return;
      }

      const token = userDoc.get("fcmToken");
      if (!token) {
        console.log("No FCM token for user:", notif.recipient);
        return;
      }

      // Send push notification
      const message = {
        token,
        notification: {
          title: notif.title || "New Notification",
          body: notif.message || "",
        },
      };

      try {
        const response = await getMessaging().send(message);
        console.log("SUCCESS:", response);
      } catch (err) {
        console.error("ERROR sending FCM:", err);
      }
    });
