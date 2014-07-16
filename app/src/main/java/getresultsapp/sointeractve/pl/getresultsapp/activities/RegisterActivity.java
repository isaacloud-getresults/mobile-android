package getresultsapp.sointeractve.pl.getresultsapp.activities;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    // TEST NEW USER
    private Button buttonNewTestUser;
    private Context context;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // generate basic view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;

        buttonNewTestUser = (Button) findViewById(R.id.buttonNewTestUser);
        // set button listener
        setButtonListeners();
    }


    private void setButtonListeners() {
        buttonNewTestUser.setOnClickListener(new OnClickListener() {
            // DEBUG MODE, CREATE NEW TEST USER
            @Override
            public void onClick(View v) {
                new RegisterTask().execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * This AsyncTask is used to communicate with the API. It posts a request to
     * create a new user and checks for errors. After a successful registration
     * a new UserActivity is started.
     *
     * @author Mateusz Renes
     *
     */
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
            Log.d(TAG, "doInBackground()");
            JSONObject jsonBody = new JSONObject();
            // generate json
            try {
                jsonBody.put("email","getresultsdev@gmail.com");
                jsonBody.put("password", "123");
                jsonBody.put("firstName", "Janusz");
                jsonBody.put("lastName", "Tester");
                jsonBody.put("status", 1);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            UserData userData = App.loadUserData();
            HttpResponse response;
            // send request and retrieve response
            try {
                response = App.getConnector().path("/admin/users")
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
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            } else {
               // error login activity here;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            finish();
        }

    }

}