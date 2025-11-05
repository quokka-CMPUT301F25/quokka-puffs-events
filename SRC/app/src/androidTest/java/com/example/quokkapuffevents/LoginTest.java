package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.quokkapuffevents.controller.LoginActivity;
import com.example.quokkapuffevents.model.Database;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Test
    public void TestIncorrectPassword() throws InterruptedException {
        Database db = Database.getInstance();

        CountDownLatch latch = new CountDownLatch(1);

        db.CreateMockUser("test@email.com", 0, "password", "username", () -> {
            latch.countDown(); // signal when Firebase write is done
        });

        boolean finished = latch.await(10, TimeUnit.SECONDS);
        assertTrue("User creation timed out", finished);

        // Now the data exists in Firestore — safe to continue
        try (ActivityScenario<LoginActivity> scenario =
                     ActivityScenario.launch(LoginActivity.class)) {

            onView(withId(R.id.login_email_address)).perform(typeText("test@email.com"));
            onView(withId(R.id.login_password)).perform(typeText("wrongpassword"));
            onView(withId(R.id.sign_in_button)).perform(click());
        }
    }

}
