package getresultsapp.sointeractve.pl.getresultsapp.activities;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.connection.HttpResponse;
import pl.sointeractive.isaacloud.exceptions.IsaaCloudConnectionException;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";

    private Button buttonRegister;
    private Context context;
    private ProgressDialog dialog;
    private EditText textEmail, textPassword, textPasswordRepeat,textFirstName, textLastName;

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
                jsonBody.put("email", RegisterActivity.this.textEmail.getEditableText().toString());
                jsonBody.put("password", RegisterActivity.this.textPassword.getEditableText().toString());
                jsonBody.put("firstName", RegisterActivity.this.textFirstName.getEditableText().toString());
                jsonBody.put("lastName", RegisterActivity.this.textLastName.getEditableText().toString());
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