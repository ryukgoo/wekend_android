package com.entuition.wekend.model.data.user.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2017. 7. 19..
 */

public class CheckAccountTask extends AsyncTask<String, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private ISimpleTaskCallback callback;
    private boolean isValidAccount;

    public CheckAccountTask(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(String... params) {

        String email = params[0];
        isValidAccount = UserInfoDaoImpl.getInstance().isValidEmail(email);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isValidAccount) {
            callback.onSuccess(isValidAccount);
        } else {
            callback.onFailed();
        }
    }
}
