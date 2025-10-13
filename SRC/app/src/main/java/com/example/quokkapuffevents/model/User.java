package com.example.quokkapuffevents.model;

public class User {
    private String id;
    private String email;
    private Integer accountType; //-'ve Admin, 0 for normal user, +'ve for organiser
    //etc etc



    public User(String id, String email, Integer accountType){
        this.id = id;
        this.email = email;
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
}
