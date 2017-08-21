package com.entuition.wekend.model;

import android.app.ActivityManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.AbstractAWSSigner;
import com.amazonaws.util.DateUtils;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class Utilities {

    public static final String ENCODING_FORMAT = "UTF8";
    public static final String SIGNATURE_METHOD = "HmacSHA256";
    public static final String HTML_NEW_LINE = "<br />";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z~!@#$%^&*-=_+]).{6,20})";
    private static final String NICKNAME_PATTERN = "^[가-힣a-z0-9_-]{2,15}$";
    private static final String PHONENUMBER_PATTERN = "\\d{11}";

    public static String getDeviceUUID(final Context context) {

        UUID uuid = null;

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return uuid.toString();
    }

    /**
     * Validate Username's regular Expression Pattern
     * @param username
     * @return boolean
     */
    public static boolean isValidEmailExpression(String username) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(username);
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
        return matcher.matches();
    }

    /**
     *
     * @param nickname
     * @return
     */
    public static boolean isValidNicknameExpression(String nickname) {
        Pattern pattern = Pattern.compile(NICKNAME_PATTERN);
        Matcher matcher = pattern.matcher(nickname);
        return matcher.matches();
    }

    public static boolean isValidPhoneNumberExpression(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONENUMBER_PATTERN);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = random.generateSeed(16);
        String randomString = new String(Hex.encodeHex(randomBytes));
        return randomString;
    }

    public static String getTimestamp() {
        return DateUtils.formatISO8601Date(new Date());
    }

    public static String convertDateStringFromTimestamp(String timestamp) {
        Date date = DateUtils.parseISO8601Date(timestamp);
        return DateUtils.format("yyyy.MM.dd", date);
    }

    public static String mapToString(Map<String, String> map) {

        StringBuilder stringBuilder = new StringBuilder();

        for (String key : map.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String value = map.get(key);
            try {
                stringBuilder.append((key != null) ? URLEncoder.encode(key, "UTF-8") : "");
                stringBuilder.append("=");
                stringBuilder.append((value != null) ? URLEncoder.encode(value, "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return stringBuilder.toString();
    }

    public static Map<String, String> stringToMap(String input) {
        Map<String, String> map = new HashMap<String, String>();

        String[] nameValuePairs = input.split("&");
        for (String nameValuePair : nameValuePairs) {
            String[] nameValue = nameValuePair.split("=");
            try {
                map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(nameValue[1], "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return map;
    }

    public static ArrayList<String> asSortedArrayList(Set<String> stringSet) {
        if (stringSet == null) return new ArrayList<String>();
        else return new ArrayList<String>(new TreeSet<String>(stringSet));
    }

    public static int getAgeFromBirthYear(int birthYear) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int thisYear = calendar.get(Calendar.YEAR);

        return thisYear - birthYear;
    }

    public static int getThisYear() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public static int generateRandomVerificationCode() {
        return new Random().nextInt(1000000);
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context, Locale.KOREA);
        List<Address> addresses;
        LatLng point = null;

        try {
            addresses = coder.getFromLocationName(strAddress, 5);
            if (addresses == null || addresses.isEmpty()) {
                return null;
            }

            Address location = addresses.get(0);
            location.getLatitude();
            location.getLongitude();

            point = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return point;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * sign password
     * @param dataToSign
     * @param key
     * @return
     */
    public static String getSignature(String dataToSign, String key) {
        return new Signer().getSignature(dataToSign, key);
    }

    static class Signer extends AbstractAWSSigner {
        public String getSignature(String dataToSign, String key) {
            try {
                byte[] data = dataToSign.getBytes(ENCODING_FORMAT);
                Mac mac = Mac.getInstance(SIGNATURE_METHOD);
                mac.init(new SecretKeySpec(key.getBytes(ENCODING_FORMAT), SIGNATURE_METHOD));
                char[] signature = Hex.encodeHex(mac.doFinal(data));
                return new String(signature);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void addSessionCredentials(Request<?> request, AWSSessionCredentials credentials) {

        }

        @Override
        public void sign(Request<?> request, AWSCredentials credentials) throws AmazonClientException {

        }
    }
}
