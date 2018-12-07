package com.harlie.codechallenge.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//
// Java POJO created from JSON at http://www.jsonschema2pojo.org/
// sample source data:    {"to":{"id":"e5DBwdRt7Zg=","name":"Virgil Macias"},"from":{"id":"WI1k3IAUnnY=","name":"Adam Fields"},"timestamp":"1543621338009","areFriends":true}
//

public class PartyLogItem {

    @SerializedName("to")
    @Expose
    private To to;
    @SerializedName("from")
    @Expose
    private From from;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("areFriends")
    @Expose
    private Boolean areFriends;

    public To getTo() {
        return to;
    }

    public void setTo(To to) {
        this.to = to;
    }

    public From getFrom() {
        return from;
    }

    public void setFrom(From from) {
        this.from = from;
    }

    public String getTimestamp() {
        Date date = new Date();
        date.setTime(getTimestampAsLong());
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(date);
    }

    public Long getTimestampAsLong() {
        return Long.valueOf(timestamp);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getAreFriends() {
        return areFriends;
    }

    public void setAreFriends(Boolean areFriends) {
        this.areFriends = areFriends;
    }

    // some convenience methods:

    public String getToNameAndId() {
        return getToName() + ": " + getToId();
    }

    public String getFromNameAndId() {
        return getFromName() + ": " + getFromId();
    }

    public String getToId() {
        return to.getId();
    }

    public String getToName() {
        return to.getName();
    }

    public String getFromId() {
        return from.getId();
    }

    public String getFromName() {
        return from.getName();
    }

    public String getFriend() {
        return String.valueOf(areFriends);
    }

    @Override
    public String toString() {
        return "PartyLogItem{" +
                "to=" + to +
                ", from=" + from +
                ", timestamp='" + timestamp + '\'' +
                ", areFriends=" + areFriends +
                '}';
    }
}
