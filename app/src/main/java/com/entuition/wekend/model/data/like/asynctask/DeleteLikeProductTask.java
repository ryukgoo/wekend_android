package com.entuition.wekend.model.data.like.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.like.observable.DeleteLikeObservable;
import com.entuition.wekend.model.data.product.ProductDaoImpl;

/**
 * Created by ryukgoo on 2016. 6. 28..
 */
public class DeleteLikeProductTask extends AsyncTask<LikeDBItem, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private IDeleteLikeProductCallback callback;
    private LikeDBItem item;
    private boolean isSuccess;
    private int likedCount;

    public void setCallback(IDeleteLikeProductCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(LikeDBItem... params) {

        item = params[0];
        int productId = item.getProductId();

        try {
            LikeDBDaoImpl.getInstance().deleteLike(item);
            likedCount = LikeDBDaoImpl.getInstance().getLikedTotalCount(productId);

            ProductDaoImpl.getInstance().deleteLikeByProductId(productId, likedCount);

            isSuccess = true;
        } catch (Exception e) {
            isSuccess = false;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSuccess) {
            callback.onSuccess(likedCount);
            DeleteLikeObservable.getInstance().deleteLike(item.getProductId());
            LikeDBDaoImpl.getInstance().getList().remove(item);
        } else {
            callback.onFailed();
        }
    }

    public interface IDeleteLikeProductCallback {
        void onSuccess(int likedCount);
        void onFailed();
    }
}
