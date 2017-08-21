package com.entuition.wekend.model.data.user.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 2. 11..
 */
public class CheckNicknameTask extends AsyncTask<String, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private ICheckNicknameCallback callback;
    private boolean isNicknameAvailable;

    public CheckNicknameTask(ICheckNicknameCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(String... params) {
        Log.d(TAG, "ChecknicknameTask Start");
        String nickname = params[0];
        isNicknameAvailable = UserInfoDaoImpl.getInstance().isNicknameAvailable(nickname);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        callback.onSuccess(isNicknameAvailable);
    }
}
