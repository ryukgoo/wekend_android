package com.entuition.wekend.model.data.like.asynctask;

/**
 * Created by ryukgoo on 2016. 4. 10..
 */
public interface IHasLikeProductCallback {
    void onPrepared();
    void onSuccess(int count);
    void onFailed();
}
