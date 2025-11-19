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
    private Integer numPeopleWaiting;
    private Map<String, String> eventUsers = new HashMap<>(); //Have the string be Waitlist, invited, cancelled, etc
    private Date startDate;
    private Date drawnDate;
    private Date eventDate;
    private String imageID;
    private String qrcodeID;
    //Add geo data?
    private Boolean drawn;

    public Event(){
        this.id = "FAILURE";
    }
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
        this.qrcodeID = null;
        this.numPeopleWaiting = 0;
    }
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
        this.qrcodeID = null;
        this.numPeopleWaiting = 0;
    }

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

    public void setQrcodeID(String qrcodeID) {this.qrcodeID = qrcodeID;}

    public String getQrcodeID() {return qrcodeID;}

    public String getId() {
        return id;
    }

    public Integer getNumPeopleWaiting() {
        return numPeopleWaiting;
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


    //Actual methods
    public void SetStatus (String userID, String newStatus) {
        //Changing the status of a user
        eventUsers.put(userID, newStatus);
        Log.d("FUCK", "Got here");
        if (Objects.equals(newStatus, "Cancelled")){
            numPeopleWaiting -= 1;
        } else if (Objects.equals(newStatus, "Waiting")){
            numPeopleWaiting += 1;
        }
    }

    public ArrayList<String> drawUsers(Integer numCalled){
        /**
         * This randomly chooses numCalled people that are waiting for this event
         * @param numCalled
         * The number of people to draw
         * @return
         * Return the array list of all of the drawn users
         */
        ArrayList<String> waitingUsers = new ArrayList<>(); //Create empty list to hold users that are still on the waiting list
        Random r = new Random(); //Random class for the draw
        Database db = Database.getInstance();

        //Collect all users from the eventUsers Map that is still waiting
        for (String entry : eventUsers.keySet()) {
            if (eventUsers.get(entry).equals("Waiting")) {
                waitingUsers.add(entry);
            }
        }
        //Ensure that no error. Easier to do here than anywhere else
        if (numCalled == -1){
            numCalled = toBeDrawn;
        }
        if (numCalled > waitingUsers.size()){
            numCalled = waitingUsers.size();
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