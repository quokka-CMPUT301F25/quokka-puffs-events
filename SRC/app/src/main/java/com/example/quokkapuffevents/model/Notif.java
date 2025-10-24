package com.example.quokkapuffevents.model;

public class Notif {
    private String id;
    private Integer type; //If just a notification or a choice. 0 or 1
    private String recipient;
    private String originEvent;
    private String originUser;
    private String message;
    private Boolean chosen;
    private Integer choice; //-1 for N/A, 0 for Undecided, 1 For no, 2 for yes


    public Notif(String id, Integer type, String recipient, String originEvent, String originUser, String message){
        this.id = id;
        this.type = type; //If just a notification or a choice
        this.recipient = recipient;
        this.originEvent = originEvent;
        this.originUser = originUser;
        this.message = message;
        this.chosen = false;

        if (type == 1){
            this.choice = 0;
        }
        else {
            this.choice = -1;
        }
    }

    public String getMessage() {
        return message;
    }

    public Integer getId() {
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
