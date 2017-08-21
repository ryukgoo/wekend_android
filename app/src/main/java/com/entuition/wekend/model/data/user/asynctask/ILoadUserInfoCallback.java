package com.entuition.wekend.model.data.user.asynctask;

import com.entuition.wekend.model.data.user.UserInfo;

/**
 * Created by ryukgoo on 2016. 4. 28..
 */
public interface ILoadUserInfoCallback {
    void onSuccess(UserInfo userInfo);
    void onFailed();
}
