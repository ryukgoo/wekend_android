package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 10. 13..
 */

public class ConsumePointTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
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

        UserInfo userInfo = UserInfoDaoImpl.getInstance(context).getUserInfo();

        if (userInfo.getBalloon() >= 500) {
            userInfo.setBalloon(userInfo.getBalloon() - 500);
        } else {
            isSuccess = false;
            return null;
        }

        isSuccess = UserInfoDaoImpl.getInstance(context).updateUserInfo(userInfo);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            UserInfo userInfo = UserInfoDaoImpl.getInstance(context).getUserInfo();
            callback.onSuccess(userInfo.getBalloon());
        } else {
            callback.onFailed();
        }
    }
}
