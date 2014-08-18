package com.sointeractive.getresults.app.config;


import java.util.UUID;

public class Settings {
    // Websocket
    public static final String SERVER_ADDRESS = "http://178.62.191.47:443/";
    public static final String URL_TO_LISTEN = "/queues/notifications?limit=0&query=status:0,typeId:%d,subjectId:%d&fields=data";
    public static final String EMIT_EVENT = "chat message";
    public static final String LISTEN_EVENT = "chat message";

    // Data settings
    public static final int LEADERBOARD_ID = 1;
    public static final int PEBBLE_NOTIFICATION_ID = 0;
    public static final int ANDROID_NOTIFICATION_ID = 0;

    // Pebble
    public static final String APP_NAME = "GetResults!";
    public static final UUID PEBBLE_APP_UUID = UUID.fromString("51b19145-0542-474f-8b62-c8c34ae4b87b");
    public static final int MAX_ACHIEVEMENTS_DESCRIPTION_STR_LEN = 78;
    public static final String IC_NOTIFICATION_HEADER = "IsaaCloud notification";
    public static final String LOCATION_COUNTER = "1"; // location id counter from IsaaCloud
    public static final String KITCHEN_VISITED_COUNTER = "2";
    public static final String NULL_ROOM_COUNTER = "3";
    public static final String BROADCAST_INTENT_UPDATE_DATA = "getresults.update.data";
    public static final String BROADCAST_INTENT_NEW_LOCATION = "getresults.new.location";
    public static final String BROADCAST_INTENT_NEW_ACHIEVEMENT = "getresults.new.achievement";
    public static final int DATA_DOWNLOAD_INTERVAL = 5000; // 5 sec
    // IsaaCloud
    public static String INSTANCE_ID = "179";
    public static String APP_SECRET = "3f14569b750b69a8bc352cb34ad3e";
}
