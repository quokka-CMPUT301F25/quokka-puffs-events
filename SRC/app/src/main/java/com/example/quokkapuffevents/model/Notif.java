package com.example.quokkapuffevents.model;

import com.google.type.DateTime;

import java.util.Date;

public class Notif {
    private String id;
    private Integer type; //If just a notification or a choice. 0 or 1
    private String recipient;
    private String originEvent;
    private String originUser;
    private String message;
    private Boolean chosen;
    private Integer choice; //-1 for N/A, 0 for Undecided, 1 For no, 2 for yes
    private Date timeStamp;
    public Notif(String id, Integer type, String recipient, String originEvent, String originUser, String message){
        /**
         * Normal constructor for a notification
         */

        this.id = id;
        this.type = type; //If just a notification or a choice
        this.recipient = recipient;
        this.originEvent = originEvent;
        this.originUser = originUser;
        this.message = message;
        this.chosen = false;
        this.timeStamp = new Date();

        //If type = 1 then this is an invitation and there must be a choice.
        if (type == 1){
            this.choice = 0; //0 Means that no choice has been made
        }
        else { //If it is not an invitation then there is no need to have a choice
            this.choice = -1; //-1 Means same as N/A
        }
    }

    public Notif(){
        /**
         * This is an empty notification. If the ID is "Failure" then something has gone wrong.
         */

        this.id = "FAILURE";
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }

    public String getOriginEvent() {
        return originEvent;
    }

    public String getOriginUser() {
        return originUser;
    }

    public String getRecipient() {
        return recipient;
    }

    public Boolean getChosen() {
        return chosen;
    }

    public Integer getChoice() {
        return choice;
    }

    public void setChoice(Integer choice) {
        this.choice = choice;
    }

    public void setChosen(Boolean chosen) {
        this.chosen = chosen;
    }
}
