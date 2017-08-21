package com.entuition.wekend.model.data.product.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

/**
 * Created by ryukgoo on 2016. 4. 18..
 */
public class LoadUserInfoAndProductInfoTask extends AsyncTask<LikeDBItem, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private UserInfo userInfo;
    private ProductInfo productInfo;

    private ILoadUserInfoAndProductInfoCallback callback;

    public void setCallback(ILoadUserInfoAndProductInfoCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        callback.onPrePared();
    }

    @Override
    protected Void doInBackground(LikeDBItem... params) {

        LikeDBItem likeItem = params[0];

        String userId = likeItem.getUserId();
        int productId = likeItem.getProductId();

        userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userId);
        productInfo = ProductDaoImpl.getInstance().getProductInfo(productId);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (userInfo != null && productInfo != null) {
            callback.onSuccess(userInfo, productInfo);
        } else {
            callback.onFailed();
        }
    }
}
