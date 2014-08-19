package com.sointeractive.getresults.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.DataManager;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.data.isaacloud.Person;
import com.sointeractive.getresults.app.data.isaacloud.UserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;


public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button buttonRegister;
    private Context context;
    private ProgressDialog dialog;
    private EditText textEmail, textPassword, textPasswordRepeat, textFirstName, textLastName;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // generate basic view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;

        // find views
        // text edits
        textEmail = (EditText) findViewById(R.id.editRegisterEmail);
        textPassword = (EditText) findViewById(R.id.editRegisterPassword);
        textPasswordRepeat = (EditText) findViewById(R.id.editRegisterRePassword);
        textFirstName = (EditText) findViewById(R.id.editRegisterFirstName);
        textLastName = (EditText) findViewById(R.id.editRegisterLastName);
        //buttons
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        // set button listeners
        setButtonListeners();
    }


    private void setButtonListeners() {

        buttonRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = textPassword.getEditableText().toString();
                String pw2 = textPasswordRepeat.getEditableText().toString();
                String email = textEmail.getEditableText().toString();
                String firstName = textFirstName.getEditableText().toString();
                String lastName = textLastName.getEditableText().toString();
                if (email.length() > 0 && firstName.length() > 0
                        && lastName.length() > 0) {
                    if (pw.equals(pw2)) {
                        if (pw.matches("^((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%\\.]).{6,15})$")) {
                            new RegisterTask().execute();
                        } else {
                            Toast.makeText(context, R.string.error_password,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        resetPasswordFields();
                        Toast.makeText(
                                context,
                                R.string.activity_register_passwords_dont_match,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    resetPasswordFields();
                    Toast.makeText(context,
                            R.string.activity_register_empty_fields,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void resetPasswordFields() {
        textPassword.getEditableText().clear();
        textPasswordRepeat.getEditableText().clear();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class RegisterTask extends AsyncTask<Object, Object, Object> {

        boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
            // lock screen orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            // show progress dialog
            dialog = ProgressDialog.show(context, "Registering account",
                    "Please wait");
        }

        @Override
        protected Object doInBackground(Object... params) {
            Log.d(TAG, "Action: Registering user");

            JSONObject jsonBody = new JSONObject();
            // generate json
            try {
                jsonBody.put("email", RegisterActivity.this.textEmail.getEditableText().toString());
                jsonBody.put("password", RegisterActivity.this.textPassword.getEditableText().toString());
                jsonBody.put("firstName", RegisterActivity.this.textFirstName.getEditableText().toString());
                jsonBody.put("lastName", RegisterActivity.this.textLastName.getEditableText().toString());
                jsonBody.put("status", 1);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            userData = App.loadUserData();
            HttpResponse response;
            // send request and retrieve response
            try {
                response = App.getIsaacloudConnector().path("/admin/users")
                        .post(jsonBody);
                JSONObject json = response.getJSONObject();
                userData.setUserId(json.getInt("id"));
                userData.setName(json.getString("firstName") + " "
                        + json.getString("lastName"));
                userData.setEmail(json.getString("email"));
                App.saveUserData(userData);
                success = true;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Log.d(TAG, "onPostExecute()");
            dialog.dismiss();
            if (success) {
                new EventGetLocations().execute();
            } else {
                // TODO: error login activity here;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            finish();
        }

    }

    // GET LOCATIONS
    private class EventGetLocations extends AsyncTask<Object, Object, Object> {

        private final String TAG = EventGetLocations.class.getSimpleName();
        public boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Action: Downloading locations");
            dialog = ProgressDialog.show(context, "Downloading data", "Please wait");
        }


        @Override
        public Object doInBackground(Object... params) {
            SparseArray<List<Person>> entries = new SparseArray<List<Person>>();
            List<Location> locations = new ArrayList<Location>();
            List<Achievement> achievements = new ArrayList<Achievement>();
            locations.add(new Location("Nowhere", 0));
            userData = App.loadUserData();
            try {
                // LOCATIONS REQUEST
                Log.d(TAG, "Action: Getting locations");
                HttpResponse response = App.getIsaacloudConnector().path("/cache/users/groups").withFields("label", "id").get();
                Log.v(TAG, response.toString());
                // all locations from isa
                JSONArray locationsArray = response.getJSONArray();
                for (int i = 0; i < locationsArray.length(); i++) {
                    JSONObject locJson = (JSONObject) locationsArray.get(i);
                    Location loc = new Location(locJson);
                    if (loc.getId() != 1 && loc.getId() != 2) {
                        entries.put(loc.getId(), new LinkedList<Person>());
                        locations.add(loc);
                    }
                }


                // USERS REQUEST
                Log.d(TAG, "Action: Getting users list");
                HttpResponse usersResponse = App.getIsaacloudConnector().path("/cache/users").withFields("firstName", "lastName", "id", "counterValues").withLimit(0).get();
                Log.v(TAG, usersResponse.toString());
                JSONArray usersArray = usersResponse.getJSONArray();
                // for every user
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = (JSONObject) usersArray.get(i);
                    Person p = new Person(userJson);
                    entries.get(p.getLocation()).add(p);
                }

                // ACHIEVEMENTS REQUEST
                SparseIntArray idMap = new SparseIntArray();
                HttpResponse responseUser = App
                        .getIsaacloudConnector()
                        .path("/cache/users/" + userData.getUserId()).withFields("gainedAchievements").withLimit(0).get();
                JSONObject achievementsJson = responseUser.getJSONObject();
                JSONArray arrayUser = achievementsJson.getJSONArray("gainedAchievements");
                for (int i = 0; i < arrayUser.length(); i++) {
                    JSONObject json = (JSONObject) arrayUser.get(i);
                    idMap.put(json.getInt("achievement"), json.getInt("amount"));
                }

                HttpResponse responseGeneral = App.getIsaacloudConnector()
                        .path("/cache/achievements").withLimit(1000).get();
                JSONArray arrayGeneral = responseGeneral.getJSONArray();
                Log.v("TEST", arrayGeneral.toString(3));
                for (int i = 0; i < arrayGeneral.length(); i++) {
                    JSONObject json = (JSONObject) arrayGeneral.get(i);
                    if (idMap.get(json.getInt("id"), -1) != -1) {
                        achievements.add(new Achievement(json, true, idMap.get(json.getInt("id"))));
                    }
                }
                success = true;
                DataManager dm = App.getDataManager();
                dm.setLocations(locations);
                dm.setPeople(entries);
                dm.setAchievements(achievements);
                Log.d(TAG, "Number achievements found: " + dm.getAchievements().size());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            dialog.dismiss();
            if (success) {
                Log.d(TAG, "New user registered");
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            } else {
                Log.e(TAG, "Cannot register user");
            }
        }

    }

}