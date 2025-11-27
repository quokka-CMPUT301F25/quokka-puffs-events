package com.example.quokkapuffevents.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
    private String fcmToken;




    /**
     * Normal constructor for a User.
     * @param id
     * The String ID associated with the User.
     * @param email
     * The String Email Address of the User.
     * @param accountType
     * An integer representing what kind of account this User is (-1 for Admin, 0 for Entrant, 1 for
     * Organizer).
     * @param hashPassword
     * The String hashed password of the User.
     * @param username
     * The String Username of the User.
     * @param firstName
     * The String First Name of the User.
     * @param lastName
     * The String Last Name of the User.
     * @param phoneNumber
     * The String Phone Number of the User (Optional)
     */
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

    /**
     * This is an empty User constructor. Sets the ID to "FAILURE". If the ID is "FAILURE"
     * then something has gone wrong.
     */
    public User(){
        this.id = "FAILURE";
    }

    // Getters and Setters

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

    public Boolean getSendNotifications() {
        return sendNotifications;
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

    public void addEvent(String eventID){
        this.events.add(eventID);
    }
    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
}
