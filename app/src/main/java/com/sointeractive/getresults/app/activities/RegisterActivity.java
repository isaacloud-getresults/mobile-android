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
import com.sointeractive.getresults.app.data.isaacloud.LoginData;
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
    private LoginData loginData;

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

            HttpResponse response;
            // send request and retrieve response
            try {
                response = App.getIsaacloudConnector().path("/admin/users")
                        .post(jsonBody);
                JSONObject json = response.getJSONObject();
                Log.d(TAG, "Register json:" + json.toString());
                success = true;
            } catch (IsaaCloudConnectionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            loginData = App.loadLoginData();
            dialog.dismiss();
            if (success) {
                    loginData.setEmail(textEmail.getEditableText().toString());
                    loginData.setPassword(textPassword.getEditableText().toString());
                    loginData.setRemembered(true);
                    App.saveLoginData(loginData);
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
            } else {
                Log.e(TAG, "Error: Cannot register user");
            }
            finish();
        }

    }


}