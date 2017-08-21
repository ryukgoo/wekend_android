package com.entuition.wekend.model.data.like.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;

/**
 * Created by ryukgoo on 2016. 4. 10..
 */
public class HasLikeProductTask extends AsyncTask<LikeDBItem, Void, Void> {

    private static final String TAG = "HasLikeProductTask";

    private LikeDBItem likeItem;
    private boolean hasLikeProduct;
    private IHasLikeProductCallback callback;
    private int friendCount;

    public void setCallback(IHasLikeProductCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        callback.onPrepared();
    }

    @Override
    protected Void doInBackground(LikeDBItem... params) {

        LikeDBItem value = params[0];
        String userId = value.getUserId();
        int productId = value.getProductId();
        String gender = value.getGender();

        likeItem = LikeDBDaoImpl.getInstance().getLikeItem(userId, productId);
        hasLikeProduct = (likeItem != null);
        friendCount = LikeDBDaoImpl.getInstance().getLikedFriendCount(productId, gender);

        Log.d(TAG, "friendCount : " + friendCount);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (hasLikeProduct) {
            callback.onSuccess(friendCount);
        } else {
            callback.onFailed();
        }
    }
}
