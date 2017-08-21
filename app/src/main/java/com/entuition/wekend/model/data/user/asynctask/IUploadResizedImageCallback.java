package com.entuition.wekend.model.data.user.asynctask;

/**
 * Created by ryukgoo on 2016. 4. 28..
 */
public interface IUploadResizedImageCallback {
    void onSuccess(String imageUrl);
    void onFailed();
}
