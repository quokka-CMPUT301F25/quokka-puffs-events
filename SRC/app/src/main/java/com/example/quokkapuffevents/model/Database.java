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


    public User createUser(String email, Integer type, String hashPass, String userName){
        String id = usersRef.document().getId(); //Creates a document and returns the id
        User newUser = new User(id, email, type, hashPass, userName);
        usersRef.document(id).set(newUser);
        return(newUser);
    }

    public Event createEvent(User org, String description, Integer toBeDrawn, Optional<Integer> maxNumWaitlist, Date startDate, Date endDate){
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(id, org.getId(), description, toBeDrawn, maxNumWaitlist, startDate, endDate);
        eventsRef.document(id).set(newEvent);
        return(newEvent);
    }

    public Notif createNotification(Integer type, User recipient, Event originEvent, User originUser, String message){
        String id = notifsRef.document().getId(); //Creates a document and returns the id
        Notif newNotif = new Notif(id, type, recipient.getId(), originEvent.getId(), originUser.getId(), message);
        notifsRef.document(id).set(newNotif);
        return(newNotif);
    }

    public void deleteUser(User user){
        usersRef.document(user.getId()).delete();
    }
    public void deleteUser(String id){
        usersRef.document(id).delete();
    }
    public void deleteEvent(Event event){
        eventsRef.document(event.getId()).delete();
    }
    public void deleteEvent(String id){
        eventsRef.document(id).delete();
    }
    public void deleteNotification(Notif notif){
        notifsRef.document(notif.getId()).delete();
    }
    public void deleteNotification(String id){
        notifsRef.document(id).delete();
    }

    public ArrayList<Event> listEvents(){
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

    public User getUser(String userID){
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

    //TODO Add logic for sending out notifications
    public ArrayList<User> drawUsers(Event event){
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
