package com.entuition.wekend.model.data.mail.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.SendMail;
import com.entuition.wekend.model.data.mail.SendMailDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public class GetSendMailTask extends AsyncTask<SendMail, Void, Void> {

    private SendMail result;
    private ISimpleTaskCallback callback;

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
        String senderId = mail.getUserId();
        String receiverId = mail.getReceiverId();
        int productId = mail.getProductId();

        result = SendMailDaoImpl.getInstance().getSendMail(senderId, receiverId, productId);

        if (result != null && result.getIsRead() == Constants.MAIL_STATUS_UNREAD) {
            result.setIsRead(Constants.MAIL_STATUS_READ);
            SendMailDaoImpl.getInstance().updateSendMail(result);
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
