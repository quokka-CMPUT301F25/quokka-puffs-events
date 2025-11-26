package com.example.quokkapuffevents;

import com.example.quokkapuffevents.model.Database;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class TestingDatabase extends Database{
    private static volatile TestingDatabase instance;
    public TestingDatabase() {
        this.db = FirebaseFirestore.getInstance(); //Get database
        this.imageDB = FirebaseStorage.getInstance().getReference("Test/Uploads"); //Get Storage

        this.usersRef = db.collection("Test/users");
        this.eventsRef = db.collection("Test/events");
        this.notifsRef = db.collection("Test/notifications");

    }

    /**
     * Creates a new static instance of the Database connection.
     * @return
     * The database instance.
     */
    public static TestingDatabase getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new TestingDatabase();
                }
            }
        }
        return instance;
    }
}