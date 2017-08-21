package com.entuition.wekend.model.data.mail.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.data.mail.ReceiveMailDaoImpl;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public class LoadReceiveMailTask extends AsyncTask<String, Void, Void> {

    private Context context;
    private ISimpleTaskCallback callback;
    private boolean isSuccess;

    public LoadReceiveMailTask(Context context) {
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
    protected Void doInBackground(String... params) {

        String userId = UserInfoDaoImpl.getInstance().getUserId(context);
        isSuccess = ReceiveMailDaoImpl.getInstance().loadReceiveMailList(userId);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }
}
