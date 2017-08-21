package com.entuition.wekend.model.data.like.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeReadState;
import com.entuition.wekend.model.data.like.ReadFriendObservable;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;

/**
 * Created by ryukgoo on 2017. 6. 29..
 */

public class UpdateProfileReadStateTask extends AsyncTask<LikeReadState, Void, Void> {

    public final String TAG = getClass().getSimpleName();

    private LikeReadState readState;
    private ISimpleTaskCallback callback;
    private boolean isSuccess;

    public void setCallback(ISimpleTaskCallback callback) { this.callback = callback; }

    @Override
    protected Void doInBackground(LikeReadState... params) {

        readState = params[0];

        Log.d(TAG, "readState.userId : " + readState.getUserId());

        try {
            LikeDBDaoImpl.getInstance().updateProfileReadState(readState);
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            ReadFriendObservable.getInstance().read(readState.getUserId());
            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }
}
