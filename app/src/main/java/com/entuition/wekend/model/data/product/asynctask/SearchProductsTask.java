package com.entuition.wekend.model.data.product.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 10. 17..
 */

public class SearchProductsTask extends AsyncTask<String, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private ISimpleTaskCallback callback;
    private List<ProductInfo> results;

    public SearchProductsTask(Context context) {
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
    protected Void doInBackground(String... strings) {

        String keyword = strings[0];

        String userId = UserInfoDaoImpl.getInstance(context).getUserId();
        List<LikeDBItem> likeList = LikeDBDaoImpl.getInstance().getLikedProductList(userId);
        results = ProductDaoImpl.getInstance().searchProducts(keyword, likeList);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (results != null) callback.onSuccess(results);
        else callback.onFailed();
    }
}
