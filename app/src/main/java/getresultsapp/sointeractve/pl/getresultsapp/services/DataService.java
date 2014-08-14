package getresultsapp.sointeractve.pl.getresultsapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import getresultsapp.sointeractve.pl.getresultsapp.config.Settings;
import getresultsapp.sointeractve.pl.getresultsapp.data.App;

/**
 * Created by mac on 31.07.2014.
 */
public class DataService extends Service {

    private static final String TAG = "DataService";
    private static Timer timer = new Timer();
    private Context context;
    private WebSocketClient mWebSocketClient;


    public DataService() {
        //TODO: Use this sockets
        //SocketIONotifier.INSTANCE.connect(userId);
    }

    public void onCreate() {
        super.onCreate();
        context = this;
        Log.d(TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "startService");
        timer.scheduleAtFixedRate(new dataUpdate(), 0, Settings.dataDownloadInterval);
        //connectWebSocket();
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void connectWebSocket() {

        URI uri;

        try {
            Log.d(TAG, "websocket uri start");
            uri = new URI("wss://178.62.191.47:443");
        } catch (URISyntaxException e) {
            Log.d(TAG, "FAILED TO CONNECT WEBSOCKET");
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");
                try {
                    JSONObject json = new JSONObject();
                    json.put("token", Settings.webSocketToken);
                    json.put("url", "/queues/notifications");
                    mWebSocketClient.send(json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Toast.makeText(context, "RECEIVED NOTIFICATION: " + s, Toast.LENGTH_LONG);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s + i);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }


    ////////////////////////////////
    /// WEB SOCKET CLIENT
    ////////////////////////////////

    private class dataUpdate extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "postEventUpdateData");
            App.getEventManager().postEventUpdateData();
        }
    }


}

