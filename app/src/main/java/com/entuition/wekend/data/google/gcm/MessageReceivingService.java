package com.entuition.wekend.data.google.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.SharedPreferencesWrapper;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.observable.AddLikeObservable;
import com.entuition.wekend.data.source.mail.observable.ReceiveMailObservable;
import com.entuition.wekend.data.source.mail.observable.SendMailObservable;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.Constants.NotificationType;
import com.entuition.wekend.view.SplashScreen;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by ryukgoo on 2016. 5. 17..
 */
public class MessageReceivingService extends IntentService {

    public static final String TAG = MessageReceivingService.class.getSimpleName();

    private static final String KEY_MESSAGE = "message";

    public MessageReceivingService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        UserInfoRepository.getInstance(getApplicationContext()).registerEndpointArn(null, null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
    }

    public static void handleRemoteNotification(Bundle extras, Context context) {

        Log.d(TAG, "handleRemoteNotification");

        String[] messages = parseMessage(extras);
        if (messages == null) return;

        NotificationType type = NotificationType.valueOf(messages[0]);
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

        Intent intent = new Intent(context, SplashScreen.class);
        postNotification(intent, context, type, message, badge);
    }

    private static void postNotification(Intent intentAction, Context context, NotificationType type, String message, int badge) {

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

//        sendIconBadge(context, badge);
        ShortcutBadger.applyCount(context, badge);
    }

    private static int getTabIndexFromType(Constants.NotificationType type) {

        switch (type) {
            case like:
                return 1;
            case receiveMail:
            case sendMail:
                return 2;
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
        return null;
    }

    /*
    private static void sendIconBadge(Context context, int badge) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(context));
        intent.putExtra("badge_count", badge);
        context.sendBroadcast(intent);
    }

    private static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
    */
}