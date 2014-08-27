package com.sointeractive.getresults.app.data.isaacloud;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Data store class for notifications.
 */

public class Notification implements Serializable {

    private JSONObject data;
    private String title, message;
    private Date createdAt;

    public Notification(JSONObject data,String message, Date createdAt) {
        this.data = data;
        this.createdAt = createdAt;
        this.title = null;
        this.message = message;
    }

    public Notification(JSONObject json) throws JSONException {
        this.data = json.getJSONObject("data");
        this.createdAt = new Date(Long.valueOf(json.getString("createdAt")));
        //this.title = data.getString("");
        this.message = data.getJSONObject("body").getString("message");
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}