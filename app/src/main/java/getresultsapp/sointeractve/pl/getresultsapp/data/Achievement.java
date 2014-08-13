package getresultsapp.sointeractve.pl.getresultsapp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data store class for Achievements.
 *
 * @author Mateusz Renes
 */
public class Achievement {

    private String label, description, imageUrl;
    private int counter = 0;
    private boolean isGained;

    public Achievement(String label, String desc, boolean isGained) {
        this.setLabel(label);
        this.setDesc(desc);
        this.setGained(isGained);
    }

    public Achievement(JSONObject json, boolean isGained) throws JSONException {
        this.setLabel(json.getString("label"));
        this.setDesc(json.getString("description"));
        this.setGained(isGained);
    }

    public Achievement(JSONObject json, boolean isGained, int amount) throws JSONException {
        this.setLabel(json.getString("label"));
        this.setDesc(json.getString("description"));
        this.setGained(isGained);
        this.setCounter(amount);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isGained() {
        return isGained;
    }

    public void setGained(boolean isGained) {
        this.isGained = isGained;
    }

    public String print() {
        return "Achievement: " + label + " " + description + " " + isGained;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}