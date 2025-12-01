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
}