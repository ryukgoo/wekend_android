package com.entuition.wekend.model.data.product.asynctask;

import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.user.UserInfo;

/**
 * Created by ryukgoo on 2016. 4. 18..
 */
public interface ILoadUserInfoAndProductInfoCallback {
    void onPrePared();
    void onSuccess(UserInfo userInfo, ProductInfo productInfo);
    void onFailed();
}
