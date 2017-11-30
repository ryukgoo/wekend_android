package com.entuition.wekend.view.join.viewmodel;

/**
 * Created by ryukgoo on 2017. 10. 27..
 */

public interface InputPhoneNavigator {

    void onCompleteLogin();

    void showFailedLogin();
    void showExpiredToken();

    /**
     * Duplicated...
     * @link SettingProfileNavigator
     */
    void showInvalidCode();
    void showRequestCode();
    void showRequestCodeFailed();
    void showRegisterFailed();

    void showStartRegister();
    void showStopRegister();

}
