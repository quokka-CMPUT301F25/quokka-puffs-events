package com.example.quokkapuffevents.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Optional;

public class Database {
    private static volatile Database instance;
    private Integer userID; //current id of the user of the app
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private CollectionReference notifsRef;

    private Database() {
        this.db = FirebaseFirestore.getInstance(); //Get database
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

    public void setUserID(Integer userID) {
        this.userID = userID;
    }


    public User createUser(String email, Integer type, String hashPass, String userName){
        String id = usersRef.document().getId(); //Creates a document and returns the id
        User newUser = new User(id, email, type, hashPass, userName);
        usersRef.document(id).set(newUser);
        return(newUser);
    }

    public Event createEvent(String idEvent, User org, String description, Integer toBeDrawn, Optional<Integer> maxNumWaitlist, Date startDate, Date endDate){
        String id = eventsRef.document().getId(); //Creates a document and returns the id
        Event newEvent = new Event(idEvent, org.getId(), description, toBeDrawn, maxNumWaitlist, startDate, endDate);
        eventsRef.document(id).set(newEvent);
        return(newEvent);
    }

    
}
