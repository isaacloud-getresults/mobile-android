package com.sointeractive.getresults.app.config;


import java.util.UUID;

public class Settings {
    // Websocket
    public static String SERVER_ADDRESS; // "http://178.62.191.47:3001";
    public static final String URL_TO_LISTEN = "/queues/notifications?limit=0&query=status:0,typeId:%d,subjectId:%d&fields=data";
    public static final String EMIT_EVENT = "chat message";
    public static final String LISTEN_EVENT = "chat message";

    // Pebble
    public static final String APP_NAME = "GetResults!";
    public static final UUID PEBBLE_APP_UUID = UUID.fromString("51b19145-0542-474f-8b62-c8c34ae4b87b");
    public static final int MAX_ACHIEVEMENTS_DESCRIPTION_STR_LEN = 50; //78;
    public static final String IC_NOTIFICATION_HEADER = "IsaaCloud notification";
    public static final int RESEND_TIMES_LIMIT = 3;

    // Broadcasts
    public static final String BROADCAST_INTENT_UPDATE_DATA = "getresults.update.data";
    public static final String BROADCAST_INTENT_NEW_LOCATION = "getresults.new.location";
    public static final String BROADCAST_INTENT_NEW_ACHIEVEMENT = "getresults.new.achievement";

    // IC Data
    public static int LEADERBOARD_ID = 1;
    public static int PEBBLE_NOTIFICATION_ID; // 1;
    public static int ANDROID_NOTIFICATION_ID; // 2;

    public static String LOCATION_COUNTER; // 1; // location id counter from IsaaCloud
    public static final String KITCHEN_VISITED_COUNTER = "2";
    public static final String NULL_ROOM_COUNTER = "3";

    // Beacons
    public static String BEACON_PROXIMITY_UUID; // "B9407F30-F5F8-466E-AFF9-25556B57FE6D"

    // IsaaCloud
    public static String INSTANCE_ID;// "179";
    public static String APP_SECRET; // "cbe82930e310e3519666c8ddf9776cee";
    public static int DATA_DOWNLOAD_INTERVAL = 5000; // 5 sec
}
