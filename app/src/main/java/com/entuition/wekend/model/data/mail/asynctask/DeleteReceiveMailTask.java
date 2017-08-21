package com.entuition.wekend.model.data.mail.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.mail.ReceiveMail;
import com.entuition.wekend.model.data.mail.ReceiveMailDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 22..
 */

public class DeleteReceiveMailTask extends AsyncTask<ReceiveMail, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private ISimpleTaskCallback callback;
    private boolean isSuccess = false;

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(ReceiveMail... params) {

        ReceiveMail mail = params[0];

        try {
            isSuccess = ReceiveMailDaoImpl.getInstance().deleteReceiveMail(mail);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

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
