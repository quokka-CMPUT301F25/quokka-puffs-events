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

        eventsRef.document(event.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                specificEvent.set(0, documentSnapshot.toObject(Event.class));
            }
        });

        List usersInEvent = (List) specificEvent.get(0).getEventUsers().keySet();

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

    //public ArrayList<User> drawUsers

    //public ArrayList<User> redrawUsers

    //public void deleteUser

    //public void deleteEvent

    //public ArrayList<Event> getEventsFromUser

    public User getUser(String userID){
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
        ArrayList<User> user = new ArrayList<>(); //because I cannot create or access within the mini function

        usersRef.document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user.set(0, documentSnapshot.toObject(User.class));
            }
        });
        return(user.get(0));
    }

    public Event getEvent(String eventID){
        //Got the basis of this code from the firebase website: https://firebase.google.com/docs/firestore/query-data/get-data
        ArrayList<Event> event = new ArrayList<>(); //because I cannot create or access within the mini function

        eventsRef.document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                event.set(0, documentSnapshot.toObject(Event.class));
            }
        });
        return(event.get(0));
    }
}
