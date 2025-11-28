package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.google.firebase.database.core.RepoManager.clear;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import android.widget.AdapterView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.rule.ActivityTestRule;

import com.example.quokkapuffevents.controller.LoginActivity;
import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.Notif;
import com.example.quokkapuffevents.model.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * For testing any Entrant related User Stories
 */
@RunWith(AndroidJUnit4.class)
public class EntrantTestCases {
    Database db = Database.getInstance();

    public void ClearDatabase(){
        db.ListNotifs(notifs -> {
            for (Notif notif : notifs){
                db.DeleteNotification(notif);
            }
        });
        db.ListUsers(users -> {
            for (User user : users){
                db.DeleteUser(user);
            }
        });
        db.ListEvents(events -> {
            for (Event event : events){
                db.DeleteEvent(event);
            }
        });
    }

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
     * For automating accessing the entrant user main dashboard.
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

    /**
     * Creates an event for testing user stories.
     *
     * @param eventDate
     * A hardcoded date for setting an event into the past, present, or future
     *
     * @return
     * A mock event for entrants to register for
     */
    public Event createMockEvent(Date eventDate) {
        return db.CreateEvent("Mock Event", "Mock Organizer", "Mock Description", 10, new Date(), eventDate, false);
    }

    public static void assertDoesNotExist(ViewInteraction viewInteraction) {
        try {
            viewInteraction.check((view, noViewFoundException) -> {
                if (view != null) {
                    throw new AssertionError("View still exists in hierarchy: " + view);
                }
            });
        } catch (NoMatchingViewException e) {
        }
    }

    /**
     * User Story US 01.01.01 test case
     */
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

    /**
     * User Story US 01.01.03 test case
     */
    @Test
    public void TestViewingEvents() {
        // Create and Login Entrant
        User entrant = accessEntrantDashboard();
        db.SetUserID(entrant.getId());

        // Create Event
        Event testEvent = createMockEvent(new Date());

        try {
            Thread.sleep(3000);

            // Open Events List
            onView(withId(R.id.all_events_button)).check(matches(isDisplayed())).perform(click());

            Thread.sleep(1500);

            // Test if event appears in the list
            onView(withText(testEvent.getName())).perform(scrollTo()).check(matches(isDisplayed()));

            Thread.sleep(1500);

            //Clean Up
            db.DeleteUser(entrant);
            db.DeleteEvent(testEvent);

        } catch (InterruptedException e) {
            ClearDatabase();
            throw new RuntimeException(e);
        }
    }

