package com.example.quokkapuffevents.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Event {
    private String id;
    private String name;
    private String org;
    private String description;
    private Integer toBeDrawn;
    private Integer maxNumWaitlist;
    //TODO
    //private QRCode qrCode;
    //private Geo geo;????
    private Map<String, String> eventUsers = new HashMap<>(); //Have the string be Waitlist, invited, cancelled, etc
    private Date startDate;
    private Date drawnDate;
    private Date eventDate;
    private String imageID;
    //Add geo data?
    private Boolean drawn;

    // Two versions of Event constructor, one version for no max waitlist capacity, the other including it.

    /**
     * The default constructor for Event. Does not have a limit on the max waitlist capacity.
     * @param id
     * @param name
     * @param org
     * @param description
     * @param toBeDrawn
     * @param drawnDate
     * @param eventDate
     */
    public Event(String id, String name, String org, String description, Integer toBeDrawn, Date drawnDate, Date eventDate){
        this.name = name;
        this.id = id;
        this.org = org;
        this.description = description;
        this.toBeDrawn = toBeDrawn;
        this.maxNumWaitlist = -1;
        this.startDate = new Date();
        this.drawnDate = drawnDate;
        this.eventDate = eventDate;
        this.drawn = false;
        this.imageID = null;
    }

    /**
     * The altered constructor for Event. Contains a limit on the max waitlist capacity.
     * @param id
     * @param name
     * @param org
     * @param description
     * @param toBeDrawn
     * @param maxNumWaitlist
     * @param drawnDate
     * @param eventDate
     */
    public Event(String id, String name, String org, String description, Integer toBeDrawn, Integer maxNumWaitlist, Date drawnDate, Date eventDate){
        this.name = name;
        this.id = id;
        this.org = org;
        this.description = description;
        this.toBeDrawn = toBeDrawn;
        this.maxNumWaitlist = maxNumWaitlist;
        this.startDate =  new Date();
        this.drawnDate = drawnDate;
        this.eventDate = eventDate;
        this.drawn = false;
        this.imageID = null;
    }

    /**
     *
     */
    public Event(){
        this.id = "FAILURE";
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDrawn(Boolean drawn) {
        this.drawn = drawn;
    }

    public Boolean getDrawn() {
        return drawn;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getImageID() {
        return imageID;
    }

    public String getId() {
        return id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getDrawnDate() {
        return drawnDate;
    }

    public Integer getMaxNumWaitlist() {
        return maxNumWaitlist;
    }

    public Integer getToBeDrawn() {
        return toBeDrawn;
    }

    public Map<String, String> getEventUsers() {
        return eventUsers;
    }

    public String getDescription() {
        return description;
    }

    public String getOrg() {
        return org;
    }

    //Unique methods

    /**
     *
     * @param userID
     */
    public void addUser(String userID){
        //Adding an entry to the map
        eventUsers.put(userID, "Waiting");
    }

    /**
     *
     * @param userID
     * @param newStatus
     */
    public void SetStatus (String userID, String newStatus) {
        //Changing the status of a user
        if(eventUsers.size() <= maxNumWaitlist)
        {
            eventUsers.put(userID, newStatus);
        }
    }

    /**
     * Randomly chooses the number of people to to draw from the list that are waiting for this event.
     * @param numCalled
     * The number of people to draw
     * @return
     * Return the array list of all of the drawn users
     */
    public ArrayList<String> drawUsers(Integer numCalled){
        ArrayList<String> waitingUsers = new ArrayList<>(); //Create empty list to hold users that are still on the waiting list
        Random r= new Random(); //Random class for the draw
        Database db = Database.getInstance();

        //Collect all users from the eventUsers Map that is still waiting
        for (String entry : eventUsers.keySet()) {
            if (eventUsers.get(entry).equals("Waiting")) {
                waitingUsers.add(entry);
            }
        }
        //Ensure that no error. Easier to do here than anywhere else
        if (numCalled > waitingUsers.size()){
            numCalled = waitingUsers.size();
        }
        if (numCalled == -1){
            numCalled = toBeDrawn;
        }

        //List of drawn user ids
        ArrayList<String> chosen = new ArrayList<>();
        //Draw however many are needed
        for (int i = 0; i < numCalled; i++){
            int randInd = r.nextInt(waitingUsers.size()); //Get random index
            String chosenUser = waitingUsers.get(randInd); //Collect user id
            chosen.add(chosenUser);
            waitingUsers.remove(chosenUser); //Remove user from waiting so that they cannot be chosen again

            SetStatus(chosenUser, "Invited"); //Update map to show that they have been invited

            db.CreateNotification(1, chosenUser, id, org, "You have been drawn for this event.");
        }
        for (String user : waitingUsers){
            db.CreateNotification(0, user, id, org, "This event has been drawn. Unfortunately you were not drawn, there is a chance that you may be drawn in the future.");
        }

        return(chosen); //Return list of ids
    }

}