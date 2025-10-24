package com.example.quokkapuffevents;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.Notif;
import com.example.quokkapuffevents.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class DatabaseTest {

    @Test
    void testCreateUserAndGetUser() {
        Database db = Database.getInstance();
        User user1 = db.createUser("email", 0, "gadwidg", "SethRocks");
        User user2 = db.createUser("email2", 1, "aiwd", "HunnyBun");


        User user3 = db.getUser(user1.getId());

        assertEquals(user1.getUserName(), user3.getUserName());
        assertEquals("SethRocks", user1.getUserName());
        assertNotNull(user2.getId());

    }

    @Test
    void testGetUserNotifications() {
        Database db = Database.getInstance();
        User user1 = db.createUser("email", 0, "gadwidg", "SethRocks");
        User user2 = db.createUser("email2", 1, "aiwd", "HunnyBun");

        Event event1 = db.createEvent(user1, "Test test tes", 10, Optional.of(100), Date.from(Instant.now()), Date.from(Instant.now()));
        Notif notif1 = db.createNotification(1, user1.getId(), event1.getId(), user2.getId(), "Garbilygoo Test TEst");

        ArrayList<Notif> test = db.getUserNotifications(user1);

        assertEquals("Garbilygoo Test TEst", test.get(0).getMessage());
        assertNotNull(test);

    }

}
