package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented tests for NotificationDash fragment.
 * These run on an emulator/device because they involve Android UI.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationDashTest {

    @Test
    public void testFragmentLaunchesAndListViewExists() {
        try (ActivityScenario<FragmentHostActivity> scenario =
                     ActivityScenario.launch(FragmentHostActivity.class)) {

            // Verify the ListView with id=NotifList exists and is visible
            onView(withId(R.id.NotifList))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void testAdapterInitiallyEmpty() {
        try (ActivityScenario<FragmentHostActivity> scenario =
                     ActivityScenario.launch(FragmentHostActivity.class)) {

            scenario.onActivity(activity -> {
                // Access the fragment’s ListView through the inflated layout
                ListView listView = activity.findViewById(R.id.NotifList);
                assertNotNull("ListView should exist", listView);
                assertNotNull("Adapter should be attached", listView.getAdapter());
                assertEquals("Adapter should start empty", 0, listView.getAdapter().getCount());
            });
        }
    }
}
