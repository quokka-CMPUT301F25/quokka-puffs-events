package com.example.quokkapuffevents.model;

import java.util.ArrayList;
import java.util.Map;

public class Event {
    private Integer id;
    private User org;
    private String description;
    private Integer numParticipants;
    private QRCode qrCode;
    private Map<User, String> eventUsers; //Have the string be Waitlist, invited, cancelled, etc
    //Add geo data?
}
