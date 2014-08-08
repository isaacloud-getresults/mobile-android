package getresultsapp.sointeractve.pl.getresultsapp.data;

import pl.sointeractive.isaacloud.Isaacloud;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

public class App extends Application {

    private static Isaacloud connector;
    private static App obj;
    private static FileManager fileManager;
    private static DataManager dataManager;
    private static EventManager eventManager;


    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        Log.d("APP:", "FileManager created");
        fileManager = new FileManager();
        eventManager = new EventManager();
        dataManager = new DataManager();
    }


    public static DataManager getDataManager() {
        return dataManager;
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static void saveUserData(UserData userData) {
        fileManager.saveUserData(userData, obj);
    }

    public static UserData loadUserData() {
        return fileManager.loadUserData(obj);
    }

    public static void saveLoginData(LoginData data) {
        fileManager.saveLoginData(data, obj);
    }

    public static LoginData loadLoginData() {
        return fileManager.loadLoginData(obj);
    }

    public static String loadConfigData() { return fileManager.loadConfigData(obj); }

    public static void saveConfigData(String data) { fileManager.saveConfigData(data, obj); }

    public static App getInstance() {
        return obj;
    }

    public static void setConnector(Isaacloud connector) {
        App.connector = connector;
    }

    public static Isaacloud getConnector() {
        return connector;
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) obj.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static List<Location> getLocations () {
        return dataManager.getLocations();
    }

    public static List<Person> getPeopleAtLocation(Location l) {
        return dataManager.getPeopleAtLocation(l);
    }

}