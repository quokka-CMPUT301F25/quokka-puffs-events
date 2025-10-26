package com.example.quokkapuffevents.model;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Database {
    private static volatile Database instance;
    private Integer userID; //current id of the user of the app
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private CollectionReference notifsRef;
    private CollectionReference imagesRef;

    private Database() {
        this.db = FirebaseFirestore.getInstance(); //Get database
        this.usersRef = db.collection("users");
        this.eventsRef = db.collection("events");
        this.notifsRef = db.collection("notifications");
        this.imagesRef = db.collection("images");
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

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getUserID() {
        return userID;
    }

    //Base creation methods
    public User createUser(String email, Integer type, String hashPass, String userName){
        /**
         * Creates a new user and ensures that the user has a proper ID and is in the firebase database
         * @param email
         * This is the city to check
         * @param type
         * This denotes the type of account the user is. -1 for admin, 0 for participent, and 1 for organizer
         * @param hashPass
         * This is the hashed password of the user
         * @param userName
         * This is the username that the user has chosen
         * @return
         * Returns the user as a new Class. Ensures that the user is saved to the cloud
         */
        String id = usersRef.document().getId(); //Creates a document and returns the id
        User newUser = new User(id, email, type, hashPass, userName); //Creates new User class
        usersRef.document(id).set(newUser); //Overwrites id in database with new user data
        return(newUser);
    }
    public Event createEvent(User org, String description, Integer toBeDrawn, Integer maxNumWaitlist, Date startDate, Date drawnDate, Date endDate){
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
        Event newEvent = new Event(id, org.getId(), description, toBeDrawn, maxNumWaitlist, startDate, drawnDate, endDate); //This version has the max on the size of the waitlsit
        eventsRef.document(id).set(newEvent);
        return(newEvent);
    }
    public Event createEvent(User org, String description, Integer toBeDrawn, Date startDate, Date drawnDate, Date endDate){
        /**
         * Same as the other create event but does not construct it with the optional cap on number of participents
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
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(id, org.getId(), description, toBeDrawn, startDate, drawnDate, endDate); //This version does not have the limit
        eventsRef.document(id).set(newEvent);
        return(newEvent);
    }
    public Notif createNotification(Integer type, User recipient, Event originEvent, User originUser, String message){
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
        Notif newNotif = new Notif(id, type, recipient.getId(), originEvent.getId(), originUser.getId(), message);
        notifsRef.document(id).set(newNotif);
        return(newNotif);
    }

    //TODO: READ
    //To edit safetly follow this
    //More wide ranging changes. For quick fixes user:
    //event = db.getEvent(event.getID())
    //event.setXYZ(xyz)
    //db.saveEvent(event)

    //TODO Make these prettier. They stink right now
    public User getUser(String userID){
        /**
         * This method collects the most up to date data from the database of a user based on their id
         * @param userID
         * The id of the user being searched for
         * @return
         * Returns the user in a User class. The return will have the most up to date data for the user id
         */
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
        ArrayList<User> user = new ArrayList<>(); //because I cannot create or access within the mini function
        user.add(new User());

        usersRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user.set(0, document.toObject(User.class));

                    } else {
                        Log.d(TAG, "No such document");
                        System.out.println("No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    System.out.println("Document Retrieval Failed");
                }
            }
        });

        return(user.get(0));
    }
    public Event getEvent(String eventID){
        /**
         * This method collects the most up to date data from the database of an event based on their id
         * @param eventID
         * The id of the event being searched for
         * @return
         * Returns the event in an Event class. The return will have the most up to date data for the event id
         */
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
        ArrayList<Event> event = new ArrayList<>(); //because I cannot create or access within the mini function
        event.add(new Event());

        eventsRef.document(eventID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        event.set(0, document.toObject(Event.class));

                    } else {
                        Log.d(TAG, "No such document");
                        System.out.println("No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    System.out.println("Document Retrieval Failed");
                }
            }
        });
        return(event.get(0));
    }
    public Notif getNotification(String notifID){
        /**
         * This method collects the most up to date data from the database of notif based on their id
         * @param notifID
         * The id of the notif being searched for
         * @return
         * Returns the notification in a Notif class. The return will have the most up to date data for the notification id
         */
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
        ArrayList<Notif> notif = new ArrayList<>(); //because I cannot create or access within the mini function
        notif.add(new Notif());

        notifsRef.document(notifID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        notif.set(0, document.toObject(Notif.class));

                    } else {
                        Log.d(TAG, "No such document");
                        System.out.println("No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    System.out.println("Document Retrieval Failed");
                }
            }
        });
        return(notif.get(0));
    }
    //TODO FIX THE IMAHES BELOW
    /*
    public Event uploadImage(Event event, BufferedImage image){
        /**
         * Creates a new image and adds it to the firebase and event
         * @param event
         * This is the city to check
         * @param image
         * This denotes the type of account the user is. -1 for admin, 0 for participent, and 1 for organizer
         * @return
         * The updated event

    String id = imagesRef.document().getId(); //Creates a document and returns the id
        imagesRef.document(id).set(image);

        return(event);
}

    public BufferedImage getImage(Event event){
        /**
         * Collects and returns an events image
         * @param event
         * The event that is being looked at
         * @return
         * A buffered image holding the png

        BufferedImage image;
        imagesRef.document(event.getImageID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        image = document;

                    } else {
                        Log.d(TAG, "No such document");
                        System.out.println("No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    System.out.println("Document Retrieval Failed");
                }
            }
        });

        return(user.get(0));
    }
    */

    public void saveUser(User user){
        usersRef.document(user.getId()).set(user);
    }
    public void saveEvent(Event event){
        eventsRef.document(event.getId()).set(event);
    }
    public void saveNotif(Notif notif){
        notifsRef.document(notif.getId()).set(notif);
    }

    //Two version to allow us to use both the object or just their id
    public void deleteUser(User user){
        /**
         * Deletes the provided user from the firebase database
         * @param user
         * This is the user that is being deleted
         */
        usersRef.document(user.getId()).delete();
    }
    public void deleteUser(String id){
         /**
         * Deletes the provided user from the firebase database
         * @param id
         * This is the id of the user that is being deleted
         */
        usersRef.document(id).delete();
    }
    public void deleteEvent(Event event){
        /**
         * Deletes the provided event from the firebase database
         * @param event
         * This is the event that is being deleted
         */
        eventsRef.document(event.getId()).delete();
    }
    public void deleteEvent(String id){
        /**
         * Deletes the provided event from the firebase database
         * @param id
         * This is the id of the event that is being deleted
         */
        eventsRef.document(id).delete();
    }
    public void deleteNotification(Notif notif){
        /**
         * Deletes the provided notif from the firebase database
         * @param notif
         * This is the notif that is being deleted
         */
        notifsRef.document(notif.getId()).delete();
    }
    public void deleteNotification(String id){
        /**
         * Deletes the provided notif from the firebase database
         * @param id
         * This is the id of the notif that is being deleted
         */
        notifsRef.document(id).delete();
    }

    //Extrapolated Date Methods
    //TODO: Test all of these:
    public ArrayList<Event> listEvents(){
        /**
         * This method provides a list of every event that is in the database
         * @return
         * Returns an ArrayList that holds all of the known events
         */
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
       ArrayList<Event> events = new ArrayList<>();
       //Will return every single event
       eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                events.add(document.toObject(Event.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
       return events;
    }

    public ArrayList<User> usersInEvent(Event event){
        /**
         * This method provides a list of every user that is signed up to an event
         * @param event
         * This is the event that is being looked at. The users returned will have signed up to this event
         * @return
         * Returns an ArrayList that holds all of the users that have signed up for this event
         */
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
        ArrayList<User> users = new ArrayList<>(); //because I cannot create or access within the mini function
        ArrayList<Event> specificEvent = new ArrayList<>(); //because I cannot create or access within the mini function

        //Collect the most up to date version of the event
        eventsRef.document(event.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                specificEvent.set(0, documentSnapshot.toObject(Event.class));
            }
        });

        //List of all users in the event
        List usersInEvent = (List) specificEvent.get(0).getEventUsers().keySet();

        //Collects the data for every user with an id in the above list
        usersRef.whereIn("id", usersInEvent)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(document.toObject(User.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return(users);
    }

    public ArrayList<Event> getEventsFromUser(User user){
        /**
         * This method provides a list of every event that a user has signed up for
         * @param user
         * This is the user that is being looked at
         * @return
         * Returns an ArrayList that holds all of the events that the user has signed up for
         */
        ArrayList<User> specificUser = new ArrayList<>(); //because I cannot create or access within the mini function
        ArrayList<Event> events = new ArrayList<>(); //because I cannot create or access within the mini function

        //Collect the most up to date version of the event
        usersRef.document(user.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                specificUser.set(0, documentSnapshot.toObject(User.class));
            }
        });

        //List of all users in the event
        List eventsOfUser = specificUser.get(0).getEvents();

        //Collects the data for every user with an id in the above list
        eventsRef.whereIn("id", eventsOfUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                events.add(document.toObject(Event.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return(events);
    }

    //TODO Add logic for sending out notifications. Need Testing
    public ArrayList<User> drawUsers(Event event){
        /**
         * This method draws the correct number of people for an event. It is the random raffle mechanism
         * @param event
         * The event that is randomly selecting participents from its waiting list
         * @return
         * Returns an Array List containing all of the chosen users
         */
        //Update from database
        event = getEvent(event.getId());
        //Collect User IDs
        ArrayList<String> userID = event.drawUsers(-1);

        //Convert from id to User class
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < userID.size(); i ++){
            users.add(getUser(userID.get(i)));
        }
        return(users);
    }

    public ArrayList<User> redrawUsers(Event event, Integer numToDraw){
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
        //Update from database
        event = getEvent(event.getId());
        //Collect User IDs
        ArrayList<String> userID = event.drawUsers(numToDraw);
        //Convert from id to User class
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < userID.size(); i ++){
            users.add(getUser(userID.get(i)));
        }
        return(users);
    }

    //Notif Methods
    public ArrayList<Notif> getUserNotifications(User user){
        /**
         * This method collects and returns all of the notifications that have been sent to a user
         * @param user
         * The user that the notifications have been sent to
         * @return
         * Returns an Array List containing all of the notifications
         */
        ArrayList<Notif> notifs = new ArrayList<>();
        notifsRef.whereEqualTo("recipient", user.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                notifs.add(document.toObject(Notif.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return(notifs);
    }

}
