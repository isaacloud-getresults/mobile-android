package getresultsapp.sointeractve.pl.getresultsapp.data;

import java.io.Serializable;



public class UserData implements Serializable {


    private String name, email;
    private boolean hasNewNotifications;
    private int userId;
    private Notification lastNotification;



    public UserData() {
        // set default user data
        name = "user name";
        email = "user email";
        setHasNewNotifications(false);
    }

    public boolean hasNewNotifications() {
        return hasNewNotifications;
    }

    public void setHasNewNotifications(boolean hasNewNotifications) {
        this.hasNewNotifications = hasNewNotifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Notification getLastNotification() {
        return lastNotification;
    }

    public void setLastNotification(Notification lastNotification) {
        this.lastNotification = lastNotification;
    }



}