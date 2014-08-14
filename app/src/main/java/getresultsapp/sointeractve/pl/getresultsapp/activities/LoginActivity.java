package getresultsapp.sointeractve.pl.getresultsapp.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.config.IsaaCloudSettings;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.DataManager;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Achievement;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Location;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.Person;
import getresultsapp.sointeractve.pl.getresultsapp.isaacloud.data.UserData;
import getresultsapp.sointeractve.pl.getresultsapp.pebble.cache.LoginCache;
import pl.sointeractive.isaacloud.Isaacloud;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.InvalidConfigException;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    private static final String TAG = "LoginActivity";
    static boolean internetConnection = true;
    Thread thread;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean googleLogin = false;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private LinearLayout llProfileLayout;
    private Context context;
    private TextView editEmail, editPassword;
    private UserData userData;
    private ProgressDialog dialog;
    private Button buttonLogIn;
    private Button buttonNewUser;
    private SignInButton buttonSignIn;
    private Button buttonScan;
    private Button btnRevokeAccess;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        thread = new Thread(new InternetRunnable());
        thread.start();
        configureApplication();

        // create new wrapper instance for API connection
        initializeConnector();

        // find relevant views and add listeners
        buttonSignIn = (SignInButton) findViewById(R.id.buttonGoogle);
//        btnRevokeAccess = (Button) findViewById(R.id.buttonRevokeAccess);
        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        buttonNewUser = (Button) findViewById(R.id.buttonNewUser);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        editEmail = (TextView) findViewById(R.id.editEmail);
        editPassword = (TextView) findViewById(R.id.editPassword);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        // add listeners
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetConnection) {
                    if (editEmail.getEditableText().toString().equals("")
                            || editPassword.getEditableText().toString().equals("")) {
                        Toast.makeText(context, R.string.error_empty,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(editEmail.getEditableText().toString(), editPassword.getEditableText().toString());
                        userData = App.loadUserData();
                        new LoginTask().execute();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
            }

        });

        buttonNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonAction", "New user clicked");
                if (internetConnection) {
                    Intent intent = new Intent(context, RegisterActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
            }

        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (internetConnection)
                    signInWithGplus();
                else
                    Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        buttonScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 101);

                } catch (Exception e) {
                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
            }
        });
