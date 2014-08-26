package com.sointeractive.getresults.app.data.isaacloud;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data store class for notifications.
 */

public class Notification {

    private JSONObject data;
    private String title, message;

    public Notification(final JSONObject data, final String title, final String message) {
        this.data = data;
        //this.title = title;
        this.message = message;
    }

    public Notification(final JSONObject json) throws JSONException {
        this.data = json.getJSONObject("data");
        //this.title = data.getString("");
        this.message = data.getJSONObject("body").getString("message");
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(final JSONObject data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }


}