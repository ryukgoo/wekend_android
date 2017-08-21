package com.entuition.wekend.model.data.mail.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.data.mail.ReceiveMailDaoImpl;
import com.entuition.wekend.model.data.mail.SendMailDaoImpl;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 21..
 */

public class GetNewMailTask extends AsyncTask<Void, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private ISimpleTaskCallback callback;
    private int newMailCount;

    public GetNewMailTask(Context context) {
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

        int receiveMailCount = ReceiveMailDaoImpl.getInstance().getNewMail(userId);
        int sendMailCount = SendMailDaoImpl.getInstance().getNewMail(userId);

        newMailCount = receiveMailCount + sendMailCount;

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.onSuccess(newMailCount);
    }
}
