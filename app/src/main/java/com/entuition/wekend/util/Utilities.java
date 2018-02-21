package com.entuition.wekend.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import bolts.AppLinks;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class Utilities {

    public static final String TAG = "Utilities";

    static final String ENCODING_FORMAT = "UTF8";
    static final String SIGNATURE_METHOD = "HmacSHA256";
    public static final String HTML_NEW_LINE = "<br />";

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

    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = random.generateSeed(16);
        return new String(Hex.encodeHex(randomBytes));
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

    public static String getAgeFromBirthYear(int birthYear) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int thisYear = calendar.get(Calendar.YEAR);
        int age = thisYear - birthYear;

        return String.valueOf(age);
    }

    public static int getThisYear() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public static String getPhoneNumberFormat(String phone) {
        if (phone != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return PhoneNumberUtils.formatNumber(phone, Locale.getDefault().getCountry());
            } else {
                return PhoneNumberUtils.formatNumber(phone);
            }
        }
        return null;
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
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            if (procInfos != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                    if (processInfo.processName.equals(packageName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getLinkIdFromIntent(Context context, Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            Log.d(TAG, "uri : " + uri.toString());
            if (uri.getQueryParameter("productId") != null) {
                return uri.getQueryParameter("productId");
            }
        }

        Uri facebookUri = AppLinks.getTargetUrlFromInboundIntent(context, intent);
        if (facebookUri != null) {
            Log.d(TAG, "facebookUri : " + facebookUri);
            return facebookUri.getQueryParameter("productId");
        }

        return null;
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
        protected void addSessionCredentials(Request<?> request, AWSSessionCredentials credentials) {}

        @Override
        public void sign(Request<?> request, AWSCredentials credentials) throws AmazonClientException {}
    }
}
