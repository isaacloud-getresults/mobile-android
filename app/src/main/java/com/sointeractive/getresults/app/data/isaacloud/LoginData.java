package com.sointeractive.getresults.app.data.isaacloud;

import java.io.Serializable;


public class LoginData implements Serializable {

    private boolean remembered;
    private String email;
    private String password;

    public LoginData() {
        remembered = false;
        email = "";
        password = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isRemembered() {
        return remembered;
    }

    public void setRemembered(final boolean remembered) {
        this.remembered = remembered;
    }
}