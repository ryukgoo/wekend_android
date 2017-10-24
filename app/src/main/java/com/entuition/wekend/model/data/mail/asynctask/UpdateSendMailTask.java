package com.entuition.wekend.model.data.mail.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.SendMail;
import com.entuition.wekend.model.data.mail.SendMailDaoImpl;
import com.entuition.wekend.model.data.mail.observable.UpdateSendMailObservable;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public class UpdateSendMailTask extends AsyncTask<SendMail, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private boolean isSuccess;
    private ISimpleTaskCallback callback;

    public UpdateSendMailTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    /**
     *
     * @param params SendMail -> userId, receiverId, productId, updatedTime, status
     * @return
     */
    @Override
    protected Void doInBackground(SendMail... params) {

        SendMail mail = params[0];

        UserInfo userInfo = UserInfoDaoImpl.getInstance(context).getUserInfo(mail.getUserId());
        UserInfo receiverInfo = UserInfoDaoImpl.getInstance(context).getUserInfo(mail.getReceiverId());
        ProductInfo productInfo = ProductDaoImpl.getInstance().getProductInfo(mail.getProductId());

        mail.setSenderNickname(userInfo.getNickname());
        mail.setReceiverNickname(receiverInfo.getNickname());
        mail.setProductTitle(productInfo.getTitleKor());
        mail.setIsRead(Constants.MAIL_STATUS_READ);

        String timestamp = Utilities.getTimestamp();

        if (mail.getUpdatedTime() == null) {
            mail.setUpdatedTime(timestamp);
        }
        mail.setResponseTime(timestamp);

        isSuccess = SendMailDaoImpl.getInstance().updateSendMail(mail);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            UpdateSendMailObservable.getInstance().update();
            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }
}
