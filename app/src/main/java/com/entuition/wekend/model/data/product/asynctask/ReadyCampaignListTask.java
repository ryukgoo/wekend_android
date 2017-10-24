package com.entuition.wekend.model.data.product.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.product.ProductQueryOptions;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 10..
 */
public class ReadyCampaignListTask extends AsyncTask<ProductQueryOptions, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private IReadyCampaignListCallback callback;
    private List<ProductInfo> results;
    private List<LikeDBItem> likeList;

    public ReadyCampaignListTask(Context context) {
        this.context = context;
    }

    public void setCallback(IReadyCampaignListCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(ProductQueryOptions... params) {
        String userId = UserInfoDaoImpl.getInstance(context).getUserId();

        likeList = LikeDBDaoImpl.getInstance().getLikedProductList(userId);
        results = ProductDaoImpl.getInstance().loadProductList(params[0], likeList);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (results != null) callback.onCompleted(likeList);
        else callback.onFailed();
    }
}