/*
        btnRevokeAccess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                revokeGplusAccess();
            }
        });
*/
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    // Handler for the result from QR code scanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                if (contents.contains("/?*#$!%@/")) {
                    // String for QR code: 179/?*#$!%@/3f14569b750b69a8bc352cb34ad3e
                    StringTokenizer tokenizer = new StringTokenizer((contents), "/?*#$!%@/");
                    String conf = (String) (tokenizer.nextElement() + "/" + tokenizer.nextElement());
                    App.saveConfigData(conf);

                    Toast.makeText(getApplicationContext(), "Application is configured\n" + conf, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Inappropriate QR code", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
            configureApplication();
            initializeConnector();
            Log.d(TAG, "After configureApplication() " + IsaaCloudSettings.INSTANCE_ID + " / " + IsaaCloudSettings.APP_SECRET);
        }

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

    }

    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        googleLogin = true;
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            com.google.android.gms.plus.model.people.Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String info = currentPerson.getName().getGivenName() + " " + currentPerson.getName().getFamilyName() + "\n" + Plus.AccountApi.getAccountName(mGoogleApiClient);
            Toast.makeText(this, info, Toast.LENGTH_LONG).show();
            if (internetConnection) new LoginTask().execute();
            else Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Current person is null", Toast.LENGTH_LONG).show();


    }

    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Toast.makeText(getApplicationContext(), "Access revoked!", Toast.LENGTH_SHORT).show();
                            mGoogleApiClient.connect();

                        }

                    });
        }
    }

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();

    }

    public void initializeConnector() {
        Map<String, String> config = new HashMap<String, String>();
        config.put("instanceId", IsaaCloudSettings.INSTANCE_ID);
        config.put("appSecret", IsaaCloudSettings.APP_SECRET);
        try {
            App.setIsaacloudConnector(new Isaacloud(config));
        } catch (InvalidConfigException e) {
            e.printStackTrace();
        }
    }

    public void configureApplication() {
        String s = App.loadConfigData();
        StringTokenizer tok = new StringTokenizer((s), "/");
        while (tok.hasMoreElements()) {
            IsaaCloudSettings.INSTANCE_ID = (String) tok.nextElement();
            IsaaCloudSettings.APP_SECRET = (String) tok.nextElement();
        }
    }

    public void runMainActivity() {
        // RUN MAIN ACTIVITY
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean hasActiveInternetConnection() {
        return isNetworkAvailable();
        /*if(isNetworkAvailable()) {
            try {
                Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
                int returnVal = p1.waitFor();
                return (returnVal == 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;*/
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    // LOGIN
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

            String email;
            boolean register = true;
            if (googleLogin) email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            else email = LoginActivity.this.editEmail.getEditableText().toString();
            final Map<String, Object> query = new HashMap<String, Object>();
            query.put("email", email);

            int id = -1;
            try {
                HttpResponse idResponse = App.getIsaacloudConnector().path("/cache/users").withFields("id").withQuery(query).get();
                id = idResponse.getJSONArray().getJSONObject(0).getInt("id");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (id > 0) {
                    HttpResponse response = App.getIsaacloudConnector().path("/cache/users/" + id)
                            .withFields("id", "firstName", "lastName", "level", "email", "counterValues", "leaderboards").get();
                    Log.d(TAG, response.toString());
                    final JSONObject userJSON = response.getJSONObject();
                    String userFirstName = userJSON.getString("firstName");
                    String userLastName = userJSON.getString("lastName");
                    String userEmail = userJSON.getString("email");
                    int userId = userJSON.getInt("id");
                    // send loaded data to App.UserData
                    UserData userData = new UserData();
                    userData.setName(userFirstName + " " + userLastName);
                    userData.setFirstName(userFirstName);
                    userData.setEmail(userEmail);
                    userData.setUserId(userId);
                    userData.setLeaderboardData(userJSON);
                    App.saveUserData(userData);
                    // report user found
                    success = true;
                    register = false;
                }

                if (googleLogin && register) {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
                    jsonBody.put("password", "Google@1998");
                    jsonBody.put("firstName", Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName());
                    jsonBody.put("lastName", Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getFamilyName());
                    jsonBody.put("status", 1);

                    UserData userData = App.loadUserData();
                    HttpResponse registerResponse = App.getIsaacloudConnector().path("/admin/users")
                            .post(jsonBody);
                    JSONObject Json = registerResponse.getJSONObject();
                    userData.setUserId(Json.getInt("id"));
                    userData.setName(Json.getString("firstName") + " "
                            + Json.getString("lastName"));
                    userData.setEmail(Json.getString("email"));
                    App.saveUserData(userData);
                    success = true;
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
            dialog.dismiss();
            if (success) {
                new EventGetLocations().execute();
            } else {
                Log.d(TAG, "Here!");
            }

        }

    }

    // GET LOCATIONS
    private class EventGetLocations extends AsyncTask<Object, Object, Object> {

        private static final String TAG = "EventGetLocations";
        public boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
            dialog = ProgressDialog.show(context, "Downloading data", "Please wait");
        }


        @Override
        public Object doInBackground(Object... params) {
            SparseArray<List<Person>> entries = new SparseArray<List<Person>>();
            List<Location> locations = new ArrayList<Location>();
            try {
                // LOCATIONS REQUEST
                HttpResponse response = App.getIsaacloudConnector().path("/cache/users/groups").withFields("label", "id").get();
                Log.d(TAG, response.toString());
                // all locations from isa
                JSONArray locationsArray = response.getJSONArray();
                for (int i = 0; i < locationsArray.length(); i++) {
                    JSONObject locJson = (JSONObject) locationsArray.get(i);
                    Location loc = new Location(locJson);
                    if (loc.getId() != 1 && loc.getId() != 2) {
                        entries.put(loc.getId(), new LinkedList<Person>());
                        locations.add(loc);
                    }
                    if (loc.getId() == Integer.parseInt(Settings.nullRoomCounter)) {
                        UserData userData = App.loadUserData();
                        userData.setUserLocation(loc);
                        App.saveUserData(userData);
                    }
                }
                success = true;
                DataManager dm = App.getDataManager();
                dm.setLocations(locations);
                dm.setPeople(entries);

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
            Log.d(TAG, "onPostExecute()");
            if (success) {
                new EventGetAchievements().execute();
            } else {
                Log.d(TAG, "NOT SUCCES");
            }
        }

    }

    // GET LOCATIONS
    private class EventGetAchievements extends AsyncTask<Object, Object, Object> {

        private static final String TAG = "EventGetLocations";
        public boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
        }


        @Override
        public Object doInBackground(Object... params) {
            List<Achievement> achievements = new ArrayList<Achievement>();
            try {

                // ACHIEVEMENTS REQUEST
                HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
//                Log.d(TAG, "ACTUAL USER ID IS from object: " + userData.getUserId());
                HttpResponse responseUser = App
                        .getIsaacloudConnector()
                        .path("/cache/users/" + App.loadUserData().getUserId()).withFields("gainedAchievements").withLimit(0).get();
                JSONObject achievementsJson = responseUser.getJSONObject();
                JSONArray arrayUser = achievementsJson.getJSONArray("gainedAchievements");
                for (int i = 0; i < arrayUser.length(); i++) {
                    JSONObject json = (JSONObject) arrayUser.get(i);
                    idMap.put(json.getInt("achievement"), json.getInt("amount"));
                }

                HttpResponse responseGeneral = App.getIsaacloudConnector()
                        .path("/cache/achievements").withLimit(1000).get();
                JSONArray arrayGeneral = responseGeneral.getJSONArray();
                Log.d("TEST", arrayGeneral.toString(3));
                for (int i = 0; i < arrayGeneral.length(); i++) {
                    JSONObject json = (JSONObject) arrayGeneral.get(i);
                    if (idMap.containsKey(json.getInt("id"))) {
                        achievements.add(new Achievement(json, true, idMap.get(json.getInt("id"))));
                    }
                }
                success = true;
                DataManager dm = App.getDataManager();
                dm.setAchievements(achievements);
                Log.d(TAG, "ACHIEVEMENTS LIST SIZE: " + dm.getAchievements().size());

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
            Log.d(TAG, "onPostExecute()");
            dialog.dismiss();
            if (success) {
                Log.d(TAG, "SUCCES");
                LoginCache.INSTANCE.logIn();
                runMainActivity();
            } else {
                Log.d(TAG, "NOT SUCCES");
            }
        }

    }

    public class InternetRunnable implements Runnable {
        public void run() {
            while (true) {
                internetConnection = hasActiveInternetConnection();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                Log.d(TAG, "Connected: " + internetConnection);
            }
        }
    }
}

