package com.entuition.wekend.model.data.mail.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.ReceiveMail;
import com.entuition.wekend.model.data.mail.ReceiveMailDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public class GetReceiveMailTask extends AsyncTask<ReceiveMail, Void, Void> {

    private ReceiveMail result;
    private ISimpleTaskCallback callback;

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
        String userId = mail.getUserId();
        String senderId = mail.getSenderId();
        int productId = mail.getProductId();

        result = ReceiveMailDaoImpl.getInstance().getReceiveMail(userId, senderId, productId);

        if (result != null && result.getIsRead() == Constants.MAIL_STATUS_UNREAD) {
            result.setIsRead(Constants.MAIL_STATUS_READ);
            ReceiveMailDaoImpl.getInstance().updateReceiveMail(result);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (result != null) {
            callback.onSuccess(result);
        } else {
            callback.onFailed();
        }
    }
}
