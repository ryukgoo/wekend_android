package com.entuition.wekend.model.data.mail.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.SendMail;
import com.entuition.wekend.model.data.mail.SendMailDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 22..
 */

public class DeleteSendMailTask extends AsyncTask<SendMail, Void, Void> {

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
    protected Void doInBackground(SendMail... params) {

        SendMail mail = params[0];

        try {
            isSuccess = SendMailDaoImpl.getInstance().deleteSendMail(mail);
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
