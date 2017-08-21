package com.entuition.wekend.model.data.like.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 4. 19..
 */
public class GetLikeProductTask extends AsyncTask<Void, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private boolean isSuccess;
    private ISimpleTaskCallback callback;

    public GetLikeProductTask(Context context) {
        this.context = context;
    }

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String userId = UserInfoDaoImpl.getInstance().getUserId(context);
        Log.d(TAG, "GetLikeProductTask >>>>>> userId : " + userId);
        try {
            ProductDaoImpl.getInstance().getLikedTime();
            LikeDBDaoImpl.getInstance().loadLikeProductList(userId);
            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }
}
