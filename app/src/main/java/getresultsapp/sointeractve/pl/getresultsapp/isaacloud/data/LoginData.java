package getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data;

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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemembered() {
        return remembered;
    }

    public void setRemembered(boolean remembered) {
        this.remembered = remembered;
    }
}