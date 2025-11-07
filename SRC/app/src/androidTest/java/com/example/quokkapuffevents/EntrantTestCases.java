package com.example.quokkapuffevents;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.widget.ListView;

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

    @Test
    public void accessEntrantDashboard() {
        User mockEntrant = createMockEntrant();
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText(mockEntrant.getEmail()));
            onView(withId(R.id.login_password)).perform(typeText(mockEntrant.getHashPassword()));
            onView(withId(R.id.sign_in_button)).perform(click());

            Thread.sleep(1500);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void TestJoinWaitingList() {
        accessEntrantDashboard();

        Event event = db.CreateEvent("Mock Event", "Mock Organizer", "Mock Description", 10, new Date(), new Date());

        onView(withId(R.id.all_events_button)).perform(click());
        ListView allEventsList = .findViewById(R.id.all_events_list);

        assert(event.getEventUsers().get("TestingEntrant") == null);

    }

    public void TestViewingEvents() {
        accessEntrantDashboard();


    }
}
