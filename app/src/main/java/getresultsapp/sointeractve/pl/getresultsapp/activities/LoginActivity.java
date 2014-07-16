package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import getresultsapp.sointeractve.pl.getresultsapp.R;
import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;
import getresultsapp.sointeractve.pl.getresultsapp.data.LoginData;
import getresultsapp.sointeractve.pl.getresultsapp.data.UserData;
import pl.sointeractive.isaacloud.Isaacloud;
import pl.sointeractive.isaacloud.exceptions.InvalidConfigException;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private Context context;
    private LoginData logindata;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        // create new wrapper instance for API connection
        initializeConnector();

        // find relevant views and add listeners
        Button buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ButtonAction", "Login clicked");
            }

        });

        Button buttonNewUser = (Button) findViewById(R.id.buttonNewUser);
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




}
