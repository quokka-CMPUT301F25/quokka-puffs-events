package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.*;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.quokkapuffevents.controller.LoginActivity;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.quokkapuffevents.model.Database;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * For testing any Admin related User Stories
 */
@RunWith(AndroidJUnit4.class)
public class AdminTest {
    Database db = Database.getInstance();
    private final LatLng defaultLocation = new LatLng(-34, 151);

    /* For granting permissions of push notification, allows for tests to run properly
    without unexpected permission popups. */
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    public User createMockAdmin() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPasswordByte = md.digest("password".getBytes(StandardCharsets.UTF_8));
        String hashedPassword = new String(hashedPasswordByte);
        User user = db.CreateUser("TestingAdmin@Admin.ca", -1, hashedPassword,
                "TestingAdmin", "FirstAdmin", "LastAdmin",
                "5871234567");
        return user;

    }

    public User createTestUser() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPasswordByte = md.digest("password".getBytes(StandardCharsets.UTF_8));
        String hashedPassword = new String(hashedPasswordByte);
        User user = db.CreateUser("testuser@example.com", 0, hashedPassword,
                "TestUser", "FirstUser", "LastUser",
                "5870011111");
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

    public Event createMockEvent(Date eventDate) {
        return db.CreateEvent("Mock Event", "Mock Organizer", "Mock Description", 10, new Date(), eventDate, defaultLocation, -1);
    }

    public void deleteMockEvent(Event event) {
        db.DeleteEvent(event.getId());
    }

    public void deleteMockAdmin(User user) {
        db.DeleteUser(user);
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

    @Test
    public void accessAdminDashboard() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
    }

    @Test
    public void accessAdminEvents() {
        User mockAdmin = createMockAdmin();

        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));

            onView(withId(R.id.eventsIcon)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(withText("All Events")));

            Thread.sleep(1500);

            onView(withId(R.id.adminEventsListView))
                    .check(matches(not(hasDescendant(withText("AdminDeleteTest")))));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
    }

    @Test
    public void accessAdminUsers() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));

            onView(withId(R.id.usersIcon)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(withText("All Users")));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
    }

    @Test
    public void accessAdminNotifs() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));

            onView(withId(R.id.notificationsIcon)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(withText("All Notifications")));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
    }

    //    US 03.03.01 As an administrator, I want to be able to browse images. (Done - Kishan)
    @Test
    public void accessAdminImages() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));

            onView(withId(R.id.imagesIcon)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(withText("All Images")));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
    }

//    US 03.01.01 As an administrator, I want to be able to remove events. (In-Progress - KYLE)
    @Test
    public void deleteUser() {

        User mockAdmin = createMockAdmin();
        User testUser = createTestUser();




        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));


//            Go to the users dashboard
            onView(withId(R.id.usersIcon)).perform((click()));
            Thread.sleep(1500);


            onView(withText("testuser@example.com")).check(matches(isDisplayed()));


//            Click the delete button
            onView(allOf(withId(R.id.deleteButton),
                    hasSibling(withText("testuser@example.com"))))
                    .perform(click());

            Thread.sleep(1500);

            onView(withText("Delete User")).check(matches(isDisplayed()));

            onView(withId(android.R.id.button2)).perform(click());

            Thread.sleep(1500);

            assertDoesNotExist(onView(withText("testuser@example.com")));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
        deleteMockAdmin(testUser);

    }

//  US 03.02.01 As an administrator, I want to be able to remove profiles. (In-Progress - KYLE)

    @Test
    public void deleteEvent() throws InterruptedException {

        User mockAdmin = createMockAdmin();

        db.ListEvents(events -> {
            if(!events.isEmpty())
            {
                for(Event e: events)
                    db.DeleteEvent(e);
            }
        });

        Event mockEvent = createMockEvent(new Date());
        Thread.sleep(1500);

        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));


//            Go to the events dashboard
            onView(withId(R.id.eventsIcon)).perform((click()));
            Thread.sleep(1500);


            onView(withText("Mock Event")).check(matches(isDisplayed()));


//            Click the delete button
            onView(allOf(withId(R.id.deleteButton),
                    hasSibling(withText("Mock Event"))))
                    .perform(click());

            Thread.sleep(1500);

            onView(withText("Delete Event")).check(matches(isDisplayed()));

            onView(withId(android.R.id.button2)).perform(click());

            Thread.sleep(1500);

            assertDoesNotExist(onView(withText("Mock Event")));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
        deleteMockEvent(mockEvent);

    }

    @Test
    public void deleteOrganizer() {

        User mockAdmin = createMockAdmin();
        User testOrganizer = createTestOrganizer();


        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));


//            Go to the users dashboard
            onView(withId(R.id.usersIcon)).perform((click()));
            Thread.sleep(1500);

            onView(withId(R.id.adminUsersListView)).check(matches(isDisplayed()));

//            Click the delete button
            onView(allOf(withId(R.id.deleteButton),
                    hasSibling(withText("testuser@example.com"))))
                    .perform(click());

            Thread.sleep(1500);

            onView(withText("Delete User")).check(matches(isDisplayed()));

            onView(withId(android.R.id.button2)).perform(click());

            Thread.sleep(1500);

            assertDoesNotExist(onView(withText("TestOrganizer")));


        } catch (InterruptedException e) {
            deleteMockAdmin(mockAdmin);
            deleteMockAdmin(testOrganizer);
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
        deleteMockAdmin(testOrganizer);

    }

    //    US 03.03.01 As an administrator, I want to be able to remove images. (In-Progress - Kishan)
    @Test
    public void deleteImage() {

        User mockAdmin = createMockAdmin();
        Event testEvent = createMockEvent(new Date());
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_temp);

        db.UploadImageToDatabase(bitmap,uri -> {
            testEvent.setImageID(uri);
            db.SaveEvent(testEvent);
        });

        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            closeSoftKeyboard();
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));


//            Go to the users dashboard
            onView(withId(R.id.eventsIcon)).perform((click()));
            Thread.sleep(1500);

            onView(withText("Mock Event")).check(matches(isDisplayed()));


//            Click the delete button
            onView(allOf(withId(R.id.deleteButton),
                    hasSibling(withText("Mock Event"))))
                    .perform(click());

            Thread.sleep(1500);

            onView(withText("Delete Image")).check(matches(isDisplayed()));

            onView(withId(android.R.id.button2)).perform(click());

            Thread.sleep(1500);

            assertDoesNotExist(onView(withText("Mock Event")));


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
        deleteMockEvent(testEvent);

    }

}
