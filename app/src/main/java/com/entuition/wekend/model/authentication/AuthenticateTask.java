package com.entuition.wekend.model.authentication;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2017. 10. 16..
 */

public class AuthenticateTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ISimpleTaskCallback callback;
    private boolean isRegistered;

    public AuthenticateTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        CognitoSyncClientManager.init(context);

        isRegistered = false;
        String username = DeveloperAuthenticationProvider.getDevAuthClientInstance().getUsernameFromDevice();

        if (username != null) {
            DeveloperAuthenticationProvider authenticationProvider =
                    (DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider();
            CognitoSyncClientManager.addLogins(authenticationProvider.getProviderName(), username);
            String token = authenticationProvider.refresh();

            isRegistered = (token != null);

            if (isRegistered) {
                String userId = UserInfoDaoImpl.getInstance().getUserId(context);
                UserInfoDaoImpl.getInstance().loadUserInfo(userId);
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
