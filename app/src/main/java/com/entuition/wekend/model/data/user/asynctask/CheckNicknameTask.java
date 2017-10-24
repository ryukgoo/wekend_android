package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 2. 11..
 */
public class CheckNicknameTask extends AsyncTask<String, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private ICheckNicknameCallback callback;
    private boolean isNicknameAvailable;

    public CheckNicknameTask(Context context, ICheckNicknameCallback callback) {
        this.context = context;
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
        isNicknameAvailable = UserInfoDaoImpl.getInstance(context).isNicknameAvailable(nickname);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        callback.onSuccess(isNicknameAvailable);
    }
}
