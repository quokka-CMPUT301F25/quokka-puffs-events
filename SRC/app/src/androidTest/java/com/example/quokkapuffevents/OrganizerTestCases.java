package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * For testing any Organizer related User Stories
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerTestCases {

    Database db = Database.getInstance();


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

//    US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list. (In-progress KYLE)
//    US 02.01.04 As an organizer, I want to set a registration period.

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


}
