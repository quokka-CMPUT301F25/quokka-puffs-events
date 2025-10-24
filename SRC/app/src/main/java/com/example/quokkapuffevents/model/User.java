package com.example.quokkapuffevents.model;

import java.util.ArrayList;

public class User {
    private String id;
    private String email;
    private String hashPassword;
    private String userName;
    private ArrayList<String> events;
    private Integer accountType; //-'ve Admin, 0 for normal user, +'ve for organiser
    //etc etc



    public User(String id, String email, Integer accountType, String hashPassword, String userName){
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.userName = userName;
        this.events = new ArrayList();
        this.accountType = accountType;
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
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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



}
