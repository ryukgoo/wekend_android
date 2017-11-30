package com.entuition.wekend.data.google.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ryukgoo on 2016. 5. 17..
 */
public class ExternalReceiver extends BroadcastReceiver {

    public static final String TAG = ExternalReceiver.class.getSimpleName();

    public static final String ACTION_GOOGLE_RECEIVE = "com.google.android.c2dm.intent.RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_GOOGLE_RECEIVE)) {
                Bundle extras = intent.getExtras();
                MessageReceivingService.handleRemoteNotification(extras, context);
            }

        }
    }
}
