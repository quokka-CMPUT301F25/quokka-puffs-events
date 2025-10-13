package com.example.quokkapuffevents.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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


    public User createUser(String email, Integer type){
        String id = usersRef.document().getId(); //Creates a document and returns the id
        User newUser = new User(id, email, type);
        usersRef.document(id).set(newUser);
        return(newUser);
    }
}
