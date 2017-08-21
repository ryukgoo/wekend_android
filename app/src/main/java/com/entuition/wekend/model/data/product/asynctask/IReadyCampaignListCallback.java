package com.entuition.wekend.model.data.product.asynctask;

import com.entuition.wekend.model.data.like.LikeDBItem;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 10..
 */
public interface IReadyCampaignListCallback {
    void onPrepare();
    void onCompleted(List<LikeDBItem> results);
    void onFailed();
}
