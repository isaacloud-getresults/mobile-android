package com.sointeractive.getresults.app.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.common.collect.Lists;
import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.DataManager;
import com.sointeractive.getresults.app.data.isaacloud.Achievement;
import com.sointeractive.getresults.app.data.isaacloud.Location;
import com.sointeractive.getresults.app.data.isaacloud.LoginData;
import com.sointeractive.getresults.app.data.isaacloud.Person;
import com.sointeractive.getresults.app.data.isaacloud.UserData;
import com.sointeractive.getresults.app.pebble.cache.LoginCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import pl.sointeractive.isaacloud.Isaacloud;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.InvalidConfigException;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static boolean internetConnection = true;
    private static Context context;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;
    private boolean googleLogin = false;
    private boolean mSignInClicked;
    private LoginData loginData;
    private ConnectionResult mConnectionResult;
    private LinearLayout llProfileLayout;
    private TextView editEmail, editPassword;
    private UserData userData;
    private ProgressDialog dialog;
    private Button btnRevokeAccess;
    private CheckBox checkbox;
    private ActionBar actionBar;
    private boolean Glogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        Thread thread = new Thread(new InternetRunnable());
        //<DEBUG_ONLY>
        // TODO: Restore this to code
        thread.start();
        //</DEBUG_ONLY>
        loginData = App.loadLoginData();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("logout")) {
                Glogin = false;
                loginData.setRemembered(false);
            }
        }
        App.loadConfigData();

        final Map<String, String> config = new HashMap<String, String>();
        config.put("instanceId", Settings.INSTANCE_ID);
        config.put("appSecret", Settings.APP_SECRET);
        try {
            App.setIsaacloudConnector(new Isaacloud(config));
        } catch (final InvalidConfigException e) {
            e.printStackTrace();
        }
        // create new wrapper instance for API connection
        /*
        if(App.loadConfigData() == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setMessage("Welcome to our app\nLet me be your guide")
            .setIcon(R.drawable.ic_launcher);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        */


        // find relevant views and add listeners
        final SignInButton buttonSignIn = (SignInButton) findViewById(R.id.buttonGoogle);
