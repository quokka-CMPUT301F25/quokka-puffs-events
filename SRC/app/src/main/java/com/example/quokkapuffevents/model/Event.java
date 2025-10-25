package com.example.quokkapuffevents.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class Event {
    private String id;
    private String org;
    private String description;
    private Integer toBeDrawn;
    private Integer maxNumWaitlist;
    //TODO
    //private QRCode qrCode;
    private Map<String, String> eventUsers; //Have the string be Waitlist, invited, cancelled, etc
    private Date startDate;
    private Date endDate;
    private String imageID;
    //Add geo data?
    private Boolean drawn;

    public Event(){
        this.id = "FAILURE";
    }
    public Event(String id, String org, String description, Integer toBeDrawn, Optional<Integer> maxNumWaitlist, Date startDate, Date endDate){
        Integer numWait = maxNumWaitlist.isPresent() ? maxNumWaitlist.get() : -1;

        this.id = id;
        this.description = description;
        this.toBeDrawn = toBeDrawn;
        this.maxNumWaitlist = numWait;
        this.startDate = startDate;
        this.endDate = endDate;
        this.drawn = false;
        this.imageID = null;
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
        this.eventUsers.put(userID, "Waiting");
    }

    public ArrayList<String> drawUsers(Integer numCalled){
        ArrayList<String> waitingUsers = new ArrayList<>(); //Create empty list to hold users that are still on the waiting list
        Random r= new Random(); //Random class for the draw

        //Collect all users from the eventUsers Map that is still waiting
        for (Map.Entry<String, String> entry : this.eventUsers.entrySet()) {
            if (Objects.equals(entry.getValue(), "Waiting")) {
                waitingUsers.add(entry.getKey());
            }
        }
        //Ensure that no error
        //TODO add authentication before here
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

            this.eventUsers.put(chosenUser, "Invited"); //Update map to show that they have been invited
        }

        return(chosen); //Return list of ids
    }
}
