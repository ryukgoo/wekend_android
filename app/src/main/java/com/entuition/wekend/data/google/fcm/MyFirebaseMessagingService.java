package com.entuition.wekend.data.google.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.SharedPreferencesWrapper;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.observable.AddLikeObservable;
import com.entuition.wekend.data.source.mail.observable.ReceiveMailObservable;
import com.entuition.wekend.data.source.mail.observable.SendMailObservable;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.SplashScreen;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private static final String KEY_MESSAGE = "message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG,"From : " + remoteMessage.getFrom());

        String[] messages = parseMessage(remoteMessage.getData());
        if (messages == null) return;

        try {
            Constants.NotificationType type = Constants.NotificationType.valueOf(messages[0]);
            String message = messages[1];
            int productId = Integer.parseInt(messages[2]);
            int badge = Integer.parseInt(messages[3]);

            NotificationCountingObservable.getInstance().change(type);

            switch (type) {
                case like:
                    LikeInfo info = new LikeInfo();
                    info.setProductId(productId);
                    AddLikeObservable.getInstance().addLike(info);
                    break;
                case sendMail:
                    SendMailObservable.getInstance().change();
                    break;
                case receiveMail:
                    ReceiveMailObservable.getInstance().change();
                    break;
            }

            Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
            postNotification(intent, getApplicationContext(), type, message, badge);

            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Message Notification Body : " + remoteMessage.getNotification().getBody());
            }

            for (String key : remoteMessage.getData().keySet()) {
                Log.d(TAG, "key : " + key + ", value : " + remoteMessage.getData().get(key));
            }
        } catch (IllegalArgumentException e) {

        }
    }

    private void postNotification(Intent intentAction, Context context, Constants.NotificationType type, String message, int badge) {
        Log.d(TAG, "postNotification > message : " + message + ", badge : " + badge);

        intentAction.putExtra(Constants.START_ACTIVITY_POSITION, getTabIndexFromType(type));

        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_icon)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(pendingIntent)
//                .setBadgeIconType(R.drawable.app_icon)
//                .setChannelId("1234")
                .setTicker(message)
                .setNumber(badge)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        boolean isAvailableAlarm = SharedPreferencesWrapper.getNotificationAlarm(sharedPreferences);
        boolean isAvailableVibration = SharedPreferencesWrapper.getNotificationVibration(sharedPreferences);

        int notificationDefault = Notification.DEFAULT_LIGHTS;// | Notification.FLAG_AUTO_CANCEL;
        if (isAvailableAlarm) notificationDefault |= Notification.DEFAULT_SOUND;
        if (isAvailableVibration) notificationDefault |= Notification.DEFAULT_VIBRATE;
        notificationBuilder.setDefaults(notificationDefault);

        switch (type) {
            case like:
                notificationManager.notify(R.string.notification_id, notificationBuilder.build());
                break;
            case receiveMail:
            case sendMail:
                notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
                break;

        }

        ShortcutBadger.applyCount(context, badge);
    }

    private int getTabIndexFromType(Constants.NotificationType type) {

        switch (type) {
            case like:
                return 1;
            case receiveMail:
            case sendMail:
                return 2;
        }

        return 0;
    }

    private String[] parseMessage(Map<String, String> data) {
        if (data != null) {
            for (String key : data.keySet()) {
                if (key.equals(KEY_MESSAGE)) {
                    return data.get(key).split("\\|\\|");
                }
            }
        }
        return null;
    }
}
