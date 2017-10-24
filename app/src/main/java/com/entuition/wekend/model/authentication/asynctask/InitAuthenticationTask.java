package com.entuition.wekend.model.authentication.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2017. 10. 16..
 */

public class InitAuthenticationTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private ISimpleTaskCallback callback;
    private boolean isRegistered;

    public InitAuthenticationTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        CognitoSyncClientManager.init(context.getApplicationContext());

        isRegistered = false;
        String username = DeveloperAuthenticationProvider.getDevAuthClientInstance().getUsernameFromDevice();

        if (username != null) {
            DeveloperAuthenticationProvider authenticationProvider =
                    (DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider();
            CognitoSyncClientManager.addLogins(authenticationProvider.getProviderName(), username);
            String token = authenticationProvider.refresh();

            isRegistered = (token != null);

            if (isRegistered) {
                UserInfoDaoImpl.getInstance(context).getUserInfo();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (callback != null) {
            callback.onSuccess(isRegistered);
        }
    }
}
