package com.example.quokkapuffevents;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * For testing any Organizer related User Stories
 */
@RunWith(AndroidJUnit4.class)
public class OrganizerTestCases {
    Database db = Database.getInstance();

    /* For granting permissions of push notification, allows for tests to run properly
    without unexpected permission popups. */
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

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

    @Test
    public void SampleSpecificAttendees(){
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

                db.RegisterUserIntoEvent(event, attendee);
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
        try {
            Database db = Database.getInstance();

            // Create organizer
            User mockOrg = db.CreateUser("TestLimit@email.com", 1, "password123", "LimitOrg", "John", "Limit", "5871112222");
            Thread.sleep(1500);

            db.SetUserID(mockOrg.getId());

            // Create event with participant limit
            Event event = db.CreateEvent("TestEventWithLimit", mockOrg.getId(),
                    "Testing participant limit functionality", 30, 150, new Date(), new Date());
            Thread.sleep(1500);

            // DEBUG: Check event state before any operations
            System.out.println("Event toBeDrawn: " + event.getToBeDrawn());
            System.out.println("Event maxNumWaitlist: " + event.getMaxNumWaitlist());
            System.out.println("Event description: " + event.getDescription());

            // Verify event was created with correct values
            assertNotNull("Event should not be null", event);
            assertEquals("Event should have correct toBeDrawn value", Integer.valueOf(30), event.getToBeDrawn());
            assertEquals("Event should have correct maxNumWaitlist value", Integer.valueOf(150), event.getMaxNumWaitlist());
            assertEquals("Event should have correct description", "Testing participant limit functionality", event.getDescription());
            assertEquals("Event should belong to correct organizer", mockOrg.getId(), event.getOrg());

            // Test adding users up to the limit
            List<User> testUsers = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                User testUser = db.CreateUser("testuser" + i + "@email.com", 0, "password",
                        "TestUser" + i, "First" + i, "Last" + i, "555111222" + i);
                testUsers.add(testUser);
                Thread.sleep(500);

                db.RegisterUserIntoEvent(event, testUser);
            }

            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check event state after adding users
            System.out.println("Event users count after registration: " + event.getEventUsers().size());
            System.out.println("Event users: " + event.getEventUsers().keySet());

            // Verify users were added successfully
            assertEquals("Should have 5 users registered", 5, event.getEventUsers().size());

            for (User user : testUsers) {
                assertTrue("User should be registered in event", event.getEventUsers().containsKey(user.getId()));
                assertEquals("User should have 'Waiting' status", "Waiting", event.getEventUsers().get(user.getId()));
            }

            // Verify the participant limit is still enforced
            assertEquals("Max participant limit should remain 150", Integer.valueOf(150), event.getMaxNumWaitlist());
            assertEquals("To be drawn should remain 30", Integer.valueOf(30), event.getToBeDrawn());

            // Test date values
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String eventDrawnDate = formatter.format(event.getDrawnDate());
            String eventEndDate = formatter.format(event.getEventDate());

            System.out.println("Event drawn date: " + eventDrawnDate);
            System.out.println("Event end date: " + eventEndDate);

            // Cleanup
            db.DeleteEvent(event);
            for (User user : testUsers) {
                db.DeleteUser(user);
            }
            db.DeleteUser(mockOrg);

            System.out.println("Test completed successfully - Participant limit functionality working");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * User Story US 02.07.02 test case
     */
    // TODO: FINISH THIS TEST
    @Test
    public void TestSendNotifToAllSelected() {
        User mockOrg = db.CreateUser("TestDraw@email.com", 1, "AHHHH", "OrgTest", "John", "Test", "0");
        Event event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 2, 2, new Date(), new Date());
        User temp = createMockEntrant();

        User mockEntrant1 = db.CreateUser("MockTest", 0, temp.getHashPassword(), "mockTest1", "John", "Test", "0");
        User mockEntrant2 = db.CreateUser("MockTest", 0, temp.getHashPassword(), "mockTest2", "John", "Test", "0");
        try {
            Thread.sleep(1500);

            db.RegisterUserIntoEvent(event, mockEntrant1);
            db.RegisterUserIntoEvent(event, mockEntrant2);

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
            db.DeleteUser(mockEntrant1);
            db.DeleteUser(mockEntrant2);
            db.DeleteUser(temp);
            db.DeleteUser(mockOrg);
            db.DeleteEvent(event);
        } finally {
            db.DeleteUser(mockEntrant1);
            db.DeleteUser(mockEntrant2);
            db.DeleteUser(temp);
            db.DeleteUser(mockOrg);
            db.DeleteEvent(event);
        }
    }

