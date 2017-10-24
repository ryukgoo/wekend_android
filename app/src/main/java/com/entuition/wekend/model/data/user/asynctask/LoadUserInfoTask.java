package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 4. 28..
 */
public class LoadUserInfoTask extends AsyncTask<String, Void, Void> {

    private final Context context;
    private UserInfo userInfo;
    private ILoadUserInfoCallback callback;

    public LoadUserInfoTask(Context context) {
        this.context = context;
    }

    public void setCallback(ILoadUserInfoCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {

        String userId = params[0];
        userInfo = UserInfoDaoImpl.getInstance(context).getUserInfo(userId);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (userInfo == null) {
            if (callback != null) callback.onFailed();
        } else {
            if (callback != null) callback.onSuccess(userInfo);
        }
    }
}
