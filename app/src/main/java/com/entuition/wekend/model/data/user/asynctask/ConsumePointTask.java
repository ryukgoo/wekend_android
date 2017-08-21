package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 10. 13..
 */

public class ConsumePointTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private ISimpleTaskCallback callback;
    private boolean isSuccess = false;

    public ConsumePointTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        UserInfo userInfo = UserInfoDaoImpl.getInstance().loadUserInfo(UserInfoDaoImpl.getInstance().getUserId(context));

        if (userInfo.getBalloon() >= 500) {
            userInfo.setBalloon(userInfo.getBalloon() - 500);
        } else {
            isSuccess = false;
            return null;
        }

        isSuccess = UserInfoDaoImpl.getInstance().updateUserInfo(userInfo);
        UserInfoDaoImpl.getInstance().loadUserInfo(UserInfoDaoImpl.getInstance().getUserId(context));

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            String userId = UserInfoDaoImpl.getInstance().getUserId(context);
            UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userId);
            callback.onSuccess(userInfo.getBalloon());
        } else {
            callback.onFailed();
        }
    }
}
