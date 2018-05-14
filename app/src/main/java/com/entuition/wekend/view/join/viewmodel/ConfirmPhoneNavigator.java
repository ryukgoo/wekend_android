package com.entuition.wekend.view.join.viewmodel;

/**
 * Created by ryukgoo on 2018. 2. 22..
 */

public interface ConfirmPhoneNavigator {
    void onRequestComplete();
    void onRequestFailed();
    void onConfirmCode(String userId, String username);
    void onConfirmCodeFailed();
    void onNotFoundAccount();
    void notMatchRegisteredPhone();
    void onResetPassword(String userId, String username);
}
