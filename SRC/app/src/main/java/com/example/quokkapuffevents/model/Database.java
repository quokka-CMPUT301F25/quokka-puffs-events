package com.example.quokkapuffevents.model;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import com.example.quokkapuffevents.R;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;


public class Database {
    private static volatile Database instance;
    private String userID; //current id of the user of the app
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private CollectionReference notifsRef;
    private FirebaseStorage imageDB;


    public Database() {
        this.db = FirebaseFirestore.getInstance(); //Get database
        this.imageDB = FirebaseStorage.getInstance(); //Get Storage

        this.usersRef = db.collection("users");
        this.eventsRef = db.collection("events");
        this.notifsRef = db.collection("notifications");

    }

    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    public void SetUserID(String userID) {
        this.userID = userID;
    }

    public String GetCurrentUserID() {
        return userID;
    }

    //Base creation methods
    public User CreateUser(String email, Integer type, String hashPass, String userName, String firstName, String lastName, String phoneNumber){
        /**
         * Creates a new user and ensures that the user has a proper ID and is in the firebase database
         * @param email
         * This is the email address that the user has chosen
         * @param type
         * This denotes the type of account the user is. -1 for admin, 0 for participant, and 1 for organizer
         * @param hashPass
         * This is the hashed password of the user
         * @param userName
         * This is the username that the user has chosen
         * @param firstName
         * This is the first name of the user
         * @param lastName
         * This is the last name of the user
         * @param phoneNumber
         * This is the phone number that the user has chosen
         * @return
         * Returns the user as a new Class. Ensures that the user is saved to the cloud
         */
        String id = usersRef.document().getId(); //Creates a document and returns the id
        User newUser = new User(id, email, type, hashPass, userName, firstName, lastName, phoneNumber); //Creates new User class
        usersRef.document(id).set(newUser); //Overwrites id in database with new user data
        return(newUser);
    }

    public Event CreateEvent(String name, String org, String description, Integer toBeDrawn, Integer maxNumWaitlist, Date drawnDate, Date endDate){
        /**
         * Creates a new event and saves the new events data to the database
         * @param org
         * This is the user that is creating the event
         * @param description
         * This is a description of the event
         * @param toBeDrawn
         * This is the number of people to be drawn at the end
         * @param maxNumWaitlist
         * This is an optional value that denotes a max for the number of people that can sign up for this event
         * @param startDate
         * This is the events start date
         * @param endDate
         * This is the events end date
         * @return
         * Returns the event as a new Class. Ensures that the event is saved to the cloud
         */
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(id, name, org, description, toBeDrawn, maxNumWaitlist, drawnDate, endDate); //This version has the max on the size of the waitlsit
        eventsRef.document(id).set(newEvent);

        GetUser(userID, user -> {
            user.addEvent(id);
            SaveUser(user);
        });
        return(newEvent);
    }
    public Event CreateEvent(String name, String org, String description, Integer toBeDrawn, Date drawnDate, Date endDate){
        /**
         * Same as the other create event but does not construct it with the optional cap on number of participants
         * @param org
         * This is the user that is creating the event
         * @param description
         * This is a description of the event
         * @param toBeDrawn
         * This is the number of people to be drawn at the end
         * @param startDate
         * This is the events start date
         * @param endDate
         * This is the events end date
         * @return
         * Returns the event as a new Class. Ensures that the event is saved to the cloud
         */
        Log.d("Test", "Testing something");
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(id, name, org, description, toBeDrawn, drawnDate, endDate); //This version has the max on the size of the waitlsit
        eventsRef.document(id).set(newEvent);

        GetUser(userID, user -> {
            user.addEvent(id);
            SaveUser(user);
        });
        return newEvent;
    }

