package com.entuition.wekend.view.join.viewmodel;

import com.entuition.wekend.data.source.userinfo.UserInfo;

/**
 * Created by ryukgoo on 2018. 2. 22..
 */

public interface ConfirmAccountNavigator {
    void onConfirmAccount(UserInfo userInfo);
    void onConfirmAccountFailed();
}