    /**
     * User Story US 01.02.01 test case
     */
    @Test
    public void TestRegistering() {
        // Switches the activity to the RegisterActivity
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.register_page_button)).perform(click());

            Thread.sleep(1500);

            // Inputting information into text boxes with phone number
            onView(withId(R.id.register_email)).perform(typeText("RegisterTest@Email.com"));
            onView(withId(R.id.register_username)).perform(typeText("RegisterEntrant"));
            onView(withId(R.id.register_password)).perform(typeText("password"));
            onView(withId(R.id.register_first_name)).perform(typeText("Register"));
            onView(withId(R.id.register_last_name)).perform(typeText("Entrant"));
            onView(withId(R.id.register_phone_number)).perform(typeText("1234567890"));
            onView(withId(R.id.register_entrant_button)).perform(click());

            // Signing into newly created account
            onView(withId(R.id.register_info_button)).perform(click());

            Thread.sleep(1500);

            // Testing if registration completed successfully using UI elements
            onView(withId(R.id.settings_button)).check(matches(isDisplayed()));

            // Testing if userId exists
            String userId = db.GetCurrentUserID();
            assertNotNull("User Id should not be null", userId);

            // Switching to Settings Fragment
            onView(withId(R.id.settings_button)).perform(click());

            Thread.sleep(1500);

            // Signing out of the current entrant account
            onView(withId(R.id.signOutBtn)).perform(click());

            db.DeleteUser(userId); // Deletes mock entrant from database

            onView(withId(R.id.register_page_button)).perform(click());

            Thread.sleep(1500);

            // Inputting information into text boxes without phone number
            onView(withId(R.id.register_email)).perform(typeText("RegisterTest@Email.com"));
            onView(withId(R.id.register_username)).perform(typeText("RegisterEntrant"));
            onView(withId(R.id.register_password)).perform(typeText("password"));
            onView(withId(R.id.register_first_name)).perform(typeText("Register"));
            onView(withId(R.id.register_last_name)).perform(typeText("Entrant"));
            onView(withId(R.id.register_entrant_button)).perform(click());

            // Signing into newly created account
            onView(withId(R.id.register_info_button)).perform(click());

            Thread.sleep(1500);

            // Testing if registration completed successfully using UI elements
            onView(withId(R.id.settings_button)).check(matches(isDisplayed()));

            // Testing if userId exists
            String newUserId = db.GetCurrentUserID();
            assertNotNull("User Id should not be null", newUserId);

            // Switching to Settings Fragment
            onView(withId(R.id.settings_button)).perform(click());

            Thread.sleep(1500);

            // Signing out of the current entrant account
            onView(withId(R.id.signOutBtn)).perform(click());

            // Clean Up
            db.DeleteUser(userId);

        } catch (InterruptedException e) {
            ClearDatabase();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestViewingPastEvents() {
        accessEntrantDashboard();
    }

    @Test
    public void TestReceivingLostNotification() {
        accessEntrantDashboard();
    }

    /**
     * User Story US 01.02.02 test case
     */
    @Test
    public void UpdateEntrantInfo() {
        User entrant = accessEntrantDashboard();
        db.SetUserID(entrant.getId());

        try {
            onView(withId(R.id.settings_button)).perform(click());
            Thread.sleep(1500);
            onView(withId(R.id.editProfileBtn)).perform(click());
            Thread.sleep(2000);

            onView(withId(R.id.userFirstNameTextInput)).perform(clearText());
            onView(withId(R.id.userFirstNameTextInput)).perform(typeText("Changed"));

            onView(withId(R.id.userLastNameTextInput)).perform(clearText());
            onView(withId(R.id.userLastNameTextInput)).perform(typeText("Changed"));

            onView(withId(R.id.userEmailTextInput)).perform(clearText());
            onView(withId(R.id.userEmailTextInput)).perform(typeText("Changed"));

            onView(withId(R.id.userContactInformationInput)).perform(clearText());
            onView(withId(R.id.userContactInformationInput)).perform(typeText("Changed"));
            closeSoftKeyboard();

            onView(withId(R.id.confirmChangesBtn)).perform(click());
            Thread.sleep(800);

            onView(withId(R.id.userFirstAndLastNameText)).check(matches(
                    withText("Changed Changed")));
            onView(withId(R.id.userEmailText)).check(matches(withText("Changed")));
            onView(withId(R.id.userPhoneNumber)).check(matches(withText("Changed")));
        } catch (InterruptedException e) {
            ClearDatabase();
            throw new RuntimeException(e);
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.02.04 test case
     */
    @Test
    public void TestDeleteProfile() {
        accessEntrantDashboard();
        String dummyID = db.GetCurrentUserID();
        try {
            onView(withId(R.id.settings_button)).perform(click());
            Thread.sleep(1500);
            onView(withId(R.id.editProfileBtn)).perform(click());
            Thread.sleep(1500);
            onView(withId(R.id.deleteAccountBtn)).perform(click());
            Thread.sleep(1500);

            // Testing if returned back to
            onView(withId(R.id.sign_in_button)).check(matches(isDisplayed()));
            assertNull(db.GetCurrentUserID());
            db.ListUsers(users -> {
                for (User user : users){
                    assertNotEquals(dummyID, user.getId());
                }
            });
            Thread.sleep(4000);

        } catch (InterruptedException e) {
            ClearDatabase();
            throw new RuntimeException(e);
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.07.01 test case
     */
    @Test public void TestRememberMe() {
        User mockEntrant = createMockEntrant();
        try (ActivityScenario<LoadingActivity> scenario = ActivityScenario.launch(LoadingActivity.class)) {
            Thread.sleep(1500);
            //Filling in info
            onView(withId(R.id.login_email_address)).perform(typeText(mockEntrant.getEmail()));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();

            //Check Remember me
            onView(withId(R.id.remember_me_button)).perform(click());
            onView(withId(R.id.sign_in_button)).perform(click());
            Thread.sleep(1500);

            //Close and reopen
            ActivityScenario.launch(LoadingActivity.class);
            Thread.sleep(1500);
            onView(withId(R.id.login_email_address)).check(matches(withText(mockEntrant.getEmail())));
            onView(withId(R.id.login_password)).check(matches(withText("password")));
            onView(withId(R.id.remember_me_button)).check(matches(isDisplayed()));
            Thread.sleep(1500);

            onView(withId(R.id.remember_me_button)).perform(click());
            onView(withId(R.id.sign_in_button)).perform(click());
            Thread.sleep(1500);

            //Close and reopen
            ActivityScenario.launch(LoadingActivity.class);
            Thread.sleep(1500);
            onView(withId(R.id.login_email_address)).check(matches(withText("")));
            onView(withId(R.id.login_password)).check(matches(withText("")));
            onView(withId(R.id.remember_me_button)).check(matches(isDisplayed()));
            Thread.sleep(1500);

        } catch (InterruptedException e) {
            ClearDatabase();
            throw new RuntimeException(e);
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.04.01 test case
     */
    @Test
    public void TestChosenInDrawNotif() {
        User mockOrg = null;
        Event event = null;
        try {
            User mockEntrant = accessEntrantDashboard();

            mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
            Thread.sleep(1500);
            event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 1, 1, new Date(), new Date(), false);
            Thread.sleep(1500);


            event.addUser(mockEntrant.getId());

            //Check no notification exists
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);

            assertDoesNotExist(onView(withText(R.string.winner_header)));
            onView(withId(R.id.home_button)).perform(click());
            Thread.sleep(1500);

            //Draw User
            event.drawUsers(-1);
            Thread.sleep(1500);

            //Go back to notif
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);

            //Testing to see if notification has appeared
            onView(withText(R.string.winner_header)).check(matches(isDisplayed()));


        } catch (InterruptedException e) {
            ClearDatabase();
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.04.02 test case
     */
    @Test public void TestNotChosenInDrawNotif() {
        User mockOrg = null;
        Event event = null;
        try {
            User mockEntrant = accessEntrantDashboard();
            mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
            Thread.sleep(1500);
            event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 1, 1, new Date(), new Date(), false);
            Thread.sleep(1500);
            event.addUser(mockEntrant.getId());
            //Check no notification exists
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);
            assertDoesNotExist(onView(withText(R.string.not_picked)));
            onView(withId(R.id.home_button)).perform(click());
            Thread.sleep(1500);
            //Draw User
            event.drawUsers(0);
            Thread.sleep(1500);
            //Go back to notif
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);
            //Testing to see if notification has appeared
            onView(withText(R.string.not_picked)).check(matches(isDisplayed()));


        } catch (InterruptedException e) {
            ClearDatabase();
            throw new RuntimeException(e);
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.05.02 test case
     */
    @Test
    public void TestAcceptInviteToEvent() {
        User mockOrg = null;
        Event event = null;
        try {
            User mockEntrant = accessEntrantDashboard();

            mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
            Thread.sleep(1500);
            event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 1, 1, new Date(), new Date(), false);
            Thread.sleep(1500);

            event.addUser(mockEntrant.getId());
            //Draw User
            event.drawUsers(-1);
            Thread.sleep(1500);


            //Go notif
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);

            //Testing to see if notification has appeared
            onView(withText(R.string.winner_header)).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.acceptBtn), hasSibling(withText("X")))).perform(click());

            //Get updated event info
            db.GetEvent(event.getId(), updatedEvent -> {
                assertEquals("Accepted", updatedEvent.getEventUsers().get(mockEntrant.getId()));
            });
            Thread.sleep(1500);


        } catch (InterruptedException e) {
            ClearDatabase();
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.05.03 test case
     */
    @Test
    public void TestDeclineInviteToEvent() {
        User mockOrg = null;
        Event event = null;
        try {
            User mockEntrant = accessEntrantDashboard();

            mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
            Thread.sleep(1500);
            event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 1, 1, new Date(), new Date(), false);
            Thread.sleep(1500);

            event.addUser(mockEntrant.getId());
            //Draw User
            event.drawUsers(-1);
            Thread.sleep(1500);


            //Go notif
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);

            //Testing to see if notification has appeared
            onView(withText(R.string.winner_header)).check(matches(isDisplayed()));
            onView(allOf(withId(R.id.rejectBtn), hasSibling(withText("X")))).perform(click());

            //Get updated Event info
            db.GetEvent(event.getId(), updatedEvent -> {
                assertEquals("Rejected", updatedEvent.getEventUsers().get(mockEntrant.getId()));
            });
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            ClearDatabase();
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 02.07.02 test case
     */
    @Test
    public void TestSendNotifToAllSelected() {
        User mockOrg = null;
        Event event = null;
        User temp = accessEntrantDashboard();
        try {
            User mockEntrant1 = db.CreateUser("MockTest", 0, temp.getHashPassword(), "mockTest1", "John", "Test", "0");
            User mockEntrant2 = db.CreateUser("MockTest", 0, temp.getHashPassword(), "mockTest2", "John", "Test", "0");
            mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
            Thread.sleep(1500);
            event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 2, 2, new Date(), new Date(), false);
            Thread.sleep(1500);

            event.addUser(mockEntrant1.getId());
            event.addUser(mockEntrant2.getId());

            event.drawUsers(-1);
            Thread.sleep(1500);

            ActivityScenario.launch(LoginActivity.class);
            onView(withId(R.id.login_email_address)).perform(typeText(mockEntrant1.getUserName()));
            closeSoftKeyboard();
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());
            Thread.sleep(1500);
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);

            //Testing to see if notification has appeared
            onView(withText(R.string.winner_header)).check(matches(isDisplayed()));

            ActivityScenario.launch(LoginActivity.class);
            onView(withId(R.id.login_email_address)).perform(typeText(mockEntrant2.getUserName()));
            closeSoftKeyboard();
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());
            Thread.sleep(1500);
            onView(withId(R.id.notifs_button)).perform(click());
            Thread.sleep(1500);

            //Testing to see if notification has appeared
            onView(withText(R.string.winner_header)).check(matches(isDisplayed()));


        } catch (InterruptedException e) {
            ClearDatabase();
        } finally {
            ClearDatabase();
        }
    }

    /**
     * User Story US 01.04.03 test case
     */
    @Test
    public void NotificationsOptOut() {
        // Create and login entrant
        User entrant = accessEntrantDashboard();
        db.SetUserID(entrant.getId());

        try {
            Thread.sleep(1500);

            onView(withId(R.id.settings_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.editProfileBtn)).perform(click());

            // Check if user has notification turned on by default
            db.GetUser(entrant.getId(), user -> {
                assertEquals(true, user.getSendNotifications());
            });

            Thread.sleep(1500);

            onView(withId(R.id.enableNotificationsSwitchBtn)).perform(click());

            // Check if after turning off notifications from the menu it saves to the database
            db.GetUser(entrant.getId(), user -> {
                assertEquals(false, user.getSendNotifications());
            });

        } catch (InterruptedException e) {
            db.DeleteUser(entrant);
            throw new RuntimeException(e);
        } finally {
            db.DeleteUser(entrant);
        }
    }
}