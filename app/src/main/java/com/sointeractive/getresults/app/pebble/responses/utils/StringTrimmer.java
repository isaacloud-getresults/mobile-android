package com.sointeractive.getresults.app.pebble.responses.utils;

public class StringTrimmer {
    public static final int MAX_ACHIEVEMENT_DESCRIPTION_STR_LEN = 50; //78;

    private static final int MAX_COWORKER_FULL_NAME_STR_LEN = 45;
    private static final int MAX_ACHIEVEMENT_NAME_STR_LEN = 80;
    private static final int MAX_BEACON_NAME_STR_LEN = 45;
    private static final int MAX_USER_NAME_STR_LEN = 45;


    public static String getCoworkerName(final String name) {
        return getSafeLengthString(name, MAX_COWORKER_FULL_NAME_STR_LEN);
    }

    public static String getAchievementName(final String name) {
        return getSafeLengthString(name, MAX_ACHIEVEMENT_NAME_STR_LEN);
    }

    public static String getBeaconName(final String name) {
        return getSafeLengthString(name, MAX_BEACON_NAME_STR_LEN);
    }

    public static String getUserName(final String name) {
        return getSafeLengthString(name, MAX_USER_NAME_STR_LEN);
    }

    private static String getSafeLengthString(final String s, final int maxLength) {
        if (s.length() > maxLength) {
            return s.substring(0, maxLength);
        } else {
            return s;
        }
    }
}
