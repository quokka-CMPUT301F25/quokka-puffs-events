package com.example.quokkapuffevents.model;

import java.util.ArrayList;

public class User {
    private String id;
    private String email;
    private String hashPassword;
    private String username;
    private ArrayList<String> events;
    private Integer accountType; //-'ve Admin, 0 for normal user, +'ve for organiser
    private Boolean sendNotifications;
    //etc etc


    public User(){
        this.id = "FAILURE";
    }
    public User(String id, String email, Integer accountType, String hashPassword, String username){
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.username = username;
        this.events = new ArrayList<>();
        this.accountType = accountType;
        this.sendNotifications = true;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getSendNotifications() {
        return sendNotifications;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSendNotifications(Boolean sendNotifications) {
        this.sendNotifications = sendNotifications;
    }
}
