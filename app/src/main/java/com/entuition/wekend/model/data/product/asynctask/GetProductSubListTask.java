package com.entuition.wekend.model.data.product.asynctask;

import android.os.AsyncTask;

import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 7. 26..
 */
public class GetProductSubListTask extends AsyncTask<Void, Void, Void> {

    private IGetProductSubListCallback callback;
    private List<ProductInfo> results;

    public void setCallback(IGetProductSubListCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPrepare();
    }

    @Override
    protected Void doInBackground(Void... params) {

        results = ProductDaoImpl.getInstance().getProductSubList();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (results != null) {
            callback.onCompleted(results);
        } else {
            callback.onFailed();
        }
    }
}
