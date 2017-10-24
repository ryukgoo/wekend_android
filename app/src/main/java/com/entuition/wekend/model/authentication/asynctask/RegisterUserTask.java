package com.entuition.wekend.model.authentication.asynctask;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;

import com.entuition.clientsdk.model.RegisterRequestModel;
import com.entuition.clientsdk.model.RegisterResponseModel;
import com.entuition.wekend.R;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.common.ISimpleTaskCallback;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class RegisterUserTask extends AsyncTask<RegisterRequestModel, Void, RegisterResponseModel> {

    private final String TAG = getClass().getSimpleName();

    private static final String RESPONSE_SUCCESS = "success";
    private static final String RESPONSE_FAILED = "failed";
    private final Context context;

    private String username;
    private String password;
    private String nickname;
    private String gender;
    private int birth;
    private String phone;

    private ISimpleTaskCallback callback;

    public RegisterUserTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected RegisterResponseModel doInBackground(RegisterRequestModel... params) {
        username = params[0].getUsername();
        password = params[0].getPassword();
        nickname = params[0].getNickname();
        gender = params[0].getGender();
        birth = params[0].getBirth();
        phone = params[0].getPhone();
        return DeveloperAuthenticationProvider.getDevAuthClientInstance()
                .register(username, password, nickname, gender, birth, phone);
    }

    @Override
    protected void onPostExecute(RegisterResponseModel result) {
        if (result == null || result.getResult().equals(RESPONSE_FAILED)) {
            new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomAlertDialog))
                    .setTitle(context.getString(R.string.register_alertdialog_failed_title))
                    .setMessage(context.getString(R.string.register_alertdialog_failed_message))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            Log.d(TAG, "Register Success");

            ((DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider())
                    .login(username, password, context, callback);
        }
    }
}
