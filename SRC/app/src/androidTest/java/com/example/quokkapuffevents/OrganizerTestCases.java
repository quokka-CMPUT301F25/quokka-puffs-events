package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.text.SimpleDateFormat;

/**
 * For testing any Organizer related User Stories
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerTestCases {
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

    public User createTestOrganizer() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPasswordByte = md.digest("password".getBytes(StandardCharsets.UTF_8));
        String hashedPassword = new String(hashedPasswordByte);
        User user = db.CreateUser("testorganizer@example.com", 1, hashedPassword,
                "TestOrganizer", "FirstOrganizer", "LastOrganizer",
                "5870011122");
        return user;
    }

    public void deleteMockUser(User user) {
        db.DeleteUser(user);
    }

    @Test public void SampleSpecificAttendees(){
        try {
            Database db = Database.getInstance();

            User mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
            Thread.sleep(1500);

            db.SetUserID(mockOrg.getId());

            Event event = db.CreateEvent("TestDrawSpecificAttendees", mockOrg.getId(),
                    "Testing specified number of attendees sampling", 5, 1, new Date(), new Date());
            Thread.sleep(1500);

            List<User> attendees = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                User attendee = db.CreateUser("attendee" + i + "@test.com", 0, "password",
                        "Attendee" + i, "First" + i, "Last" + i, "555000" + i);
                attendees.add(attendee);
                Thread.sleep(500);

                event.addUser(attendee.getId());
            }

            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check event state before drawing
            System.out.println("Event toBeDrawn: " + event.getToBeDrawn());
            System.out.println("Event users count: " + event.getEventUsers().size());
            System.out.println("Event users: " + event.getEventUsers().keySet());

            ArrayList<String> sampledAttendeeIds = event.drawUsers(-1);

            // DEBUG: Check what was returned
            System.out.println("Sampled attendees count: " + (sampledAttendeeIds != null ? sampledAttendeeIds.size() : "null"));
            System.out.println("Sampled attendees: " + sampledAttendeeIds);

            assertNotNull("Sampled attendees list should not be null", sampledAttendeeIds);
            assertEquals("Should return exactly the specified number of attendees",
                    5, sampledAttendeeIds.size());

            for (String sampledAttendeeId : sampledAttendeeIds) {
                boolean isRegistered = event.getEventUsers().containsKey(sampledAttendeeId);
                assertTrue("Sampled attendee should be registered for the event", isRegistered);
            }

            Set<String> uniqueSampled = new HashSet<>(sampledAttendeeIds);
            assertEquals("Should have no duplicate attendees in sample",
                    sampledAttendeeIds.size(), uniqueSampled.size());

            db.DeleteEvent(event);
            for (User attendee : attendees) {
                db.DeleteUser(attendee);
            }
            db.DeleteUser(mockOrg);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void OptLimitParticipants() {
        User testOrganizer = createTestOrganizer();
        try(ActivityScenario<LoginActivity> scenario =
                    ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestOrganizer"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.headerBtnContainer)).check(matches(isDisplayed()));

            onView(withId(R.id.add_events_button)).perform(click());

            onView(withId(R.id.eventLimitParticipantsSwitch)).perform(click());
            onView(withId(R.id.eventMaxParticipantsInput)).perform(typeText("150"));
            onView(withId(R.id.eventTitleInput)).perform(typeText("Example Event"));
            onView(withId(R.id.eventDateInput)).perform(typeText("2025-12-03"));
            onView(withId(R.id.drawDateInput)).perform(typeText("2025-12-08"));
            onView(withId(R.id.eventParticipantAmountInput)).perform(typeText("30"));
            onView(withId(R.id.eventDescInput)).perform(typeText("HELP ME"));

            Thread.sleep(2000);
            onView(withId(R.id.confirmEventCreationBtn)).perform(click());

            Thread.sleep(5000);
            onView(withId(R.id.home_button)).perform(click());
            Thread.sleep(5000);

            db.GetEventsFromUser(testOrganizer, events -> {

                for(Event e: events) {
                    if(e.getName().equals("Example Event")) {
                        assert(e.getMaxNumWaitlist() == 150);
                        Date eventDrawDateObj = e.getDrawnDate();

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String eventDrawnDate = formatter.format(eventDrawDateObj);
                        assert(eventDrawnDate.equals("2025/12/08"));
                    } else {
                        throw new Error("Not workings");
                    }
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        deleteMockUser(testOrganizer);
    }

    @Test
    public void ImageEventPoster(){
        // TODO: Create test when storage is all good
    }
}