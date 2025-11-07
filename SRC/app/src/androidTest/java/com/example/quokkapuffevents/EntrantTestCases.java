package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quokkapuffevents.controller.LoginActivity;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EntrantTestCases {
    Database db = Database.getInstance();

    /**
     * Creates an entrant account for testing user stories.
     *
     * @return A mock entrant user account
     */
    public User createMockEntrant() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPasswordByte = md.digest("password".getBytes(StandardCharsets.UTF_8));
        String hashedPassword = new String(hashedPasswordByte);
        User user = db.CreateUser("TestingEntrant@Entrant.ca", 0, hashedPassword,
                "TestingEntrant", "FirstEntrant", "LastEntrant",
                "5871234567");
        return user;
    }

    /**
     * For automating accessing the entrant user main dashboard
     */
    public User accessEntrantDashboard() {
        User mockEntrant = createMockEntrant();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText(mockEntrant.getEmail()));
            closeSoftKeyboard();
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        
        return mockEntrant;
    }

    public Event createMockEvent(Date eventDate) {
        return db.CreateEvent("Mock Event", "Mock Organizer", "Mock Description", 10, new Date(), eventDate);
    }

    @Test
    public void TestJoinWaitingList() {
        Database db = Database.getInstance();

        // Create Organizer 
        User organizer = db.CreateUser("Organizer@Test.ca", 1, "pass",
                "OrgUser", "Org", "User", "1111111111");
        db.SetUserID(organizer.getId());

        // Create Event 
        Event testEvent = createMockEvent(new Date());

        //  Create Entrant 
        User entrant = accessEntrantDashboard();
        db.SetUserID(entrant.getId());

        try {
            Thread.sleep(2000);

            //  Open Event List 
            onView(withId(R.id.all_events_button))
                    .check(matches(isDisplayed()))
                    .perform(click());

            Thread.sleep(1500);

            // Click Register Button 
            onData(anything())
                    .inAdapterView(withId(R.id.findEventsListView))
                    .atPosition(0)
                    .onChildView(withId(R.id.event_register_btn_all))
                    .perform(click());

            Thread.sleep(2000);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify Entrant Was Added Correctly 
        final boolean[] verified = {false};

        db.GetEvent(testEvent.getId(), event -> {
            assertTrue("User is not added to waitlist.", event.getEventUsers().containsKey(entrant.getId()));
            assertEquals("Waiting", event.getEventUsers().get(entrant.getId()));

            db.GetUser(entrant.getId(), user -> {
                assertTrue("Event did not appear in entrant event list.", user.getEvents().contains(testEvent.getId()));
                verified[0] = true;
            });
        });

        try { Thread.sleep(2000); } catch (Exception ignored) {}

        assertTrue("Verification never completed.", verified[0]);

        // Cleanup 
        db.DeleteEvent(testEvent.getId());
        db.DeleteUser(entrant.getId());
        db.DeleteUser(organizer.getId());
    }
    
    @Test
    public void TestViewingEvents() {
        accessEntrantDashboard();
    }

    @Test
    public void TestRegistering() {

    }

    @Test
    public void TestViewingPastEvents() {
        accessEntrantDashboard();
    }

    @Test
    public void TestReceivingLostNotification() {
        accessEntrantDashboard();
    }
}
