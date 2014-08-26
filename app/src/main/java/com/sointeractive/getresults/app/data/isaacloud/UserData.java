package com.sointeractive.getresults.app.data.isaacloud;

import android.util.Log;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.pebble.responses.LoginResponse;
import com.sointeractive.getresults.app.pebble.responses.ResponseItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class UserData implements Serializable {
    private static final String TAG = UserData.class.getSimpleName();

    private String name, email, firstName;
    private int userId;
    private Location userLocation;

    private int score = 0;
    private int rank = 0;

    private String level;
    private String gainedAchievements;

    public String getLevel() {
        return level;
    }

    public void setLevel(final String level) {
        this.level = level;
    }

    public String getScore() {
        return String.valueOf(score);
    }

    public int getRank() {
        return rank;
    }

    public String getGainedAchievements() {
        return gainedAchievements;
    }

    public void setGainedAchievements(final String gainedAchievements) {
        this.gainedAchievements = gainedAchievements;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(final int id) {
        for (final Location l : App.getLocations()) {
            if (l.getId() == id) {
                this.userLocation = l;
                break;
            }
        }
    }

    public void setUserLocation(final Location newLocation) {
        this.userLocation = newLocation;
    }

    public int getUserLocationId() {
        if (userLocation == null) {
            return -1;
        } else {
            return userLocation.getId();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public void setLeaderboardData(final JSONObject json) {
        try {
            final JSONObject leaderboard = getLeaderboard(json.getJSONArray("leaderboards"));
            if (leaderboard != null) {
                score = leaderboard.getInt("score");
                rank = leaderboard.getInt("position");
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Error: Cannot get leaderboard");
        }
    }

    private JSONObject getLeaderboard(final JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!jsonArray.isNull(i)) {
                final JSONObject leaderboard = jsonArray.getJSONObject(i);
                if (leaderboard.getInt("id") == Settings.LEADERBOARD_ID) {
                    return leaderboard;
                }
            }
        }
        Log.e(TAG, "Error: No leaderboard with id: " + Settings.LEADERBOARD_ID);
        return null;
    }

    public ResponseItem toLoginResponse(final String roomName, final int roomPages, final int achievementsNumber, final int achievementPages) {
        return new LoginResponse(getName(), score, rank, roomName, roomPages, achievementsNumber, achievementPages);
    }
}