package com.entuition.wekend.model.googleservice.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ryukgoo on 2016. 5. 17..
 */
public class ExternalReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive > intent : " + intent.getAction().toString());
        if (intent != null) {

            if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
                Bundle extras = intent.getExtras();

                MessageReceivingService.handleRemoteNotification(extras, context);
            }

        }
    }
}