//        btnRevokeAccess = (Button) findViewById(R.id.buttonRevokeAccess);
        final Button buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        final Button buttonNewUser = (Button) findViewById(R.id.buttonNewUser);
        final Button buttonScan = (Button) findViewById(R.id.buttonScan);
        editEmail = (TextView) findViewById(R.id.editEmail);
        editPassword = (TextView) findViewById(R.id.editPassword);
        checkbox = (CheckBox) findViewById(R.id.rememberCheckBox);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        if (loginData.isRemembered()) {
            checkbox.setChecked(true);
            editEmail.setText(loginData.getEmail());
            editPassword.setText(loginData.getPassword());
        }

        // add listeners
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.APP_SECRET != null) {
                    if (internetConnection) {
                        if (checkbox.isChecked()) {
                            loginData.setRemembered(true);
                            loginData.setEmail(editEmail.getEditableText().toString());
                            loginData.setPassword(editPassword.getEditableText().toString());
                            App.saveLoginData(loginData);
                        } else {
                            loginData.setRemembered(false);
                            loginData.setEmail("");
                            loginData.setPassword("");
                            App.saveLoginData(loginData);
                        }
                        if (editEmail.getEditableText().toString().equals("")
                                || editPassword.getEditableText().toString().equals("")) {
                            Toast.makeText(context, R.string.error_empty,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "Action: Try log in user: " + editEmail.getEditableText().toString() + ", with password: " + editPassword.getEditableText().toString());
                            userData = App.loadUserData();
                            new LoginTask().execute();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(getApplicationContext(), "Application is not configured\nTap \"Configure application\" and scan QR code", Toast.LENGTH_SHORT).show();
            }
        });

        buttonNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Settings.APP_SECRET != null) {
                    if (internetConnection) {
                        Intent intent = new Intent(context, RegisterActivity.class);
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Application is not configured\nTap \"Configure application\" and scan QR code", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "ButtonAction: SignIn clicked");
                if (Settings.APP_SECRET != null) {
                    if (internetConnection) {
                        Glogin = true;
                        signInWithGplus();
                    } else
                        Toast.makeText(getApplicationContext(), "No Internet connection", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "Application is not configured\nTap \"Configure application\" and scan QR code", Toast.LENGTH_SHORT).show();
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

        //<DEBUG_ONLY>
        // TODO: Remove this from code
        /*try {
            generateFakeData();
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
                    final DataManager dm = App.getDataManager();
                    final List<Achievement> achievements = dm.getAchievements();
                    final int achievementsNumber = achievements.size();
                    try {
                        final JSONObject achievementJSON = new JSONObject("{id:" + achievementsNumber + ",label:'test achievement " + achievementsNumber + "',description:'test description " + achievementsNumber + "'}");
                        final Achievement achievement = getFakeAchievement(achievementJSON);
                        achievements.add(0, achievement);
                    } catch (final JSONException e) {
                        Log.e(TAG, "Cannot add fake achievement");
                    }
                    dm.setAchievements(achievements);

                    final SparseArray<List<Person>> entries = new SparseArray<List<Person>>();
                    final int peopleNumber = dm.getPeople().size();
                    final int beaconsNumber = dm.getLocations().size();
                    for (int i = 0; i < beaconsNumber; i++) {
                        entries.put(i, new LinkedList<Person>());
                    }
                    for (int i = 0; i < peopleNumber; i++) {
                        final Person p = new Person("Tester", "Test " + i, i % beaconsNumber, i);
                        entries.get(p.getLocation()).add(p);
                    }
                    entries.get(peopleNumber % beaconsNumber).add(new Person("Tester", "Test " + peopleNumber, peopleNumber % beaconsNumber, peopleNumber));
                    App.getDataManager().setPeople(entries);
                }
            });
        } catch (final JSONException e) {
            Log.e(TAG, "Cannot create fake data");
        }*/
    }

    private final List<String> urls = new LinkedList<String>(Arrays.asList(
            "http://chart.apis.google.com/chart?chs=200x200&cht=qr&chld=%7C1&chl=http%3A%2F%2Fxyz.getresults.isaacloud.com%2F"
    ));

    public Achievement getFakeAchievement(final JSONObject achievementJSON) throws JSONException {
        final Achievement achievement = new Achievement(achievementJSON, true, 1);
        final String imageUrl = urls.get(0);
        urls.remove(0);
        achievement.setImageUrl(imageUrl);
        urls.add(imageUrl);
        return achievement;
    }

    private void generateFakeData() throws JSONException {
        App.getPebbleConnector().closePebbleApp();

        final int BEACONS = 16;
        final int AVG_PEOPLE = 0;
        final int ACHIEVEMENTS = 0;

        // User
        final UserData userData = new UserData();
        userData.setName("Tester Test");
        userData.setFirstName("Tester");
        userData.setEmail("tester@testing.test");
        userData.setUserId(1);
        final JSONObject userJSON = new JSONObject("{leaderboards: [{id: 1, score: 128, position: 8}]}");
        userData.setLeaderboardData(userJSON);
        userData.setLevel("5");
        App.saveUserData(userData);

        // Beacons
        final SparseArray<List<Person>> entries = new SparseArray<List<Person>>();
        final List<Location> locations = new ArrayList<Location>();
        for (int i = 0; i < BEACONS; i++) {
            final JSONObject locJson = new JSONObject("{id:" + i + ",label:'test room " + i + "'}");
            final Location loc = new Location(locJson);
            entries.put(loc.getId(), new LinkedList<Person>());
            locations.add(loc);
            if (loc.getId() == 0) {
                userData.setUserLocation(loc);
                App.saveUserData(userData);
            }
        }
        final DataManager dm = App.getDataManager();
        dm.setLocations(locations);
        dm.setPeople(entries);

        // Achievements
        final List<Achievement> achievements = new ArrayList<Achievement>();
        for (int i = 0; i < ACHIEVEMENTS; i++) {
            final JSONObject achievementJSON = new JSONObject("{id:" + i + ",label:'test achievement " + i + "',description:'test description " + i + "'}");
            achievements.add(0, getFakeAchievement(achievementJSON));
        }
        dm.setAchievements(achievements);

        // People
        for (int i = 0; i < BEACONS * AVG_PEOPLE; i++) {
            final Person p = new Person("Tester", "Test " + i, i % BEACONS, i);
            entries.get(p.getLocation()).add(p);
        }
        App.getDataManager().setPeople(entries);

        // Log in
        App.getPebbleConnector().clearSendingQueue();
        LoginCache.INSTANCE.logIn();
    }

    //</DEBUG_ONLY>

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        editEmail = (TextView) findViewById(R.id.editEmail);
        editPassword = (TextView) findViewById(R.id.editPassword);
        checkbox = (CheckBox) findViewById(R.id.rememberCheckBox);
        if (loginData.isRemembered()) {
            checkbox.setChecked(true);
            editEmail.setText(loginData.getEmail());
            editPassword.setText(loginData.getPassword());
        }
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
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
        Log.d(TAG, "ButtonAction: ResolveSignInError()");
        if (mConnectionResult != null) {
            Log.d(TAG, "ButtonAction: " + mConnectionResult.toString());
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
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            Log.d(TAG, "Button action: signInWithGplus()");
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
                String contents = data.getStringExtra("SCAN_RESULT") + "config.json";
                JSONObject json = new JSONObject();
                try {
                    json = new GetJSON().execute(contents).get();
                } catch(ExecutionException e) {
                    e.printStackTrace();
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }

                if (json != null) {
                    String conf = null;
                    try {
                        conf = json.getString("clientid") + " " + json.getString("androidsecret") + " " +
                        json.getString("uuid") + " " + json.getInt("pebbleNotification") + " " +
                        json.getInt("mobileNotification") + " " + json.getInt("counter") + " " + json.getString("websocket");
                        App.saveConfigData(conf);
                        Toast.makeText(getApplicationContext(), "Application is configured", Toast.LENGTH_SHORT).show();
                    } catch(JSONException e) {
                        Toast.makeText(this, "Something changed in JSON", Toast.LENGTH_SHORT);
                    }
                    Log.d("Settings: ", "conf = " + conf);
                } else
                    Toast.makeText(getApplicationContext(), "Inappropriate QR code\n" + contents, Toast.LENGTH_SHORT).show();
            }
            if (resultCode == RESULT_CANCELED) {
                // TODO: Handle cancel
            }
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
            if (internetConnection) {
                if (Glogin && googleLogin) new LoginTask().execute();
                else revokeGplusAccess();
            } else Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();

    }

    void runMainActivity() {
        // RUN MAIN ACTIVITY
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void onBackPressed() {
        finish();
    }

    // LOGIN
    private class LoginTask extends AsyncTask<Object, Object, Object> {

        boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Action: logging in");
            // lock screen orientation and show progress dialog
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            dialog = ProgressDialog.show(context, "Logging in", "Please wait");
        }

        @Override
        protected Object doInBackground(Object... params) {

            Log.d(TAG, "Action: Login user in background");

            String email;
            boolean register = true;
            if (googleLogin && Glogin) email = Plus.AccountApi.getAccountName(mGoogleApiClient);
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
                    Log.v(TAG, response.toString());
                    final JSONObject userJSON = response.getJSONObject();
                    String userFirstName = userJSON.getString("firstName");
                    String userLastName = userJSON.getString("lastName");
                    String userEmail = userJSON.getString("email");
                    String level = userJSON.getString("level");
                    int userId = userJSON.getInt("id");
                    // send loaded data to App.UserData
                    UserData userData = new UserData();
                    userData.setName(userFirstName + " " + userLastName);
                    userData.setFirstName(userFirstName);
                    userData.setEmail(userEmail);
                    userData.setUserId(userId);
                    userData.setLeaderboardData(userJSON);
                    userData.setLevel(level);
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
                    userData.setLevel(Json.getString("level"));
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
            dialog.dismiss();
            if (success) {
                Log.d(TAG, "Login success");
                new EventGetLocations().execute();
            } else {
                Log.e(TAG, "Cannot log in");
            }

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
            try {
                // LOCATIONS REQUEST
                HttpResponse response = App.getIsaacloudConnector().path("/cache/users/groups").withFields("label", "id").get();
                Log.v(TAG, response.toString());
                // all locations from isa
                JSONArray locationsArray = response.getJSONArray();
                for (int i = 0; i < locationsArray.length(); i++) {
                    JSONObject locJson = (JSONObject) locationsArray.get(i);
                    Location loc = new Location(locJson);
                    entries.put(loc.getId(), new LinkedList<Person>());
                    locations.add(loc);
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
            if (success) {
                Log.d(TAG, "Locations downloaded");
                new EventGetAchievements().execute();
            } else {
                Log.e(TAG, "Cannot get locations");
            }
        }

    }

    // GET LOCATIONS
    private class EventGetAchievements extends AsyncTask<Object, Object, List<Achievement>> {

        private final String TAG = EventGetAchievements.class.getSimpleName();
        public boolean success = false;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Action: Downloading achievements");
        }


        @Override
        public List<Achievement> doInBackground(Object... params) {
            List<Achievement> achievements = new ArrayList<Achievement>();
            try {

                // ACHIEVEMENTS REQUEST
                SparseIntArray idMap = new SparseIntArray();
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
                Log.v("TEST", arrayGeneral.toString(3));
                for (int i = 0; i < arrayGeneral.length(); i++) {
                    JSONObject json = (JSONObject) arrayGeneral.get(i);
                    if (idMap.get(json.getInt("id"), -1) != -1) {
                        achievements.add(new Achievement(json, true, idMap.get(json.getInt("id"))));
                    }
                }
                success = true;
                return achievements;
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
        protected void onPostExecute(List<Achievement> result) {
            if (success) {
                final DataManager dm = App.getDataManager();
                dm.setAchievements(result);
                Log.d(TAG, "Event: Downloaded " + dm.getAchievements().size() + " achievements");
                LoginCache.INSTANCE.logIn();
                dialog.dismiss();
                runMainActivity();
                // SEND LOGIN EVENT
                App.getEventManager().postEventLogin();
            } else {
                dialog.dismiss();
                Log.e(TAG, "Cannot get achievements");
            }
        }

    }

    private class GetJSON extends AsyncTask<String, Object, JSONObject> {

        @Override
        public JSONObject doInBackground(String... params) {
            Log.d(TAG, "URL: " + params[0]);
            JSONObject json;
            try {
                json = readJsonFromUrl(params[0]);
            } catch (JSONException e) {
                json = null;
            } catch (IOException e) {
                json = null;
            }
            if (json != null) Log.d(TAG, "JSON: " + json.toString());
            else Log.d(TAG, "JSON: null");
            return json;
        }

    }

    private class InternetRunnable implements Runnable {
        public void run() {
            while (context != null) {
                internetConnection = hasActiveInternetConnection();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Event: Check internet thread interrupted");
                    return;
                }
//                Log.d(TAG, "Connected: " + internetConnection);
            }
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}


