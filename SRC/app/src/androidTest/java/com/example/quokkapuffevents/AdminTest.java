package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static java.util.EnumSet.allOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quokkapuffevents.controller.AdminActivity;
import com.example.quokkapuffevents.controller.LoginActivity;
import com.example.quokkapuffevents.model.Event;
import com.example.quokkapuffevents.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.example.quokkapuffevents.model.Database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;


@RunWith(AndroidJUnit4.class)
public class AdminTest {

    Database db = Database.getInstance();

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

    public void deleteMockAdmin(User user) {
        db.DeleteUser(user);
    }

    @Test
    public void accessAdminDashboard() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
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

    @Test
    public void accessAdminImages() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
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

    @Test
    public void signOutAdmin() {
        User mockAdmin = createMockAdmin();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("TestingAdmin"));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.adminTitle)).check(matches(isDisplayed()));

            onView(withId(R.id.settingsIcon)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.signOutBtn)).perform(click());

            Thread.sleep(1500);

            onView(withId(R.id.login_information_container)).check(matches(isDisplayed()));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        deleteMockAdmin(mockAdmin);
    }


}
