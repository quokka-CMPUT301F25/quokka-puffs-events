package com.example.quokkapuffevents;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.quokkapuffevents.model.Database;
import com.example.quokkapuffevents.model.Notif;
import com.example.quokkapuffevents.model.User;
import com.example.quokkapuffevents.view.NotificationArrayAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for NotificationArrayAdapter.
 * These are instrumentation tests (run on device) but do not require a Fragment or Activity.
 */
@RunWith(AndroidJUnit4.class)
public class NotificationArrayAdapterTest {

    private NotificationArrayAdapter adapter;
    private Context context;
    private Database db;

    @Before
    public void setUp() {
        // Initialize database and mock user
        db = Database.getInstance();
        User mockUser = db.CreateUser("Email", 0, "HashPass", "UserName");
        db.SetUserID(mockUser.getId());

        // Get a valid context
        context = ApplicationProvider.getApplicationContext();

        // Initialize the adapter with an empty list
        adapter = new NotificationArrayAdapter(context, new ArrayList<>());
    }

    @Test
    public void testSetNotificationsUpdatesList() {
        // Create some fake notifications
        List<Notif> testNotifs = new ArrayList<>();
        testNotifs.add(new Notif("ID", 0, "r1", "e1", "o1", "m1"));
        testNotifs.add(new Notif("ID1", 1, "r2", "e2", "o2", "m2"));

        // Update adapter
        adapter.setNotifications(testNotifs);

        // Validate size and content
        assertEquals(2, adapter.getCount());
        assertEquals("o1", adapter.getItem(0).getOriginUser());
    }

    @Test
    public void testRemoveNotificationRemovesCorrectly() {
        Notif n1 = new Notif("ID", 0, "r1", "e1", "o1", "m1");
        Notif n2 = new Notif("ID1", 1, "r2", "e2", "o2", "m2");

        adapter.setNotifications(List.of(n1, n2));
        adapter.removeNotification(n1);

        assertEquals(1, adapter.getCount());
        assertEquals("o2", adapter.getItem(0).getOriginUser());
    }

    @Test
    public void testGetItemAndCount() {
        Notif n1 = new Notif("ID", 0, "r1", "e1", "o1", "m1");
        adapter.setNotifications(List.of(n1));

        assertEquals(1, adapter.getCount());
        assertEquals(n1, adapter.getItem(0));
    }
}
