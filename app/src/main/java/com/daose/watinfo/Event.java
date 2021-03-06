package com.daose.watinfo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by student on 25/06/16.
 */
public class Event implements Serializable {
    private String name;
    private String time;
    private String location;
    //private String date;
    private Date date;
    private String description;
    private long voteFood;
    private long voteShirt;
    private boolean isFoodVoted;
    private boolean isShirtVoted;
    private boolean isDateHeader;
    //faculty, specification?

    public Event(String name, String location, String time, Date date) {
        this.name = name;
        this.location = location;
        this.time = time;
        this.date = date;
        isDateHeader = false;
        voteFood = 0;
        voteShirt = 0;
    }

    public Event(Date date) {
        isDateHeader = true;
        this.date = date;
    }

    public boolean isDateHeader() {
        return isDateHeader;
    }

    public boolean isFoodVoted() {
        return isFoodVoted;
    }

    public void setFoodVoted(boolean voted) {
        this.isFoodVoted = voted;
    }

    public boolean isShirtVoted() {
        return isShirtVoted;
    }

    public Date getDate() {
        return this.date;
    }

    public String getDatabaseName() {
        return this.name.replace('.', '_').replace('#', '_');
    }

    public void setShirtVoted(boolean voted) {
        this.isShirtVoted = voted;
    }

    public long getVoteFood() {
        return this.voteFood;
    }

    public long getVoteShirt() {
        return this.voteShirt;
    }

    public void setVoteFood(long i) {
        this.voteFood = i;
    }

    public void setVoteShirt(long i) {
        this.voteShirt = i;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getTime() {
        return this.time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

