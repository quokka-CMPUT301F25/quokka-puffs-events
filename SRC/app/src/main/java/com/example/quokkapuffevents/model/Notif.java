package com.example.quokkapuffevents.model;

import java.util.Date;

public class Notif {
    private String id;
    private Integer type = 0; //If just an alert or a choice. 0 or alert, 1 for choice.
    private String recipient;
    private String originEvent;
    private String originUser;
    private String message = "";
    private Boolean chosen = false;
    private Integer choice; //-1 for N/A, 0 for Undecided, 1 For no, 2 for yes
    private Date timeStamp;
    private String title;



    /**
     * Normal constructor for a notification.
     * @param id
     * The String ID associated with a notification.
     * @param type
     * An Integer representing this particular notification as just an alert or containing a choice
     * (0 for alert, 1 for choice).
     * @param recipient
     * A String containing who is receiving the notification.
     * @param originEvent
     * A String detailing which event this notification comes from.
     * @param originUser
     * A String detailing who sent out this notification.
     * @param message
     * A String containing what the notification's text description contains.
     * @param title
     * Title for the notification
     */
    public Notif(String id, Integer type, String recipient, String originEvent, String originUser, String message, String title){
        this.id = id;
        this.type = type; //If just an alert or a choice
        this.recipient = recipient;
        this.originEvent = originEvent;
        this.originUser = originUser;
        this.message = message;
        this.chosen = false;
        this.timeStamp = new Date();
        this.title = title;

        //If type = 1 then this is an invitation and there must be a choice.
        if (type == 1){
            this.choice = 0; //0 Means that no choice has been made
        }
        else { //If it is not an invitation then there is no need to have a choice
            this.choice = -1; //-1 Means same as N/A
        }
    }

    /**
     * This is an empty Notification constructor. Sets the ID to "FAILURE". If the ID is "FAILURE"
     * then something has gone wrong.
     */
    public Notif(){
        this.id = "FAILURE";
    }

    // Getters and Setters

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
