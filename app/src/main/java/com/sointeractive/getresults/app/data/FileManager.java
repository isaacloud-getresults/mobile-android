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

    public UserData loadUserData(final App app) {
        UserData data = null;
        //Check if the file exists. If not, create a new one.
        final File checkFile = new File(app.getFilesDir(), userDataFileName);
        if (!checkFile.exists()) {
            saveUserData(new UserData(), app);
        }
        //load the file
        try {
            final FileInputStream fis = app.openFileInput(userDataFileName);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            data = (UserData) ois.readObject();
            ois.close();
            fis.close();
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "File not found");
        } catch (final StreamCorruptedException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void saveUserData(final UserData data, final App app) {
        //save the file
        try {
            final FileOutputStream fos = app.openFileOutput(userDataFileName,
                    Context.MODE_PRIVATE);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public LoginData loadLoginData(final App app) {
        LoginData data = null;
        //Check if the file exists. If not, create a new one.
        final File checkFile = new File(app.getFilesDir(), loginDataFileName);
        if (!checkFile.exists()) {
            saveLoginData(new LoginData(), app);
        }
        //load the file
        try {
            final FileInputStream fis = app.openFileInput(loginDataFileName);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            data = (LoginData) ois.readObject();
            ois.close();
            fis.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final StreamCorruptedException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void saveLoginData(final LoginData data, final App app) {
        //save the file
        try {
            final FileOutputStream fos = app.openFileOutput(loginDataFileName,
                    Context.MODE_PRIVATE);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public String loadConfigData(final App app) {
        String data = null;
        //Check if the file exists. If not, create a new one.
        final File checkFile = new File(app.getFilesDir(), configDataFileName);
        if (!checkFile.exists()) {
            saveConfigData("", app);
            Log.d("Settings: ", "File does not exist");
        }
        //load the file
        try {
            final FileInputStream fis = app.openFileInput(configDataFileName);
            final ObjectInputStream ois = new ObjectInputStream(fis);
            data = (String) ois.readObject();
            ois.close();
            fis.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final StreamCorruptedException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        }
        saveConfigData(data, app);
        Log.d("Settings: ", "data: " + data);
        return data;
    }

    public void saveConfigData(final String data, final App app) {
        //save the file
        try {
            Log.d("Settings: ", "Data saved in the file");
            final FileOutputStream fos = app.openFileOutput(configDataFileName,
                    Context.MODE_PRIVATE);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        if (!data.equals("")) {
            final StringTokenizer tokenizer = new StringTokenizer(data);
            Settings.INSTANCE_ID = (String) tokenizer.nextElement();
            Log.d("Settings: ", Settings.INSTANCE_ID);
            Settings.APP_SECRET = (String) tokenizer.nextElement();
            Log.d("Settings: ", Settings.APP_SECRET);

            //<DEBUG_ONLY>
            // TODO: Remove this from code
            //Settings.INSTANCE_ID = "280";
            //Settings.APP_SECRET = "7359227c3698cb3f135a64776788462";
            //Settings.APP_SECRET = "a531194288d1f06d9b6838ec4fd980f8";
            //Settings.APP_SECRET = "1e5a52c8209f59b5e2a55f23c9aff156";
            //</DEBUG_ONLY>

            Settings.BEACON_PROXIMITY_UUID = (String) tokenizer.nextElement();
            Log.d("Settings: ", Settings.BEACON_PROXIMITY_UUID);
            Settings.PEBBLE_NOTIFICATION_ID = Integer.valueOf((String) tokenizer.nextElement());
            Settings.ANDROID_NOTIFICATION_ID = Integer.valueOf((String) tokenizer.nextElement());
            Settings.LOCATION_COUNTER = (String) tokenizer.nextElement();
            Settings.SERVER_ADDRESS = (String) tokenizer.nextElement();
        }
    }

    public void resetUserData(final App app) {
        saveUserData(new UserData(), app);
    }
}