    String CHANNEL_ID = "notificationChannelID";


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Source - https://stackoverflow.com/a
            // Posted by k1r4n
            // Retrieved 2025-11-19, License - CC BY-SA 4.0
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            CharSequence name = "notificationChannel";
            String description = "This is a notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notif CreateNotification(Integer type, String recipient, String originEvent, String originUser, String message, Context context){
        /**
         * Creates a new notification and saves the new notif data to the database
         * @param type
         * This is the type of notifcation. 1 means that this is an invitiation while anything else means that it is simply a message
         * @param recipient
         * This is the user that the message is to
         * @param originEvent
         * This is the event that the notification is referencing
         * @param originUser
         * This is the organizer of the event that is sending the notification
         * @param message
         * This is what is being sent in the message
         * @return
         * Returns the notification as a new Class. Ensures that the notification is saved to the cloud
         */
        String id = notifsRef.document().getId(); //Creates a document and returns the id
        Notif newNotif = new Notif(id, type, recipient, originEvent, originUser, message);
        notifsRef.document(id).set(newNotif);

        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("Quokka Puffs Events")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return(newNotif);
    }
    public void UploadImageToDatabase(Bitmap bitmap, OnSuccessListener<Uri> listener){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String path = "Images/ " + UUID.randomUUID() + ".jpeg";
        StorageReference refImage = imageDB.getReference(path);

        UploadTask uploadTask = refImage.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri uri = taskSnapshot.getUploadSessionUri();
                listener.onSuccess(uri);
            }
        });
    }

    public void GetUser(String userID, OnSuccessListener<User> listener) {
        usersRef.document(userID).get().addOnSuccessListener(document -> {
            if(document.exists()){
                User user = document.toObject(User.class);
                listener.onSuccess(user);
            }
        });
    }

    //TODO Make these prettier. They stink right now
    public void GetEvent(String eventID, OnSuccessListener<Event> listener) {
        eventsRef.document(eventID).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Event event = document.toObject(Event.class);
                listener.onSuccess(event);
            }
        });
    }
    public void GetNotification(String notifID, OnSuccessListener<Notif> listener) {
        /**
         * This method collects the most up to date data from the database of notif based on their id
         * @param notifID
         * The id of the notif being searched for
         * @return
         * Returns the notification in a Notif class. The return will have the most up to date data for the notification id
         */
        notifsRef.document(notifID).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Notif notif = document.toObject(Notif.class);
                listener.onSuccess(notif);
            }
        });
    }

    public void GetImage(Uri uri, OnSuccessListener<Bitmap> listener) {
        /**
         * This method collects the image from an event
         * @param event
         * The event that the image is from
         * @return
         * Returns the notification in a Notif class. The return will have the most up to date data for the notification id
         */
        StorageReference refImage = imageDB.getReference(String.valueOf(uri));
        final File localfile = new File(UUID.randomUUID() + ".jpeg");
        refImage.getFile(localfile).addOnSuccessListener(taskSnapshot -> {
            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
            listener.onSuccess(bitmap);
        });
    }

    public void SaveUser(User user){
        usersRef.document(user.getId()).set(user);
    }
    public void SaveEvent(Event event){
        eventsRef.document(event.getId()).set(event);
    }
    public void SaveNotif(Notif notif){
        notifsRef.document(notif.getId()).set(notif);
    }

    //Two version to allow us to use both the object or just their id
    public void DeleteUser(User user){
        /**
         * Deletes the provided user from the firebase database
         * @param user
         * This is the user that is being deleted
         */
        usersRef.document(user.getId()).delete();
    }
    public void DeleteUser(String id){
        /**
         * Deletes the provided user from the firebase database
         * @param id
         * This is the id of the user that is being deleted
         */
        usersRef.document(id).delete();
    }
    public void DeleteEvent(Event event){
        /**
         * Deletes the provided event from the firebase database
         * @param event
         * This is the event that is being deleted
         */
        eventsRef.document(event.getId()).delete();
    }
    public void DeleteEvent(String id){
        /**
         * Deletes the provided event from the firebase database
         * @param id
         * This is the id of the event that is being deleted
         */
        eventsRef.document(id).delete();
    }
    public void DeleteNotification(Notif notif){
        /**
         * Deletes the provided notif from the firebase database
         * @param notif
         * This is the notif that is being deleted
         */
        notifsRef.document(notif.getId()).delete();
    }
    public void DeleteNotification(String id){
        /**
         * Deletes the provided notif from the firebase database
         * @param id
         * This is the id of the notif that is being deleted
         */
        notifsRef.document(id).delete();
    }

    //Extrapolated Date Methods
    //TODO: Test all of these:
    public void ListEvents(OnSuccessListener<ArrayList<Event>> listener){
        /**
         * This method provides a list of every event that is in the database
         * @return
         * Returns an ArrayList that holds all of the known events
         */
        //Collects the data for every user with an id in the above list
        eventsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> events = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            events.add(doc.toObject(Event.class));
                        }
                        listener.onSuccess(events);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void ListUsers(OnSuccessListener<ArrayList<User>> listener){
        /**
         * This method provides a list of every event that is in the database
         * @return
         * Returns an ArrayList that holds all of the known events
         */
        //Collects the data for every user with an id in the above list
        usersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            users.add(doc.toObject(User.class));
                        }
                        listener.onSuccess(users);
                    } else {
                        Log.e("Firestore", "Error getting user list", task.getException());
                    }
                });
    }

    public void ListNotifs(OnSuccessListener<ArrayList<Notif>> listener){
        /**
         * This method provides a list of every notification that is in the database
         * @return
         * Returns an ArrayList that holds all of the known notifications
         */
        //Collects the data for every user with an id in the above list
        notifsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Notif> notifs = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            notifs.add(doc.toObject(Notif.class));
                        }
                        listener.onSuccess(notifs);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void UsersInEvent(Event event, OnSuccessListener<ArrayList<User>> listener){
        /**
         * This method provides a list of every user that is signed up to an event
         * @param event
         * This is the event that is being looked at. The users returned will have signed up to this event
         * @return
         * Returns an ArrayList that holds all of the users that have signed up for this event
         */
        //List of all users in the event
        List usersInEvent = (List) event.getEventUsers().keySet();

        //Collects the data for every user with an id in the above list
        usersRef.whereIn("id", (List) usersInEvent).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            users.add(doc.toObject(User.class));
                        }
                        listener.onSuccess(users);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void GetEventsFromUser(User user, OnSuccessListener<ArrayList<Event>> listener){
        /**
         * This method provides a list of every event that a user has signed up for
         * @param user
         * This is the user that is being looked at
         * @return
         * Returns an ArrayList that holds all of the events that the user has signed up for
         */
        //List of all users in the event
        List eventsOfUser = user.getEvents();
        eventsOfUser.add("Dummy");

        //Collects the data for every user with an id in the above list
        eventsRef.whereIn("id", eventsOfUser).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Event> events = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            events.add(doc.toObject(Event.class));
                        }
                        listener.onSuccess(events);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    //TODO Add logic for sending out notifications. Need Testing
    public void DrawUsers(Event event, OnSuccessListener<ArrayList<User>> listener){
        /**
         * This method draws the correct number of people for an event. It is the random raffle mechanism
         * @param event
         * The event that is randomly selecting participents from its waiting list
         * @return
         * Returns an Array List containing all of the chosen users
         */
        //Collect User IDs
        ArrayList<String> userID = event.drawUsers(-1);
        //Make into Users
        usersRef.whereIn("id", (List) userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            users.add(doc.toObject(User.class));
                        }
                        listener.onSuccess(users);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void RedrawUsers(Event event, Integer numToDraw, OnSuccessListener<ArrayList<User>> listener){
        /**
         * This method is used to redraw a specific number of participents. It is used after an event as already drawn the majority of its users
         * It allows for gaps caused by people cancelling or rejecting to be filled
         * @param event
         * The event that is randomly selecting participents from its waiting list
         * @param numToDraw
         * The number of new users from the waiting list to be drawn
         * @return
         * Returns an Array List containing all of the newly chosen users
         */
        //Collect User IDs
        ArrayList<String> userID = event.drawUsers(numToDraw);
        //Make into Users
        usersRef.whereIn("id", (List) userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            users.add(doc.toObject(User.class));
                        }
                        listener.onSuccess(users);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    //Notif Methods
    public void GetUserNotifications(User user, OnSuccessListener<ArrayList<Notif>> listener) {
        /**
         * This method collects and returns all of the notifications that have been sent to a user
         * @param user
         * The user that the notifications have been sent to
         * @return
         * Returns an Array List containing all of the notifications
         */
        notifsRef.whereEqualTo("recipient", user.getId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Notif> notifs = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            notifs.add(doc.toObject(Notif.class));
                        }
                        listener.onSuccess(notifs);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void ToggleNotifsForUser(User user){
        user.setSendNotifications(!(user.getSendNotifications()));
        SaveUser(user);
    }

    public void ValidatePasswordByEmail(String email, String password, OnSuccessListener<ArrayList<User>> listener){
        usersRef.whereEqualTo("email", email)
                .whereEqualTo("hashPassword", password)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<User> user = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            user.add(doc.toObject(User.class));
                        }
                        listener.onSuccess(user);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void ValidateUserUsername(String username, String password, OnSuccessListener<ArrayList<User>> listener){
        usersRef.whereEqualTo("userName", username)
                .whereEqualTo("hashPassword", password)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<User> user = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            user.add(doc.toObject(User.class));
                        }
                        listener.onSuccess(user);
                    } else {
                        Log.e("Firestore", "Error getting notifications", task.getException());
                    }
                });
    }

    public void UserExists(String val, OnSuccessListener<Boolean> listener){
        usersRef.where(Filter.or(
                Filter.equalTo("username", val),
                Filter.equalTo("email", val)
                ))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(true);
                    } else {
                        listener.onSuccess(false);
                    }
                });
    }

    public void FilteredEventsForUser(User user, String status, OnSuccessListener<ArrayList<Event>> listener){
        ArrayList<Event> waitingEvents = new ArrayList<>(); //Create empty list to hold users that are still on the waiting list
        //Collect all users from the eventUsers Map that is still waiting
        for (String e : user.getEvents()) {
            GetEvent(e, event -> {
                if (Objects.equals(event.getEventUsers().get(user.getId()), status)) {
                    waitingEvents.add(event);
                }
                listener.onSuccess(waitingEvents);
            });

        }
    }

    public void BlockThreadUntilCompleted(CollectionReference ref, String id, Object obj) {
        CountDownLatch latch = new CountDownLatch(1);

        ref.document(id).set(obj)
                .addOnSuccessListener(unused -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());

        try {
            latch.await();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

}