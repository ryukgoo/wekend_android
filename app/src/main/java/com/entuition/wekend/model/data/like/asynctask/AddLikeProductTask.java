package com.entuition.wekend.model.data.like.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.AddLikeObservable;
import com.entuition.wekend.model.data.product.ProductDaoImpl;

/**
 * Created by ryukgoo on 2016. 4. 9..
 */
public class AddLikeProductTask extends AsyncTask<LikeDBItem, Void, Void> {

    public final String TAG = getClass().getSimpleName();

    private IAddLikeProductCallback callback;
    private boolean isSuccess;
    private int likedCount;
    private int friendCount;
    private int productId;

    public void setCallback(IAddLikeProductCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(LikeDBItem... params) {

        LikeDBItem value = params[0];

        String userId = value.getUserId();
        int productId = value.getProductId();
        String nickname = value.getNickname();
        String gender = value.getGender();
        String productTitle = value.getProductTitle();
        String productDesc = value.getProductDesc();

        this.productId = productId;

        try {
            LikeDBDaoImpl.getInstance().addLike(userId, productId, nickname, gender, productTitle, productDesc);
            friendCount = LikeDBDaoImpl.getInstance().getLikedFriendCount(productId, gender);
            likedCount = LikeDBDaoImpl.getInstance().getLikedTotalCount(productId);

            ProductDaoImpl.getInstance().addLikeByProductId(productId, likedCount, friendCount);

            isSuccess = true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            isSuccess = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            callback.onSuccess(likedCount, friendCount);
            AddLikeObservable.getInstance().addLike(productId);
        } else {
            callback.onFailed();
        }
    }
}
