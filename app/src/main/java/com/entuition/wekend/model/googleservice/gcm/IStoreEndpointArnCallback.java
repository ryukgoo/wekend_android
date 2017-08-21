package com.entuition.wekend.model.googleservice.gcm;

/**
 * Created by ryukgoo on 2016. 5. 27..
 */
public interface IStoreEndpointArnCallback {
    void onSuccess(String registrationId);
    void onFailed();
}
