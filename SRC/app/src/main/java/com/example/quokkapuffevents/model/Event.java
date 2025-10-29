package com.example.quokkapuffevents.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private Date endDate;
    private String imageID;
    //Add geo data?
    private Boolean drawn;

    public Event(){
        this.id = "FAILURE";
    }
    public Event(String id, String name, String org, String description, Integer toBeDrawn, Date startDate, Date drawnDate, Date endDate){
        this.name = name;
        this.id = id;
        this.org = org;
        this.description = description;
        this.toBeDrawn = toBeDrawn;
        this.maxNumWaitlist = -1;
        this.startDate = startDate;
        this.drawnDate = drawnDate;
        this.endDate = endDate;
        this.drawn = false;
        this.imageID = null;
        this.eventUsers = null;
    }
    public Event(String id, String name, String org, String description, Integer toBeDrawn, Integer maxNumWaitlist, Date startDate, Date drawnDate, Date endDate){
        this.name = name;
        this.id = id;
        this.org = org;
        this.description = description;
        this.toBeDrawn = toBeDrawn;
        this.maxNumWaitlist = maxNumWaitlist;
        this.startDate = startDate;
        this.drawnDate = drawnDate;
        this.endDate = endDate;
        this.drawn = false;
        this.imageID = null;
        this.eventUsers = null;
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

    public String getId() {
        return id;
    }

    public Date getEndDate() {
        return endDate;
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
    public void addUser(String userID){
        //Adding an entry to the map
        this.eventUsers.put(userID, "Waiting");
    }

    public void SetStatus (String userID, String newStatus) {
        //Changing the status of a user
        this.eventUsers.put(userID, newStatus);
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
        Random r= new Random(); //Random class for the draw

        //Collect all users from the eventUsers Map that is still waiting
        for (Map.Entry<String, String> entry : this.eventUsers.entrySet()) {
            if (Objects.equals(entry.getValue(), "Waiting")) {
                waitingUsers.add(entry.getKey());
            }
        }
        //Ensure that no error. Easier to do here than anywhere else
        if (numCalled > waitingUsers.size()){
            numCalled = waitingUsers.size();
        }

        //List of drawn user ids
        ArrayList<String> chosen = new ArrayList<>();
        //Draw however many are needed
        for (int i = 0; i < numCalled; i++){
            Integer randInd = r.nextInt(waitingUsers.size()); //Get random index
            String chosenUser = waitingUsers.get(randInd); //Collect user id
            waitingUsers.remove(randInd); //Remove user from waiting so that they cannot be chosen again

            SetStatus(chosenUser, "Invited"); //Update map to show that they have been invited
        }

        return(chosen); //Return list of ids
    }
}