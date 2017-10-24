package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 10. 13..
 */

public class UpdateUserInfoTask extends AsyncTask<UserInfo, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private boolean isSuccess = false;
    private ISimpleTaskCallback callback;

    public UpdateUserInfoTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(UserInfo... userInfos) {

        isSuccess = UserInfoDaoImpl.getInstance(context).updateUserInfo(userInfos[0]);

        Log.d(TAG, "UpdateUserInfoTask > isSuccess : " + isSuccess);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            if (callback != null) callback.onSuccess(null);
        } else {
            if (callback != null) callback.onFailed();
        }
    }
}
