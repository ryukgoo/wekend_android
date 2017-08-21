package com.entuition.wekend.model.authentication.asynctask;

/**
 * Created by ryukgoo on 2016. 6. 8..
 */
public interface IAuthenticationCallback {
    void onPrepare();
    void onSuccess();
    void onFailed();
}
