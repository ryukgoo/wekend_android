package com.entuition.wekend.data;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by ryukgoo on 2015. 12. 23..
 */
public class SharedPreferencesWrapper {

    public static final String TAG = SharedPreferencesWrapper.class.getSimpleName();

    private static final String USER_NAME = "USER_NAME";
    private static final String DEVIDE_UID = "DEVICE_UID";
    private static final String DEVICE_KEY = "DEVICE_KEY";
    private static final String USER_ID = "USER_ID";
    private static final String REGISTRATION_ID = "REGISTRATION_ID";
    private static final String NOTIFICATION_LIKE_NUM = "NOTIFICATION_LIKE_NUM";
    private static final String NOTIFICATION_MAIL_NUM = "NOTIFICATION_MAIL_NUM";
    private static final String NOTIFICATION_ALARM = "NOTIFICATION_ALARM";
    private static final String NOTIFICATION_VIBRATION = "NOTIFICATION_VIBRATION";
    private static final String NO_MORE_GUIDE = "NO_MORE_GUIDE_RE";

    public static void wipe(SharedPreferences sharedPreferences) {
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, DEVIDE_UID, null);
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, DEVICE_KEY, null);
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, USER_NAME, null);
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, USER_ID, null);
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, REGISTRATION_ID, null);
    }

    public static void registerDeviceId(SharedPreferences sharedPreferences, String uid, String key) {
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, DEVIDE_UID, uid);
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, DEVICE_KEY, key);
    }

    public static void registerUserId(SharedPreferences sharedPreferences, String userid) {
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, USER_ID, userid);
    }

    public static void registerUsername(SharedPreferences sharedPreferences, String username) {
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, USER_NAME, username);
    }

    public static void registerRegistrationId(SharedPreferences sharedPreferences, String registrationId) {
        SharedPreferencesWrapper.storeValueInSharedPreferences(sharedPreferences, REGISTRATION_ID, registrationId);
    }

    public static void saveNotificationLikeNum(SharedPreferences sharedPreferences, int num) {
        SharedPreferencesWrapper.storeIntInSharedPreferences(sharedPreferences, NOTIFICATION_LIKE_NUM, num);
    }

    public static void saveNotificationMailNum(SharedPreferences sharedPreferences, int num) {
        SharedPreferencesWrapper.storeIntInSharedPreferences(sharedPreferences, NOTIFICATION_MAIL_NUM, num);
    }

    public static void saveNotificationAlarm(SharedPreferences sharedPreferences, boolean isAvailable) {
        SharedPreferencesWrapper.storeBooleanSharedPreferences(sharedPreferences, NOTIFICATION_ALARM, isAvailable);
    }

    public static void saveNotificationVibration(SharedPreferences sharedPreferences, boolean isAvailable) {
        SharedPreferencesWrapper.storeBooleanSharedPreferences(sharedPreferences, NOTIFICATION_VIBRATION, isAvailable);
    }

    public static void setShowNoMoreGuide(SharedPreferences sharedPreferences, boolean isShow) {
        SharedPreferencesWrapper.storeBooleanSharedPreferences(sharedPreferences, NO_MORE_GUIDE, isShow);
    }

    public static String getUidForDevice(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getValueFromSharedPreferences(sharedPreferences, DEVIDE_UID);
    }

    public static String getKeyForDevice(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getValueFromSharedPreferences(sharedPreferences, DEVICE_KEY);
    }

    public static String getUsernameFromSharedPreferences(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getValueFromSharedPreferences(sharedPreferences, USER_NAME);
    }

    public static String getUserIdFromSharedPreferences(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getValueFromSharedPreferences(sharedPreferences, USER_ID);
    }

    public static String getRegistrationIdFromSharedPreferences(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getValueFromSharedPreferences(sharedPreferences, REGISTRATION_ID);
    }

    public static int getNotificationLikeNumFromSharedPreferences(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getIntFromSharedPreferences(sharedPreferences, NOTIFICATION_LIKE_NUM, 0);
    }

    public static int getNotificationMailNumFromSharedPreferences(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getIntFromSharedPreferences(sharedPreferences, NOTIFICATION_MAIL_NUM, 0);
    }

    public static boolean getNotificationAlarm(SharedPreferences sharedPreferences) {
        return getBooleanFromSharedPreferences(sharedPreferences, NOTIFICATION_ALARM, true);
    }

    public static boolean getNotificationVibration(SharedPreferences sharedPreferences) {
        return getBooleanFromSharedPreferences(sharedPreferences, NOTIFICATION_VIBRATION, true);
    }

    public static boolean getShowNoMoreGuide(SharedPreferences sharedPreferences) {
        return SharedPreferencesWrapper.getBooleanFromSharedPreferences(sharedPreferences, NO_MORE_GUIDE, false);
    }

    private static void storeIntInSharedPreferences(SharedPreferences sharedPreferences, String key, int value) {
        Log.d(TAG, "storeIntInSharedPreferences > key : " + key + ", value : " + value);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static int getIntFromSharedPreferences(@NonNull SharedPreferences sharedPreferences, String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    private static void storeValueInSharedPreferences(SharedPreferences sharedPreferences, String key, String value) {
        Log.d(TAG, "storeValueInSharedPreferences > key : " + key + ", value : " + value);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static String getValueFromSharedPreferences(SharedPreferences sharedPreferences, String key) {
        String value = sharedPreferences.getString(key, null);
        Log.d(TAG, "getValueFromSharedPreferences > key : " + key + ", value : " + value);
        return value;
    }

    private static void storeBooleanSharedPreferences(SharedPreferences sharedPreferences, String key, boolean bool) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    private static boolean getBooleanFromSharedPreferences(SharedPreferences sharedPreferences, String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
