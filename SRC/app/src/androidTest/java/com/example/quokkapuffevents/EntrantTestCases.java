package com.example.quokkapuffevents;

import static android.text.method.TextKeyListener.clear;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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

@RunWith(AndroidJUnit4.class)
public class EntrantTestCases {
    Database db = Database.getInstance();

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

    public User accessEntrantDashboard() {
        User mockEntrant = createMockEntrant();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText(mockEntrant.getEmail()));
            onView(withId(R.id.login_password)).perform(typeText("password"));
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mockEntrant;
    }

    @Test
    public void TestJoinWaitingList() {
        accessEntrantDashboard();


    }

    public void TestViewingEvents() {
        accessEntrantDashboard();


    }

    @Test
    public void UpdateEntrantInfo() {
        User mockEntrant = accessEntrantDashboard();
        try {
            // Go to settings fragment
            onView(withId(R.id.settings_button)).perform(click());

            Thread.sleep(1500);

            // Go to edit profile fragment
            onView(withId(R.id.editProfileBtn)).perform(click());

            Thread.sleep(1500);

            // Type in the changed profile information
            onView(withId(R.id.userFirstNameTextInput)).perform(clearText(), typeText("Changed"));
            onView(withId(R.id.userLastNameTextInput)).perform(clearText(), typeText("Changed"));
            onView(withId(R.id.userEmailTextInput)).perform(clearText(), typeText("Changed"));
            onView(withId(R.id.userContactInformationInput)).perform(clearText(), typeText("Changed"));

            onView(withId(R.id.confirmChangesBtn)).perform(click());

            Thread.sleep(1500);

            db.GetUser(mockEntrant.getId(), user -> {
                onView(withId(R.id.usernameText)).check(matches(withText(user.getUserName())));
                onView(withId(R.id.userFirstAndLastNameText)).check(matches(withText(
                        user.getFirstName() + " " + user.getLastName())));
                onView(withId(R.id.userEmailText)).check(matches(withText(user.getEmail())));
                onView(withId(R.id.userPhoneNumber)).check(matches(withText(user.getPhoneNumber())));
            });
        } catch (InterruptedException e) {
            db.DeleteUser(mockEntrant);
            throw new RuntimeException(e);
        } finally {
            db.DeleteUser(mockEntrant);
        }
    }

    @Test
    public void ReceivingWinningNotif() {
        User mockEntrant = accessEntrantDashboard();
        try {
            // Go to settings fragment
            onView(withId(R.id.settings_button)).perform(click());

            Thread.sleep(1500);

            // Go to edit profile fragment
            onView(withId(R.id.editProfileBtn)).perform(click());

            Thread.sleep(1500);

            // Type in the changed profile information
            onView(withId(R.id.userFirstNameTextInput)).perform(clearText(), typeText("Changed"));
            onView(withId(R.id.userLastNameTextInput)).perform(clearText(), typeText("Changed"));
            onView(withId(R.id.userEmailTextInput)).perform(clearText(), typeText("Changed"));
            onView(withId(R.id.userContactInformationInput)).perform(clearText(), typeText("Changed"));

            onView(withId(R.id.confirmChangesBtn)).perform(click());

            Thread.sleep(1500);

            db.GetUser(mockEntrant.getId(), user -> {
                onView(withId(R.id.usernameText)).check(matches(withText(user.getUserName())));
                onView(withId(R.id.userFirstAndLastNameText)).check(matches(withText(
                        user.getFirstName() + " " + user.getLastName())));
                onView(withId(R.id.userEmailText)).check(matches(withText(user.getEmail())));
                onView(withId(R.id.userPhoneNumber)).check(matches(withText(user.getPhoneNumber())));
            });
        } catch (InterruptedException e) {
            db.DeleteUser(mockEntrant);
            throw new RuntimeException(e);
        } finally {
            db.DeleteUser(mockEntrant);
        }
    }

}
