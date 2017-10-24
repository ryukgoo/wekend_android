package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2017. 7. 19..
 */

public class CheckAccountTask extends AsyncTask<String, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private ISimpleTaskCallback callback;
    private boolean isValidAccount;

    public CheckAccountTask(Context context, ISimpleTaskCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(String... params) {

        String email = params[0];
        isValidAccount = UserInfoDaoImpl.getInstance(context).isValidEmail(email);
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
