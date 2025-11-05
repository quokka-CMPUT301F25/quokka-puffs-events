package com.example.quokkapuffevents.model;

import java.util.ArrayList;

public class User {
    private String id;
    private String email;
    private String hashPassword;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private ArrayList<String> events;
    private Integer accountType; //-1 Admin, 0 for entrant, +1 for organiser
    private Boolean sendNotifications;
    //etc etc


    public User(){
        this.id = "FAILURE";
    }
    public User(String id, String email, Integer accountType, String hashPassword, String username, String firstName, String lastName, String phoneNumber){
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
