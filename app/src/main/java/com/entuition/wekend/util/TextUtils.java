package com.entuition.wekend.util;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class TextUtils {

    public static final String TAG = TextUtils.class.getSimpleName();

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,}$";
    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9_-]{2,15}$";
    private static final String PHONENUMBER_PATTERN = "\\d{11}";

    /**
     * Validate Username's regular Expression Pattern
     * @param username
     * @return boolean
     */
    public static boolean isValidEmailExpression(String username) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
        Log.d(TAG, "isValidEmailExpression > password: " + username + ", valid : " + matcher.matches());
        return matcher.matches();
    }

    /**
     * Validate Password's regular Expression Pattern
     * @param password
     * @return
     */
    public static boolean isValidPasswordExpression(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        Log.d(TAG, "isValidEmailExpression > password: " + password + ", valid : " + matcher.matches());
        return matcher.matches();
    }

    /**
     * Validate nickname's regular Expression Pattern
     * @param nickname
     * @return
     */
    public static boolean isValidNicknameExpression(String nickname) {
        Pattern pattern = Pattern.compile(NICKNAME_PATTERN);
        Matcher matcher = pattern.matcher(nickname);
        Log.d(TAG, "isValidEmailExpression > password: " + nickname + ", valid : " + matcher.matches());
        return matcher.matches();
    }

    /**
     * Validate phoneNumber's regular Expression Pattern
     * @param phoneNumber
     * @return
     */
    public static boolean isValidPhoneNumberExpression(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONENUMBER_PATTERN);
        Matcher matcher = pattern.matcher(phoneNumber);
        Log.d(TAG, "isValidEmailExpression > password: " + phoneNumber + ", valid : " + matcher.matches());
        return matcher.matches();
    }

    public static boolean isNullorEmptyString(String str) {
        return (str == null || str.isEmpty());
    }
}
