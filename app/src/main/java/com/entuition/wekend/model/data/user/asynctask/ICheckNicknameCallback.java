package com.entuition.wekend.model.data.user.asynctask;

/**
 * Created by ryukgoo on 2016. 2. 11..
 */
public interface ICheckNicknameCallback {
    void onPrepare();
    void onSuccess(boolean isAvaliable);
    void onFailed();
}
