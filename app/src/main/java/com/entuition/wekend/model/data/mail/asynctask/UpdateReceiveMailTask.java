package com.entuition.wekend.model.data.mail.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.mail.ReceiveMail;
import com.entuition.wekend.model.data.mail.ReceiveMailDaoImpl;
import com.entuition.wekend.model.data.mail.UpdateReceiveMailObservable;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 20..
 */

public class UpdateReceiveMailTask extends AsyncTask<ReceiveMail, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private boolean isSuccess;
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

        UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(mail.getUserId());
        UserInfo senderInfo = UserInfoDaoImpl.getInstance().getUserInfo(mail.getSenderId());
        ProductInfo productInfo = ProductDaoImpl.getInstance().getProductInfo(mail.getProductId());

        mail.setSenderNickname(senderInfo.getNickname());
        mail.setReceiverNickname(userInfo.getNickname());
        mail.setProductTitle(productInfo.getTitleKor());
        mail.setIsRead(Constants.MAIL_STATUS_READ);

        String timestamp = Utilities.getTimestamp();

        if (mail.getUpdatedTime() == null) {
            mail.setUpdatedTime(timestamp);
        }
        mail.setResponseTime(timestamp);

        isSuccess = ReceiveMailDaoImpl.getInstance().updateReceiveMail(mail);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {

            UpdateReceiveMailObservable.getInstance().update();

            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }
}
