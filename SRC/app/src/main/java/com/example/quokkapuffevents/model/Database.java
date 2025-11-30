package com.example.quokkapuffevents.model;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class Database {
    private static volatile Database instance;
    protected String userID; //current id of the user of the app
    protected FirebaseFirestore db;
    protected CollectionReference usersRef;
    protected CollectionReference eventsRef;
    protected CollectionReference notifsRef;
    protected StorageReference imageDB;


    /**
     * Default constructor for Database.
     */
    public Database() {
        this.db = FirebaseFirestore.getInstance(); //Get database
        this.imageDB = FirebaseStorage.getInstance().getReference("Uploads"); //Get Storage

        this.usersRef = db.collection("users");
        this.eventsRef = db.collection("events");
        this.notifsRef = db.collection("notifications");

    }

    /**
     * Creates a new static instance of the Database connection.
     * @return
     * The database instance.
     */
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

    public FirebaseFirestore getDb() {
        return db;
    }

    // Getters and Setters

    public void SetUserID(String userID) {
        this.userID = userID;
    }

    public String GetCurrentUserID() {
        return userID;
    }

    //Base creation methods

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
    public User CreateUser(String email, Integer type, String hashPass, String userName, String firstName, String lastName, String phoneNumber, ArrayList<Double> location){
        String id = usersRef.document().getId(); //Creates a document and returns the id
        User newUser = new User(id, email, type, hashPass, userName, firstName, lastName, phoneNumber, location); //Creates new User class
        GenerateTokenForUser(newUser);
        usersRef.document(id).set(newUser); //Overwrites id in database with new user data
        return(newUser);
    }

    private void GenerateTokenForUser(User newUser){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener((task) -> {
                    if (!task.isSuccessful()) {
                        newUser.setFcmToken(task.getException().getMessage());
                        Log.e("FCM", "Fetching FCM registration token failed", task.getException());
                    } else {
                        newUser.setFcmToken(task.getResult());
                        Log.d("FCM", "FCM registration token: " + task.getResult());
                    }

                    SaveUser(newUser);
                });
    }

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
     * @param drawnDate
     * This is the events start date
     * @param endDate
     * This is the events end date
     * @return
     * Returns the event as a new Class. Ensures that the event is saved to the cloud
     */
    public Event CreateEvent(String name, String org, String description, Integer toBeDrawn, Integer maxNumWaitlist, Date drawnDate, Date endDate, Double lat, Double lng, int lockRadius){
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(id, name, org, description, toBeDrawn, maxNumWaitlist, drawnDate, endDate, lat, lng, lockRadius); //This version has the max on the size of the waitlsit
        eventsRef.document(id).set(newEvent);

        GetUser(userID, user -> {
            user.addEvent(id);
            SaveUser(user);
        });
        return(newEvent);
    }

    /**
     * Same as the other create event but does not construct it with the optional cap on number of participants
     * @param org
     * This is the user that is creating the event
     * @param description
     * This is a description of the event
     * @param toBeDrawn
     * This is the number of people to be drawn at the end
     * @param drawnDate
     * This is the events start date
     * @param endDate
     * This is the events end date
     * @return
     * Returns the event as a new Class. Ensures that the event is saved to the cloud
     */
    public Event CreateEvent(String name, String org, String description, Integer toBeDrawn, Date drawnDate, Date endDate, Double lat, Double lng, int lockRadius){
        Log.d("Test", "Testing something");
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(id, name, org, description, toBeDrawn, drawnDate, endDate, lat, lng, lockRadius); //This version has the max on the size of the waitlsit
        eventsRef.document(id).set(newEvent);

        GetUser(userID, user -> {
            user.addEvent(id);
            SaveUser(user);
        });
        return newEvent;
    }

    /**
     * Creates a new notification and saves the new notif data to the database
     * @param type
     * This is the type of notification. 1 means that this is an invitation while anything else means that it is simply a message
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
    public Notif CreateNotification(Integer type, String recipient, String originEvent, String originUser, String message, String title){
        String id = notifsRef.document().getId(); //Creates a document and returns the id
        Notif newNotif = new Notif(id, type, recipient, originEvent, originUser, message, title);
        notifsRef.document(id).set(newNotif);
        return(newNotif);
    }

    /**
     * Uploads an image to the firebase storage and returns a listener that returns the id/location of the image to update an event
     * @param bitmap
     * Bitmap of the image being uploaded
     * @param listener
     * Listener so that the upload can be asynchronous
     */
    public void UploadImageToDatabase(Bitmap bitmap, OnSuccessListener<String> listener){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String path = UUID.randomUUID() + ".jpg";
        StorageReference refImage = imageDB.child(path);

        UploadTask uploadTask = refImage.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                listener.onSuccess(path);
            }
        });
    }

    /**
     * Collects a user's info from their user id
     * @param userID
     * ID of the user being collected
     * @param listener
     * Listener so that the upload can be asynchronous
     */
    public void GetUser(String userID, OnSuccessListener<User> listener) {
        usersRef.document(userID).get().addOnSuccessListener(document -> {
            if(document.exists()){
                User user = document.toObject(User.class);
                listener.onSuccess(user);
            }
        });
    }

    /**
     * Grabs the currently selected event from the database.
     * @param eventID
     * ID of the event being collected
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void GetEvent(String eventID, OnSuccessListener<Event> listener) {
        eventsRef.document(eventID).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Event event = document.toObject(Event.class);
                listener.onSuccess(event);
            }
        });
    }

    /**
     * Collects the most up to date data from the database of notif based on their notification id.
     * @param notifID
     * The id of the notif being searched for
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void CheckNotification(String notifID, OnSuccessListener<Notif> listener) {
        notifsRef.document(notifID).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                Notif notif = document.toObject(Notif.class);
                listener.onSuccess(notif);
            }
        });
    }

    /**
     * Collects an image from the storage using it's location/id
     * @param path
     * ID/Path to the image in the storage
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void GetImage(String path, OnSuccessListener<Bitmap> listener) {
        if (path != null){
            StorageReference refImage = imageDB.child(path);
            refImage.getBytes(1000000000).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                listener.onSuccess(bmp);
            }).addOnFailureListener(e -> {
                Log.e("IMAGES", "Download failed", e);
            });
        }
    }

    /**
     * Overwrites the values of a user. Updates the values in the database
     * @param user
     * User class with the updated values
     */
    public void SaveUser(User user){
        usersRef.document(user.getId()).set(user);
    }

    /**
     * Overwrites the values of a event. Updates the values in the database
     * @param event
     * Event class with the updated values
     */
    public void SaveEvent(Event event){
        eventsRef.document(event.getId()).set(event);
    }

    /**
     * Overwrites the values of a user. Updates the values in the database
     * @param notif
     * Notif class with the updated values
     */
    public void SaveNotif(Notif notif){
        notifsRef.document(notif.getId()).set(notif);
    }

    //Two version of every delete function to allow us to use either the object itself or just their individual id

    /**
     * Deletes the provided user from the firebase database using the specified object.
     * @param user
     * This is the user that is being deleted.
     */
    public void DeleteUser(User user){
        usersRef.document(user.getId()).delete();
    }

    /**
     * Deletes the provided user from the firebase database using the user id.
     * @param id
     * This is the id of the user that is being deleted.
     */
    public void DeleteUser(String id){
        usersRef.document(id).delete();
    }

    /**
     * Deletes the provided event from the firebase database using the specified object.
     * @param event
     * This is the event that is being deleted.
     */
    public void DeleteEvent(Event event){
        DeleteImage(event.getImageID());
        DeleteImage(event.getQrcodeID());
        eventsRef.document(event.getId()).delete();
    }

    /**
     * Deletes the provided event from the firebase database using the event id.
     * @param id
     * This is the id of the event that is being deleted.
     */
    public void DeleteEvent(String id){
        GetEvent(id, this::DeleteEvent);
    }

    /**
     * Deletes the provided notification from the firebase database using the specified object.
     * @param notif
     * This is the notification that is being deleted.
     */
    public void DeleteNotification(Notif notif){
        notifsRef.document(notif.getId()).delete();
    }

    /**
     * Deletes the provided notification from the firebase database using the notification id.
     * @param id
     * This is the id of the notification that is being deleted.
     */
    public void DeleteNotification(String id){
        notifsRef.document(id).delete();
    }

    /**
     * Deletes the provided image from the firebase storage using the image id.
     * @param path
     * ID/Location to the image in the storage
     */
    public void DeleteImage(String path){
        if (path != null) {
            imageDB.child(path).delete();
        }
    }

    /**
     * Provides a list of every event that is in the database
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void ListEvents(OnSuccessListener<ArrayList<Event>> listener){
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

    /**
     * Provides a list of every user that is in the database.
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void ListUsers(OnSuccessListener<ArrayList<User>> listener){
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

    /**
     * Provides a list of every notification that is in the database.
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void ListNotifs(OnSuccessListener<ArrayList<Notif>> listener){
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

    /**
     * Provides a list of every user that is signed up to an event.
     * @param event
     * This is the event that is being looked at. The users returned will have signed up to this event
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void UsersInEvent(Event event, OnSuccessListener<ArrayList<User>> listener){
        //List of all users in the event
        List<String> usersInEvent = new ArrayList<>(event.getEventUsers().keySet());

        //If event has no users
        if (usersInEvent.isEmpty()) {
            listener.onSuccess(new ArrayList<>());
            return;
        }
        //Collects the data for every user with an id in the above list

        usersRef.whereIn("id", usersInEvent).get() // edited it as well -- KYLE
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

    /**
     * Provides a list of every event that a user has signed up for.
     * @param user
     * This is the user that is being looked at.
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void GetEventsFromUser(User user, OnSuccessListener<ArrayList<Event>> listener){
        //List of all users in the event
        List<String> eventsOfUser = user.getEvents();
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

    /**
     * Draws the correct number of people for an event. It is the random raffle mechanism.
     * This method is called when the event is first drawn.
     * Tries to invite as many users as possible
     * @param event
     * The event that is randomly selecting participants from its waiting list.
     */
    public void DrawUsers(Event event){
        //Collect User IDs. Not directly used for anything but could be in the future
        ArrayList<String> userIDs = event.drawUsers(-1);
        event.setDrawn(true);
        SaveEvent(event);
    }
    /**
     * Used to redraw a specific number of participants. Used after an event has already drawn the
     * majority of its users. Allows for gaps caused by people cancelling or rejecting to be filled.
     * @param event
     * The event that is randomly selecting participants from its waiting list
     * @param numToDraw
     * The number of new users from the waiting list to be drawn
     */
    public void RedrawUsers(Event event, Integer numToDraw){
        //Collect User IDs
        ArrayList<String> userIDs = event.drawUsers(numToDraw);
        SaveEvent(event);
    }

    /**
     * This method collects and returns all of the notifications that have been sent to a user
     * @param user
     * The user that the notifications have been sent to
     * @param listener
     * Listener so that the download can be asynchronous
     */
    public void GetUserNotifications(User user, OnSuccessListener<ArrayList<Notif>> listener) {
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

    /**
     * Method to find and return a user based on an email and password
     * @param email
     * Email of the user being looked for
     * @param password
     * Password of the user being looked for
     * @param listener
     * Listener so that the download can be asynchronous
     */
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

    /**
     * Method to find and return a user based on a username and password
     * @param username
     * Email of the user being looked for
     * @param password
     * Password of the user being looked for
     * @param listener
     * Listener so that the download can be asynchronous
     */
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

    /**
     * Checks if a user exists in the database based on a username or email
     * @param val
     * Email or username of a possible user
     * @param listener
     * Listener so that the boolean can be returned asynchronously
     */
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

    /**
     * Method to find all events where a given user has a specific status
     * @param user
     * User that we are filtering based on
     * @param status
     * Status that the user must have within an event fro the event to be returned
     * @param listener
     * Listener so that the download can be asynchronous
     */
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

    /**
     * This method is used to streamline all of the required changes of a user joining an event.
     * Updates both the event and the user
     * @param event
     * The event that the user is joining
     * @param user
     * The user that us joining the event
     */
    public void RegisterUserIntoEvent(Event event, User user){
        user.addEvent(event.getId());
        event.SetStatus(user.getId(), "Waiting");
        SaveUser(user);
        SaveEvent(event);

        CreateNotification(0, user.getId(), event.getId(), event.getOrg(), "You have joined the waiting list", "Notification");
    }

    /**
     * This method is used to streamline all of the required changes of a user cancelling joining an event.
     * Updates both the event and the user
     * @param event
     * The event that the user is cancelling for
     * @param user
     * The user that us cancelling from the event
     */
    public void CancelUserIntoEvent(Event event, User user){
        event.SetStatus(user.getId(), "Cancelled");
        SaveUser(user);
        SaveEvent(event);

        CreateNotification(0, user.getId(), event.getId(), event.getOrg(), "You have left the waiting list", "Notification");
    }

    /**
     * This method is used to close off an event, the event will be "finished". It creates notifications to notify users that the event is sign up is completed.
     * @param event
     * The event that is being finalized
     */
    public void FinishEvent(Event event){
        event.setFinished(true);
        Map<String, String> users = event.getEventUsers();
        for (String key : users.keySet()){
            if (Objects.equals(users.get(key), "Invited")){
                event.SetStatus(key, "Cancelled");
                CreateNotification(0, key, event.getId(), event.getOrg(), "The final list of entrants for this event has been decided, unfortunately you did not accept your invite in time. Better luck next time", "Event Entrant List Finalized");
            }
            else if (Objects.equals(users.get(key), "Waiting")){
                event.SetStatus(key, "Cancelled");
                CreateNotification(0, key, event.getId(), event.getOrg(), "The final list of entrants for this event has been decided, unfortunately you were not selected. Better luck next time", "Event Entrant List Finalized");
            }
            else if (Objects.equals(users.get(key), "Accepted")){
                CreateNotification(0, key, event.getId(), event.getOrg(), "The final list of entrants for this event has been decided, we look forward to seeing you at the event", "Event Entrant List Finalized");
            }
            else {
                event.SetStatus(key, "Cancelled");
                CreateNotification(0, key, event.getId(), event.getOrg(), "The final list of entrants for this event has been decided. Better luck next time", "Event Entrant List Finalized");
            }
        }
        SaveEvent(event);

    }

}