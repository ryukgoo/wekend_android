package com.entuition.wekend.model.data.like.asynctask;

/**
 * Created by ryukgoo on 2016. 4. 9..
 */
public interface IAddLikeProductCallback {
    void onSuccess(int totalCount, int friendCount);
    void onFailed();
}
