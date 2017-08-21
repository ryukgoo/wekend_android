package com.entuition.wekend.model.data.product.asynctask;

import com.entuition.wekend.model.data.product.ProductInfo;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 7. 26..
 */
public interface IGetProductSubListCallback {
    void onPrepare();
    void onCompleted(List<ProductInfo> results);
    void onFailed();
}