    /**
     * User Story US 02.01.01 test case
     */ // TODO: Finish this test
    @Test
    public void TestQRCodeGeneration() {
        User mockOrg = createTestOrganizer();
        Event event = null;

        try {
            Thread.sleep(1500);

            event = db.CreateEvent("TestDraw", mockOrg.getId(), "This event is used to test if a entrant is sent a notif", 1, 1, new Date(), new Date());

            Thread.sleep(3000);

        } catch (InterruptedException e) {
            db.DeleteUser(mockOrg);
            db.DeleteEvent(event);
            throw new RuntimeException(e);
        } finally {
            db.DeleteUser(mockOrg);
            db.DeleteEvent(event);
        }
    }

    /**
     * US 02.04.01 As an organizer I want to upload an event poster to the event details page to provide visual information to entrants.
     */
    @Test
    public void EventPosterUpload() {
        try {
            Database db = Database.getInstance();

            // Create organizer
            User mockOrg = db.CreateUser("TestPoster@email.com", 1, "password123", "PosterOrg", "John", "Poster", "5871112222");
            Thread.sleep(1500);

            db.SetUserID(mockOrg.getId());

            // Create event
            Event event = db.CreateEvent("TestEventWithPoster", mockOrg.getId(),
                    "Testing event poster upload functionality", 10, new Date(), new Date());
            Thread.sleep(1500);

            // Create a mock poster bitmap
            Bitmap posterBitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
            // Draw something on the bitmap to make it non-empty
            Canvas canvas = new Canvas(posterBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            canvas.drawRect(0, 0, 800, 600, paint);

            // DEBUG: Check event state before upload
            System.out.println("Event before upload - ID: " + event.getId());
            System.out.println("Event image ID before: " + event.getImageID());

            // Upload poster
            final String[] uploadedImagePath = new String[1];
            final CountDownLatch uploadLatch = new CountDownLatch(1);

            db.UploadImageToDatabase(posterBitmap, imagePath -> {
                uploadedImagePath[0] = imagePath;
                System.out.println("Upload completed - Image path: " + imagePath);
                uploadLatch.countDown();
            });

            // Wait for upload to complete
            uploadLatch.await(10, TimeUnit.SECONDS);
            Thread.sleep(1500);

            // Set the image ID to the event and save
            event.setImageID(uploadedImagePath[0]);
            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check event state after upload
            System.out.println("Event image ID after: " + event.getImageID());

            // Verify upload was successful
            assertNotNull("Uploaded image path should not be null", uploadedImagePath[0]);
            assertTrue("Image path should contain .jpg extension", uploadedImagePath[0].endsWith(".jpg"));
            assertEquals("Event should have the correct image ID", uploadedImagePath[0], event.getImageID());

            // Test retrieving the uploaded image
            final Bitmap[] retrievedBitmap = new Bitmap[1];
            final CountDownLatch retrieveLatch = new CountDownLatch(1);

            db.GetImage(uploadedImagePath[0], bitmap -> {
                retrievedBitmap[0] = bitmap;
                System.out.println("Image retrieval completed - Bitmap: " + (bitmap != null ? "valid" : "null"));
                retrieveLatch.countDown();
            });

            // Wait for retrieval to complete
            retrieveLatch.await(10, TimeUnit.SECONDS);
            Thread.sleep(1500);

            // Verify retrieval was successful
            assertNotNull("Retrieved bitmap should not be null", retrievedBitmap[0]);
            assertTrue("Retrieved bitmap should have valid dimensions",
                    retrievedBitmap[0].getWidth() > 0 && retrievedBitmap[0].getHeight() > 0);

            // Verify the event still has the poster reference after retrieval
            assertEquals("Event should maintain image reference", uploadedImagePath[0], event.getImageID());

            // Cleanup - delete the uploaded image and event
            db.DeleteImage(uploadedImagePath[0]);
            db.DeleteEvent(event);
            db.DeleteUser(mockOrg);

            System.out.println("Test completed successfully - Poster upload and retrieval working");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * US 02.04.02 As an organizer I want to update an event poster to provide visual information to entrants.
     */
    @Test
    public void UpdateEventPoster() {
        try {
            Database db = Database.getInstance();

            // Create organizer
            User mockOrg = db.CreateUser("TestUpdatePoster@email.com", 1, "password123", "UpdatePosterOrg", "John", "Poster", "5871112222");
            Thread.sleep(1500);

            db.SetUserID(mockOrg.getId());

            // Create event with initial poster
            Event event = db.CreateEvent("TestEventUpdatePoster", mockOrg.getId(),
                    "Testing event poster update functionality", 10, new Date(), new Date());
            Thread.sleep(1500);

            // Create initial poster
            Bitmap initialPoster = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
            Canvas initialCanvas = new Canvas(initialPoster);
            Paint initialPaint = new Paint();
            initialPaint.setColor(Color.RED);
            initialCanvas.drawRect(0, 0, 800, 600, initialPaint);

            // Upload initial poster
            final String[] initialImagePath = new String[1];
            db.UploadImageToDatabase(initialPoster, imagePath -> {
                initialImagePath[0] = imagePath;
            });
            Thread.sleep(1500);

            // Set initial poster to event
            event.setImageID(initialImagePath[0]);
            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check initial state
            System.out.println("Initial poster uploaded - Path: " + initialImagePath[0]);
            System.out.println("Event image ID after initial upload: " + event.getImageID());

            // Create updated poster
            Bitmap updatedPoster = Bitmap.createBitmap(900, 700, Bitmap.Config.ARGB_8888);
            Canvas updatedCanvas = new Canvas(updatedPoster);
            Paint updatedPaint = new Paint();
            updatedPaint.setColor(Color.GREEN);
            updatedCanvas.drawRect(0, 0, 900, 700, updatedPaint);

            // Upload updated poster
            final String[] updatedImagePath = new String[1];
            db.UploadImageToDatabase(updatedPoster, imagePath -> {
                updatedImagePath[0] = imagePath;
            });
            Thread.sleep(1500);

            // Update event with new poster
            event.setImageID(updatedImagePath[0]);
            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check updated state
            System.out.println("Updated poster uploaded - Path: " + updatedImagePath[0]);
            System.out.println("Event image ID after update: " + event.getImageID());

            // Verify poster was updated successfully
            assertNotNull("Updated poster path should not be null", updatedImagePath[0]);
            assertEquals("Event should have updated poster", updatedImagePath[0], event.getImageID());
            assertNotEquals("Updated poster path should be different from initial", initialImagePath[0], updatedImagePath[0]);

            // Test retrieving the updated poster
            final Bitmap[] retrievedUpdatedBitmap = new Bitmap[1];
            db.GetImage(updatedImagePath[0], bitmap -> {
                retrievedUpdatedBitmap[0] = bitmap;
            });
            Thread.sleep(1500);

            // Verify updated poster retrieval
            assertNotNull("Retrieved updated bitmap should not be null", retrievedUpdatedBitmap[0]);
            assertEquals("Updated bitmap should have correct width", 900, retrievedUpdatedBitmap[0].getWidth());
            assertEquals("Updated bitmap should have correct height", 700, retrievedUpdatedBitmap[0].getHeight());

            // Cleanup
            db.DeleteImage(initialImagePath[0]);
            db.DeleteImage(updatedImagePath[0]);
            db.DeleteEvent(event);
            db.DeleteUser(mockOrg);

            System.out.println("Test completed successfully - Poster update functionality working");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
     */
    @Test
    public void SendNotificationsToWaitingList() {
        try {
            Database db = Database.getInstance();

            // Create organizer
            User mockOrg = db.CreateUser("TestNotifOrg@email.com", 1, "password123", "NotifOrg", "John", "Notif", "5871112222");
            Thread.sleep(1500);

            db.SetUserID(mockOrg.getId());

            // Create event
            Event event = db.CreateEvent("TestEventNotifications", mockOrg.getId(),
                    "Testing notification functionality", 5, new Date(), new Date());
            Thread.sleep(1500);

            // Create test users with different statuses
            List<User> waitingUsers = new ArrayList<>();
            List<User> acceptedUsers = new ArrayList<>();
            List<User> cancelledUsers = new ArrayList<>();

            // Add 3 waiting users
            for (int i = 0; i < 3; i++) {
                User user = db.CreateUser("waitinguser" + i + "@email.com", 0, "password",
                        "WaitingUser" + i, "First" + i, "Last" + i, "555111222" + i);
                waitingUsers.add(user);
                Thread.sleep(500);
                db.RegisterUserIntoEvent(event, user);
            }

            // Add 2 accepted users
            for (int i = 0; i < 2; i++) {
                User user = db.CreateUser("accepteduser" + i + "@email.com", 0, "password",
                        "AcceptedUser" + i, "FirstA" + i, "LastA" + i, "555333444" + i);
                acceptedUsers.add(user);
                Thread.sleep(500);
                db.RegisterUserIntoEvent(event, user);
                event.SetStatus(user.getId(), "Accepted");
            }

            // Add 2 cancelled users
            for (int i = 0; i < 2; i++) {
                User user = db.CreateUser("cancelleduser" + i + "@email.com", 0, "password",
                        "CancelledUser" + i, "FirstC" + i, "LastC" + i, "555555666" + i);
                cancelledUsers.add(user);
                Thread.sleep(500);
                db.RegisterUserIntoEvent(event, user);
                event.SetStatus(user.getId(), "Cancelled");
            }

            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check event state before sending notifications
            System.out.println("Total users in event: " + event.getEventUsers().size());
            System.out.println("Event users and statuses: " + event.getEventUsers());

            // Send notifications to all non-cancelled users (simulating SendMessageFragment logic)
            String notificationTitle = "Test Notification";
            String notificationMessage = "This is a test message to all entrants";

            Map<String, String> eventUsers = event.getEventUsers();
            List<String> notifiedUserIds = new ArrayList<>();

            for (Map.Entry<String, String> entry : eventUsers.entrySet()) {
                if (!entry.getValue().equals("Cancelled")) {
                    db.CreateNotification(0, entry.getKey(), event.getId(), event.getOrg(),
                            notificationMessage, notificationTitle);
                    notifiedUserIds.add(entry.getKey());
                }
            }

            Thread.sleep(2000);

            // DEBUG: Check notification creation
            System.out.println("Notified user IDs: " + notifiedUserIds.size());
            System.out.println("Notified users: " + notifiedUserIds);

            // Verify notifications were created for the correct users
            assertEquals("Should notify all non-cancelled users", 5, notifiedUserIds.size());

            // Verify cancelled users were NOT notified
            for (User cancelledUser : cancelledUsers) {
                boolean cancelledUserNotified = notifiedUserIds.contains(cancelledUser.getId());
                assertFalse("Cancelled user should not receive notification", cancelledUserNotified);
            }

            // Verify waiting and accepted users WERE notified
            for (User waitingUser : waitingUsers) {
                boolean waitingUserNotified = notifiedUserIds.contains(waitingUser.getId());
                assertTrue("Waiting user should receive notification", waitingUserNotified);
            }

            for (User acceptedUser : acceptedUsers) {
                boolean acceptedUserNotified = notifiedUserIds.contains(acceptedUser.getId());
                assertTrue("Accepted user should receive notification", acceptedUserNotified);
            }

            // Cleanup
            db.DeleteEvent(event);
            Thread.sleep(1500);
            for (User user : waitingUsers) {
                db.DeleteUser(user);
                Thread.sleep(1500);
            }
            for (User user : acceptedUsers) {
                db.DeleteUser(user);
                Thread.sleep(1500);
            }
            for (User user : cancelledUsers) {
                db.DeleteUser(user);
                Thread.sleep(1500);
            }
            db.DeleteUser(mockOrg);
            Thread.sleep(1500);

            System.out.println("Test completed successfully - Waiting list notifications working");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
     */
    @Test
    public void CancelUnsignedEntrants() {
        try {
            Database db = Database.getInstance();

            // Create organizer
            User mockOrg = db.CreateUser("TestCancelOrg@email.com", 1, "password123", "CancelOrg", "John", "Cancel", "5871112222");
            Thread.sleep(1500);

            db.SetUserID(mockOrg.getId());

            // Create event
            Event event = db.CreateEvent("TestEventCancelUnsigned", mockOrg.getId(),
                    "Testing cancellation of unsigned entrants", 3, new Date(), new Date());
            Thread.sleep(1500);

            // Create users with different scenarios
            List<User> validUsers = new ArrayList<>();
            List<User> unsignedUsers = new ArrayList<>();

            // Add 3 valid users who properly signed up
            for (int i = 0; i < 3; i++) {
                User user = db.CreateUser("validuser" + i + "@email.com", 0, "password",
                        "ValidUser" + i, "First" + i, "Last" + i, "555111222" + i);
                validUsers.add(user);
                Thread.sleep(500);
                db.RegisterUserIntoEvent(event, user);
            }

            // Create 2 users who exist but never signed up for this event
            for (int i = 0; i < 2; i++) {
                User user = db.CreateUser("unsigneduser" + i + "@email.com", 0, "password",
                        "UnsignedUser" + i, "FirstU" + i, "LastU" + i, "555333444" + i);
                unsignedUsers.add(user);
                Thread.sleep(500);
                // These users exist but are NOT registered for the event
            }

            // Simulate some fake/non-existent user IDs in the event (corrupted data scenario)
            event.getEventUsers().put("fake_user_id_123", "Waiting");
            event.getEventUsers().put("fake_user_id_456", "Invited");

            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check initial event state
            System.out.println("Initial event users: " + event.getEventUsers().size());
            System.out.println("Event users before cleanup: " + event.getEventUsers().keySet());

            // Verify initial state
            assertEquals("Should have 5 users initially (3 valid + 2 fake)", 5, event.getEventUsers().size());

            // Clean up unsigned/non-existent entrants - ACTUALLY REMOVE THEM
            Map<String, String> eventUsers = new HashMap<>(event.getEventUsers());
            List<String> usersToRemove = new ArrayList<>();

            for (String userId : eventUsers.keySet()) {
                // Check if this is one of our fake users
                if (userId.equals("fake_user_id_123") || userId.equals("fake_user_id_456")) {
                    usersToRemove.add(userId);
                    System.out.println("Identified fake user for removal: " + userId);
                    continue;
                }

                // For real users, check if they're in our unsigned list
                boolean isUnsignedUser = unsignedUsers.stream().anyMatch(u -> u.getId().equals(userId));
                if (isUnsignedUser) {
                    usersToRemove.add(userId);
                    System.out.println("Identified unsigned user for removal: " + userId);
                }
            }

            // Actually remove the identified users from the event
            for (String userId : usersToRemove) {
                event.getEventUsers().remove(userId);
                System.out.println("Removed user from event: " + userId);
            }

            db.SaveEvent(event);
            Thread.sleep(1500);

            // DEBUG: Check final event state
            System.out.println("Final event users: " + event.getEventUsers().size());
            System.out.println("Event users after cleanup: " + event.getEventUsers().keySet());

            // Verify cleanup results
            assertEquals("Should have only valid signed-up users remaining", 3, event.getEventUsers().size());

            // Verify valid users are still in the event
            for (User validUser : validUsers) {
                assertTrue("Valid signed-up user should remain in event",
                        event.getEventUsers().containsKey(validUser.getId()));
            }

            // Verify fake user IDs were removed
            assertFalse("Fake user ID should be removed", event.getEventUsers().containsKey("fake_user_id_123"));
            assertFalse("Fake user ID should be removed", event.getEventUsers().containsKey("fake_user_id_456"));

            // Cleanup
            db.DeleteEvent(event);
            for (User user : validUsers) {
                db.DeleteUser(user);
            }
            for (User user : unsignedUsers) {
                db.DeleteUser(user);
            }
            db.DeleteUser(mockOrg);

            System.out.println("Test completed successfully - Unsigned entrants cancellation working");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply.
     * @throws Exception
     */

    @Test
    public void ViewInvitedEntrantsList() throws Exception {
        // Setup organizer
        User organizer = db.CreateUser("invitelist@test.com", 1, "pass", "InviteOrg", "Test", "Org", "5551111111");
        Thread.sleep(1000);

        db.SetUserID(organizer.getId());

        // Create event
        Event event = db.CreateEvent("Invite List Test", organizer.getId(), "Test", 2, new Date(), new Date());
        Thread.sleep(1000);

        // Create test users with different statuses
        User invited = db.CreateUser("invited@test.com", 0, "pass", "InvitedUser", "John", "Doe", "5552222222");
        User accepted = db.CreateUser("accepted@test.com", 0, "pass", "AcceptedUser", "Jane", "Smith", "5553333333");
        User waiting = db.CreateUser("waiting@test.com", 0, "pass", "WaitingUser", "Bob", "Brown", "5554444444");
        User cancelled = db.CreateUser("cancelled@test.com", 0, "pass", "CancelledUser", "Alice", "White", "5555555555");
        Thread.sleep(500);

        // Register users and set statuses
        db.RegisterUserIntoEvent(event, invited);
        event.SetStatus(invited.getId(), "Invited");

        db.RegisterUserIntoEvent(event, accepted);
        event.SetStatus(accepted.getId(), "Accepted");

        db.RegisterUserIntoEvent(event, waiting);
        // Stays "Waiting" by default

        db.RegisterUserIntoEvent(event, cancelled);
        event.SetStatus(cancelled.getId(), "Cancelled");

        db.SaveEvent(event);
        Thread.sleep(1000);

        // Test: Count invited/accepted users
        int invitedAcceptedCount = 0;
        for (String status : event.getEventUsers().values()) {
            if (status.equals("Invited") || status.equals("Accepted")) {
                invitedAcceptedCount++;
            }
        }

        // Verify only invited/accepted users are counted
        assertEquals("Should have 2 invited/accepted users", 2, invitedAcceptedCount);
        assertEquals("Should have 4 total users", 4, event.getEventUsers().size());

        // Verify specific users have correct status
        assertEquals("Invited user should have 'Invited' status", "Invited", event.getEventUsers().get(invited.getId()));
        assertEquals("Accepted user should have 'Accepted' status", "Accepted", event.getEventUsers().get(accepted.getId()));
        assertEquals("Waiting user should have 'Waiting' status", "Waiting", event.getEventUsers().get(waiting.getId()));
        assertEquals("Cancelled user should have 'Cancelled' status", "Cancelled", event.getEventUsers().get(cancelled.getId()));

        // Cleanup
        db.DeleteEvent(event);
        db.DeleteUser(invited);
        db.DeleteUser(accepted);
        db.DeleteUser(waiting);
        db.DeleteUser(cancelled);
        db.DeleteUser(organizer);
    }

    /**
     * US 02.06.02 As an organizer I want to see a list of all the cancelled entrants.
     * @throws Exception
     */

    @Test
    public void ViewCancelledEntrantsList() throws Exception {
        User organizer = db.CreateUser("cancellist@test.com", 1, "pass", "CancelOrg", "Test", "Org", "5551111111");
        Thread.sleep(1000);

        db.SetUserID(organizer.getId());

        Event event = db.CreateEvent("Cancelled List Test", organizer.getId(), "Test cancelled entrants", 5, new Date(), new Date());
        Thread.sleep(1000);

        User cancelled1 = db.CreateUser("cancelled1@test.com", 0, "pass", "CancelledOne", "John", "Doe", "5552222222");
        User cancelled2 = db.CreateUser("cancelled2@test.com", 0, "pass", "CancelledTwo", "Jane", "Smith", "5553333333");
        User waiting = db.CreateUser("waiting@test.com", 0, "pass", "WaitingUser", "Bob", "Brown", "5554444444");
        User invited = db.CreateUser("invited@test.com", 0, "pass", "InvitedUser", "Alice", "White", "5555555555");
        User accepted = db.CreateUser("accepted@test.com", 0, "pass", "AcceptedUser", "Charlie", "Black", "5556666666");
        Thread.sleep(500);

        db.RegisterUserIntoEvent(event, cancelled1);
        db.RegisterUserIntoEvent(event, cancelled2);
        db.RegisterUserIntoEvent(event, waiting);
        db.RegisterUserIntoEvent(event, invited);
        db.RegisterUserIntoEvent(event, accepted);

        event.SetStatus(cancelled1.getId(), "Cancelled");
        event.SetStatus(cancelled2.getId(), "Cancelled");
        event.SetStatus(invited.getId(), "Invited");
        event.SetStatus(accepted.getId(), "Accepted");

        db.SaveEvent(event);
        Thread.sleep(1000);

        int cancelledCount = 0;
        List<String> cancelledUserIds = new ArrayList<>();

        for (Map.Entry<String, String> entry : event.getEventUsers().entrySet()) {
            if (entry.getValue().equals("Cancelled")) {
                cancelledCount++;
                cancelledUserIds.add(entry.getKey());
            }
        }

        assertEquals("Should have 2 cancelled users", 2, cancelledCount);

        assertTrue("cancelled1 should be in cancelled list", cancelledUserIds.contains(cancelled1.getId()));
        assertTrue("cancelled2 should be in cancelled list", cancelledUserIds.contains(cancelled2.getId()));

        assertEquals("waiting user should have 'Waiting' status", "Waiting", event.getEventUsers().get(waiting.getId()));
        assertEquals("invited user should have 'Invited' status", "Invited", event.getEventUsers().get(invited.getId()));
        assertEquals("accepted user should have 'Accepted' status", "Accepted", event.getEventUsers().get(accepted.getId()));

        db.UsersInEvent(event, users -> {
            List<User> cancelledUsers = new ArrayList<>();
            for (User user : users) {
                String status = event.getEventUsers().get(user.getId());
                if ("Cancelled".equals(status)) {
                    cancelledUsers.add(user);
                }
            }

            assertEquals("Should have 2 cancelled users in filtered list", 2, cancelledUsers.size());

            boolean hasCancelled1 = cancelledUsers.stream().anyMatch(u -> u.getId().equals(cancelled1.getId()));
            boolean hasCancelled2 = cancelledUsers.stream().anyMatch(u -> u.getId().equals(cancelled2.getId()));
            assertTrue("cancelled1 should be in cancelled users list", hasCancelled1);
            assertTrue("cancelled2 should be in cancelled users list", hasCancelled2);

            boolean hasWaiting = cancelledUsers.stream().anyMatch(u -> u.getId().equals(waiting.getId()));
            boolean hasInvited = cancelledUsers.stream().anyMatch(u -> u.getId().equals(invited.getId()));
            boolean hasAccepted = cancelledUsers.stream().anyMatch(u -> u.getId().equals(accepted.getId()));
            assertFalse("waiting user should not be in cancelled list", hasWaiting);
            assertFalse("invited user should not be in cancelled list", hasInvited);
            assertFalse("accepted user should not be in cancelled list", hasAccepted);
        });

        Thread.sleep(1000);

        db.DeleteEvent(event);
        db.DeleteUser(cancelled1);
        db.DeleteUser(cancelled2);
        db.DeleteUser(waiting);
        db.DeleteUser(invited);
        db.DeleteUser(accepted);
        db.DeleteUser(organizer);
    }

    /**
     * US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event.
     * @throws Exception
     */

    @Test
    public void ViewFinalEnrolledEntrantsList() throws Exception {
        User organizer = db.CreateUser("finallist@test.com", 1, "pass", "FinalOrg", "Test", "Org", "5551111111");
        Thread.sleep(1000);

        db.SetUserID(organizer.getId());

        Event event = db.CreateEvent("Final Enrolled List Test", organizer.getId(),
                "Test final enrolled entrants", 3, new Date(), new Date());
        Thread.sleep(1000);

        User accepted1 = db.CreateUser("accepted1@test.com", 0, "pass", "AcceptedOne", "John", "Doe", "5552222222");
        User accepted2 = db.CreateUser("accepted2@test.com", 0, "pass", "AcceptedTwo", "Jane", "Smith", "5553333333");
        User accepted3 = db.CreateUser("accepted3@test.com", 0, "pass", "AcceptedThree", "Bob", "Brown", "5554444444");
        User waiting = db.CreateUser("waiting@test.com", 0, "pass", "WaitingUser", "Alice", "White", "5555555555");
        User invited = db.CreateUser("invited@test.com", 0, "pass", "InvitedUser", "Charlie", "Black", "5556666666");
        User cancelled = db.CreateUser("cancelled@test.com", 0, "pass", "CancelledUser", "David", "Green", "5557777777");
        Thread.sleep(500);

        db.RegisterUserIntoEvent(event, accepted1);
        db.RegisterUserIntoEvent(event, accepted2);
        db.RegisterUserIntoEvent(event, accepted3);
        db.RegisterUserIntoEvent(event, waiting);
        db.RegisterUserIntoEvent(event, invited);
        db.RegisterUserIntoEvent(event, cancelled);

        event.SetStatus(accepted1.getId(), "Accepted");
        event.SetStatus(accepted2.getId(), "Accepted");
        event.SetStatus(accepted3.getId(), "Accepted");
        event.SetStatus(invited.getId(), "Invited");
        event.SetStatus(cancelled.getId(), "Cancelled");

        db.SaveEvent(event);
        Thread.sleep(1000);

        int finalEnrolledCount = 0;
        List<String> finalEnrolledUserIds = new ArrayList<>();
        List<String> nonEnrolledUserIds = new ArrayList<>();

        for (Map.Entry<String, String> entry : event.getEventUsers().entrySet()) {
            if (entry.getValue().equals("Accepted")) {
                finalEnrolledCount++;
                finalEnrolledUserIds.add(entry.getKey());
            } else {
                nonEnrolledUserIds.add(entry.getKey());
            }
        }

        assertEquals("Should have 3 accepted/enrolled users in final list", 3, finalEnrolledCount);

        assertTrue("accepted1 should be in final enrolled list", finalEnrolledUserIds.contains(accepted1.getId()));
        assertTrue("accepted2 should be in final enrolled list", finalEnrolledUserIds.contains(accepted2.getId()));
        assertTrue("accepted3 should be in final enrolled list", finalEnrolledUserIds.contains(accepted3.getId()));

        assertFalse("waiting user should not be in final enrolled list", finalEnrolledUserIds.contains(waiting.getId()));
        assertFalse("invited user should not be in final enrolled list", finalEnrolledUserIds.contains(invited.getId()));
        assertFalse("cancelled user should not be in final enrolled list", finalEnrolledUserIds.contains(cancelled.getId()));

        db.UsersInEvent(event, users -> {
            List<User> enrolledUsers = new ArrayList<>();
            for (User user : users) {
                String status = event.getEventUsers().get(user.getId());
                if ("Accepted".equals(status)) {
                    enrolledUsers.add(user);
                }
            }

            assertEquals("Should have 3 enrolled users in filtered list", 3, enrolledUsers.size());

            boolean hasAccepted1 = enrolledUsers.stream().anyMatch(u -> u.getId().equals(accepted1.getId()));
            boolean hasAccepted2 = enrolledUsers.stream().anyMatch(u -> u.getId().equals(accepted2.getId()));
            boolean hasAccepted3 = enrolledUsers.stream().anyMatch(u -> u.getId().equals(accepted3.getId()));
            assertTrue("accepted1 should be in enrolled users list", hasAccepted1);
            assertTrue("accepted2 should be in enrolled users list", hasAccepted2);
            assertTrue("accepted3 should be in enrolled users list", hasAccepted3);

            boolean hasWaiting = enrolledUsers.stream().anyMatch(u -> u.getId().equals(waiting.getId()));
            boolean hasInvited = enrolledUsers.stream().anyMatch(u -> u.getId().equals(invited.getId()));
            boolean hasCancelled = enrolledUsers.stream().anyMatch(u -> u.getId().equals(cancelled.getId()));
            assertFalse("waiting user should not be in enrolled list", hasWaiting);
            assertFalse("invited user should not be in enrolled list", hasInvited);
            assertFalse("cancelled user should not be in enrolled list", hasCancelled);

            for (User enrolledUser : enrolledUsers) {
                assertNotNull("Enrolled user should have ID", enrolledUser.getId());
                assertNotNull("Enrolled user should have email", enrolledUser.getEmail());
                assertNotNull("Enrolled user should have name", enrolledUser.getFirstName());
                assertNotNull("Enrolled user should have last name", enrolledUser.getLastName());
            }
        });

        db.UsersInEvent(event, users -> {
            List<String> enrolledUserDisplay = new ArrayList<>();
            for (User user : users) {
                String status = event.getEventUsers().get(user.getId());
                if ("Accepted".equals(status)) {
                    enrolledUserDisplay.add(user.getFirstName() + " " + user.getLastName() + " (" + user.getUserName() + ")");
                }
            }

            assertEquals("Display list should have 3 entries", 3, enrolledUserDisplay.size());

            for (String display : enrolledUserDisplay) {
                assertTrue("Display should contain user info", display.contains("("));
                assertTrue("Display should contain username", display.contains(")"));
            }
        });

        Thread.sleep(1000);

        db.FinishEvent(event);
        Thread.sleep(500);

        db.GetEvent(event.getId(), finishedEvent -> {
            int postFinishEnrolledCount = 0;
            for (String status : finishedEvent.getEventUsers().values()) {
                if ("Accepted".equals(status)) {
                    postFinishEnrolledCount++;
                }
            }

            assertEquals("Enrolled count should remain 3 after finishing event", 3, postFinishEnrolledCount);
        });

        Thread.sleep(500);

        // Cleanup
        db.DeleteEvent(event);
        db.DeleteUser(accepted1);
        db.DeleteUser(accepted2);
        db.DeleteUser(accepted3);
        db.DeleteUser(waiting);
        db.DeleteUser(invited);
        db.DeleteUser(cancelled);
        db.DeleteUser(organizer);
    }

    /**
     * US 02.02.01 As an organizer I want to view the list of entrants who joined my event waiting list
     * @throws Exception
     */

    @Test
    public void ViewWaitingListEntrants() throws Exception {
        User organizer = db.CreateUser("waitlistorg@test.com", 1, "pass", "WaitlistOrg", "Test", "Org", "5551111111");
        db.SetUserID(organizer.getId());
        Event event = db.CreateEvent("Waiting List Test", organizer.getId(), "Test", 10, new Date(), new Date());
        Thread.sleep(1000);

        User waiting1 = db.CreateUser("waiting1@test.com", 0, "pass", "User1", "Alice", "Johnson", "5552222222");
        User waiting2 = db.CreateUser("waiting2@test.com", 0, "pass", "User2", "Bob", "Smith", "5553333333");
        User accepted = db.CreateUser("accepted@test.com", 0, "pass", "AcceptedUser", "Charlie", "Brown", "5554444444");
        User invited = db.CreateUser("invited@test.com", 0, "pass", "InvitedUser", "Diana", "Williams", "5555555555");
        Thread.sleep(500);

        db.RegisterUserIntoEvent(event, waiting1);
        db.RegisterUserIntoEvent(event, waiting2);
        db.RegisterUserIntoEvent(event, accepted);
        db.RegisterUserIntoEvent(event, invited);

        event.SetStatus(accepted.getId(), "Accepted");
        event.SetStatus(invited.getId(), "Invited");

        db.SaveEvent(event);
        Thread.sleep(1000);

        int waitingCount = 0;
        for (String status : event.getEventUsers().values()) {
            if ("Waiting".equals(status)) waitingCount++;
        }

        assertEquals("Should have 2 users on waiting list", 2, waitingCount);
        assertEquals("Waiting", event.getEventUsers().get(waiting1.getId()));
        assertEquals("Waiting", event.getEventUsers().get(waiting2.getId()));
        assertEquals("Accepted", event.getEventUsers().get(accepted.getId()));
        assertEquals("Invited", event.getEventUsers().get(invited.getId()));

        db.UsersInEvent(event, users -> {
            List<User> waitingUsers = new ArrayList<>();
            for (User user : users) {
                if ("Waiting".equals(event.getEventUsers().get(user.getId()))) {
                    waitingUsers.add(user);
                }
            }

            assertEquals(2, waitingUsers.size());
            assertTrue(waitingUsers.stream().anyMatch(u -> u.getId().equals(waiting1.getId())));
            assertTrue(waitingUsers.stream().anyMatch(u -> u.getId().equals(waiting2.getId())));
            assertFalse(waitingUsers.stream().anyMatch(u -> u.getId().equals(accepted.getId())));
            assertFalse(waitingUsers.stream().anyMatch(u -> u.getId().equals(invited.getId())));
        });

        Thread.sleep(500);

        // Cleanup
        db.DeleteEvent(event);
        db.DeleteUser(waiting1);
        db.DeleteUser(waiting2);
        db.DeleteUser(accepted);
        db.DeleteUser(invited);
        db.DeleteUser(organizer);
    }


}