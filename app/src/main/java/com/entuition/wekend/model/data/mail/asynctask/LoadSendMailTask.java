package com.entuition.wekend.model.data.mail.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.data.mail.SendMailDaoImpl;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public class LoadSendMailTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ISimpleTaskCallback callback;
    private boolean isSuccess;

    public LoadSendMailTask(Context context) {
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
    protected Void doInBackground(Void... params) {

        String userId = UserInfoDaoImpl.getInstance().getUserId(context);
        isSuccess = SendMailDaoImpl.getInstance().loadSendMailList(userId);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }
}
