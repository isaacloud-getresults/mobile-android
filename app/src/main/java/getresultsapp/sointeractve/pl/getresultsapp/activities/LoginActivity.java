package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.LoginData;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.Isaacloud;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.InvalidConfigException;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private Context context;
    private TextView editEmail, editPassword;
    private LoginData loginData;
    private UserData userData;
    private ProgressDialog dialog;
    private Button buttonLogIn;
    private Button buttonNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        // create new wrapper instance for API connection
        initializeConnector();

        // find relevant views and add listeners
        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        buttonNewUser = (Button) findViewById(R.id.buttonNewUser);
        editEmail = (TextView) findViewById(R.id.editEmail);
        editPassword = (TextView) findViewById(R.id.editPassword);

        // add listeners
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editEmail.getEditableText().toString().equals("")
                        || editPassword.getEditableText().toString().equals("")) {
                    Toast.makeText(context, R.string.error_empty,
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d("ButtonAction", "Login clicked");
                    userData = App.loadUserData();
                    new LoginTask().execute();
                }
            }

        });

        buttonNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonAction","New user clicked");
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);
            }

        });
    }

    public void initializeConnector() {
        Map<String, String> config = new HashMap<String, String>();
        config.put("instanceId", Settings.instanceId);
        config.put("appSecret", Settings.appSecret);
        try {
            App.setConnector(new Isaacloud(config));
        } catch (InvalidConfigException e) {
            e.printStackTrace();
        }
    }

    private class LoginTask extends AsyncTask<Object, Object, Object> {

        boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
            // lock screen orientation and show progress dialog
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            dialog = ProgressDialog.show(context, "Logging in", "Please wait");
        }

        @Override
        protected Object doInBackground(Object... params) {

            Log.d(TAG, "doInBackground()");
            try {
                String email = LoginActivity.this.editEmail.getEditableText().toString();
                HttpResponse response = App.getConnector().path("/admin/users")
                        .withLimit(1000).get();
                Log.d(TAG, response.toString());
                JSONArray array = response.getJSONArray();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = (JSONObject) array.get(i);
                    if (email.equals(json.get("email"))) {
                        String userFirstName = json.getString("firstName");
                        String userLastName = json.getString("lastName");
                        String userEmail = json.getString("email");
                        int userId = json.getInt("id");
                        // send loaded data to App.UserData
                        UserData userData = new UserData();
                        userData.setName(userFirstName + " " + userLastName);
                        userData.setFirstName(userFirstName);
                        userData.setEmail(userEmail);
                        userData.setUserId(userId);
                        App.saveUserData(userData);
                        // report user found
                        success = true;
                        // break the loop
                        break;
                    }
                }
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
            // dismiss progress dialog
            dialog.dismiss();
            // if the user was found, start new activity, if not, show error
            // message
            if (success) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            } else {
             // DEBUG MODE
             // WHILE ISAA IS OFFLINE
            }
            // unlock screen orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

    }

}
