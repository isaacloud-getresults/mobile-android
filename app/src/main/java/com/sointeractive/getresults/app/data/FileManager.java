package com.sointeractive.getresults.app.data;

import android.content.Context;
import android.util.Log;

import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.isaacloud.LoginData;
import com.sointeractive.getresults.app.data.isaacloud.UserData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.StringTokenizer;

/**
 * This is a helper class for saving and loading files from the application
 * folder.
 *
 * @author Mateusz Renes
 */
class FileManager {

    private static final String userDataFileName = "user_data.dat";
    private static final String loginDataFileName = "login_data.dat";
    private static final String configDataFileName = "config_data.dat";
    private static final String TAG = FileManager.class.getSimpleName();

    public UserData loadUserData(App app) {
        UserData data = null;
        //Check if the file exists. If not, create a new one.
        File checkFile = new File(app.getFilesDir(), userDataFileName);
        if (!checkFile.exists()) {
            saveUserData(new UserData(), app);
        }
        //load the file
        try {
            FileInputStream fis = app.openFileInput(userDataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (UserData) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found");
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void saveUserData(UserData data, App app) {
        //save the file
        try {
            FileOutputStream fos = app.openFileOutput(userDataFileName,
                    Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LoginData loadLoginData(App app) {
        LoginData data = null;
        //Check if the file exists. If not, create a new one.
        File checkFile = new File(app.getFilesDir(), loginDataFileName);
        if (!checkFile.exists()) {
            saveLoginData(new LoginData(), app);
        }
        //load the file
        try {
            FileInputStream fis = app.openFileInput(loginDataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (LoginData) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void saveLoginData(LoginData data, App app) {
        //save the file
        try {
            FileOutputStream fos = app.openFileOutput(loginDataFileName,
                    Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadConfigData(App app) {
        String data = null;
        //Check if the file exists. If not, create a new one.
        File checkFile = new File(app.getFilesDir(), configDataFileName);
        if (!checkFile.exists()) {
            saveConfigData("", app);
            Log.d("Settings: ", "File does not exist");
        }
        //load the file
        try {
            FileInputStream fis = app.openFileInput(configDataFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (String) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        saveConfigData(data, app);
        Log.d("Settings: ", "data: " + data);
        return data;
    }

    public void saveConfigData(String data, App app) {
        //save the file
        try {
            Log.d("Settings: ", "Data saved in the file");
            FileOutputStream fos = app.openFileOutput(configDataFileName,
                    Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!data.equals("")) {
            StringTokenizer tokenizer = new StringTokenizer(data);
            Settings.INSTANCE_ID = (String) tokenizer.nextElement();
            Log.d("Settings: ", Settings.INSTANCE_ID);
            Settings.APP_SECRET = (String) tokenizer.nextElement();
            Log.d("Settings: ", Settings.APP_SECRET);

// DEBUG ONLY
//            Settings.INSTANCE_ID = "280";
//            Settings.APP_SECRET = "dafdb94c83cf267bd83b5fa145b28d1c";

            Settings.BEACON_PROXIMITY_UUID = (String) tokenizer.nextElement();
            Log.d("Settings: ", Settings.BEACON_PROXIMITY_UUID);
            Settings.PEBBLE_NOTIFICATION_ID = Integer.valueOf((String) tokenizer.nextElement());
            Settings.ANDROID_NOTIFICATION_ID = Integer.valueOf((String) tokenizer.nextElement());
            Settings.LOCATION_COUNTER = (String) tokenizer.nextElement();
            Settings.SERVER_ADDRESS = (String) tokenizer.nextElement();
        }
    }

    public void resetUserData(App app) {
        saveUserData(new UserData(), app);
    }
}