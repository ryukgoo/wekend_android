package com.entuition.wekend.model.googleservice.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognito.internal.util.StringUtils;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.SharedPreferencesWrapper;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.view.SplashScreen;
import com.entuition.wekend.view.main.ContainerActivity;

/**
 * Created by ryukgoo on 2016. 5. 17..
 */
public class MessageReceivingService extends IntentService {

    private static final String TAG = "MessageReceivingService";

    private static final String KEY_MESSAGE = "message";

    private static SharedPreferences sharedPreferences;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public MessageReceivingService() {
        super(TAG);
    }

    public static void handleRemoteNotification(Bundle extras, Context context) {

        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }

        String[] messages = parseMessage(extras);
        String type = messages[0];
        String message = messages[1];
        int productId = Integer.parseInt(messages[2]);
        int badge = Integer.parseInt(messages[3]);

        int numOfLikeMessages = SharedPreferencesWrapper.getNotificationLikeNumFromSharedPreferences(sharedPreferences);
        int numOfMailMessages = SharedPreferencesWrapper.getNotificationMailNumFromSharedPreferences(sharedPreferences);

        if (type.equals(Constants.TYPE_NOTIFICATION_LIKE)) {
            SharedPreferencesWrapper.saveNotificationLikeNum(sharedPreferences, ++numOfLikeMessages);
            LikeDBDaoImpl.getInstance().comeNewLikeNotification(productId);
        } else if (type.equals(Constants.TYPE_NOTIFICATION_RECEIVE_MAIL)) {
            SharedPreferencesWrapper.saveNotificationMailNum(sharedPreferences, ++numOfMailMessages);
            MailNotificationObservable.getInstance().change();
        } else if (type.equals(Constants.TYPE_NOTIFICATION_SEND_MAIL)) {
            SharedPreferencesWrapper.saveNotificationMailNum(sharedPreferences, ++numOfMailMessages);
            MailNotificationObservable.getInstance().change();
        }

        Log.d(TAG, "numOfLikeMessages : " + numOfLikeMessages);
        Log.d(TAG, "numOfMailMessages : " + numOfMailMessages);

        BadgeNotificationObservable.getInstance().change(type);

        postNotification(new Intent(context, SplashScreen.class), context, type, message, badge);
    }

    public static void postNotification(Intent intentAction, Context context, String type, String message, int badge) {

        Log.d(TAG, "postNotification > message : " + message);

        intentAction.putExtra(Constants.START_ACTIVITY_POSITION, getMessageType(type));

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // PendingIntent getActivity FLAG?? --> Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
//                .setStyle(new NotificationCompat.MessagingStyle("Wekend"))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message)
                .setNumber(badge)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);

        int alarmOnOff = SharedPreferencesWrapper.getNotificationAlarm(sharedPreferences);
        int vibrationOnOff = SharedPreferencesWrapper.getNotificationVibration(sharedPreferences);

        int notificationDefault = Notification.DEFAULT_LIGHTS;
        if (alarmOnOff == 1) notificationDefault |= Notification.DEFAULT_SOUND;
        if (vibrationOnOff == 1) notificationDefault |= Notification.DEFAULT_VIBRATE;
        notificationBuilder.setDefaults(notificationDefault);

        if (type.equals(Constants.TYPE_NOTIFICATION_LIKE)) {
            notificationManager.notify(R.string.notification_id, notificationBuilder.build());
        } else {
            notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
        }

    }

    private static int getMessageType(String type) {
        if (type.equals(Constants.TYPE_NOTIFICATION_LIKE)) {
            return ContainerActivity.POSITION_LIKE;
        } else if (type.equals(Constants.TYPE_NOTIFICATION_RECEIVE_MAIL) ||
                type.equals(Constants.TYPE_NOTIFICATION_SEND_MAIL)) {
            return ContainerActivity.POSITION_MAIL;
        }
        return 0;
    }

    private static String[] parseMessage(Bundle extras) {
        if (extras != null) {
            for (String key : extras.keySet()) {
                if (key.equals(KEY_MESSAGE)) {
                    return extras.getString(key).split("\\|\\|");
                }
            }
        }
        return new String[0];
    }

    public static  void clearNotificationLikeNum(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        SharedPreferencesWrapper.saveNotificationLikeNum(sharedPreferences, 0);
    }

    public static void clearNotificationMailNum(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        SharedPreferencesWrapper.saveNotificationMailNum(sharedPreferences, 0);
    }

    public static int getNewLikeCount(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        return SharedPreferencesWrapper.getNotificationLikeNumFromSharedPreferences(sharedPreferences);
    }

    public static int getNewMailCount(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        }
        return SharedPreferencesWrapper.getNotificationMailNumFromSharedPreferences(sharedPreferences);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "MessageReceiveService onCreate!!!!");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String token = SharedPreferencesWrapper.getRegistrationIdFromSharedPreferences(sharedPreferences);
        Log.d(TAG, "GCM > onCreate > token : " + token);

        if (StringUtils.isEmpty(token)) {
            register();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void register() {

        Log.d(TAG, "register!!!!!!!!!!!!!!");

        StoreEndpointArnTask task = new StoreEndpointArnTask(getBaseContext());
        task.setCallback(new StoreEndpointArnCallback());
        task.execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.d(TAG, "onBind > action : " + intent.getAction().toString());

        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        sendOrderedBroadcast(broadcast, null);
    }

    private class StoreEndpointArnCallback implements IStoreEndpointArnCallback {

        @Override
        public void onSuccess(String registrationId) {

            Log.d(TAG, "StoreEndpointArnCallback > onSuccess > registrationId : " + registrationId);
            SharedPreferencesWrapper.registerRegistrationId(sharedPreferences, registrationId);
        }

        @Override
        public void onFailed() {
            Log.e(TAG, "StoreEndpointArnCallback > onFailed!!");
        }
    }

}