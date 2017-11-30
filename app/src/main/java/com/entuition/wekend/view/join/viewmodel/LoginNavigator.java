package com.entuition.wekend.view.join.viewmodel;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public interface LoginNavigator {

    void onCompleteLogin();

    void showInvalidInput();
    void showFailedLogin();
    void showExpiredToken();
}
