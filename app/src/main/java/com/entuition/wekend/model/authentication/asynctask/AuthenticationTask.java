package com.entuition.wekend.model.authentication.asynctask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;

import com.entuition.clientsdk.model.LoginResponseModel;
import com.entuition.wekend.R;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.view.join.InsertPhotoActivity;
import com.entuition.wekend.view.main.ContainerActivity;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2015. 12. 23..
 */
public class AuthenticationTask extends AsyncTask<UserInfo, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private String userid;
    private boolean isTokenNull = false;
    private boolean isSuccessful;

    private boolean isNoPhotos;

    private IAuthenticationCallback callback;

    public AuthenticationTask(Context context) {
        this.context = context;
    }

    public void setCallback(IAuthenticationCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(UserInfo... params) {

        Log.d(TAG, "AuthenticationTask > doInBackground");

        LoginResponseModel responseModel = DeveloperAuthenticationProvider.getDevAuthClientInstance()
                .login(params[0].getUsername(), params[0].getHashedPassword());

        Log.d(TAG, "Login response is null : " + (responseModel == null));
        Log.d(TAG, "Login response > enable : " + responseModel.getEnable());

        isSuccessful = (responseModel.getEnable() != null && responseModel.getEnable().equals("true"));

        Log.d(TAG, "Login response > isSuccessful : " + isSuccessful);

        if (isSuccessful) {
            DeveloperAuthenticationProvider authenticationProvider =
                    (DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider();
            CognitoSyncClientManager.addLogins(authenticationProvider.getProviderName(), params[0].getUsername());
            String token = authenticationProvider.refresh();

            if (token == null) {
                isTokenNull = true;
            } else {
                userid = responseModel.getUserid();

                UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userid);
                if (userInfo.getPhotos() != null) {
                    ArrayList<String> photos = Utilities.asSortedArrayList(userInfo.getPhotos());
                    isNoPhotos = photos == null || photos.size() == 0;
                } else {
                    isNoPhotos = true;
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (!isSuccessful) {

            new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                    .setTitle(context.getString(R.string.login_dialog_failed_title))
                    .setMessage(context.getString(R.string.login_dialog_failed_message))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();

            callback.onFailed();
        } else {

            if (isTokenNull) {
                new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                        .setTitle(context.getString(R.string.refresh_token_failed_title))
                        .setMessage(context.getString(R.string.refresh_token_failed_message))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                DeveloperAuthenticationProvider.getDevAuthClientInstance().logout();
                            }
                        })
                        .show();
                return;
            }

            callback.onSuccess();

            if (isNoPhotos) {
                Intent intent = new Intent(context, InsertPhotoActivity.class);
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(context, ContainerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

}
