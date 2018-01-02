package com.entuition.wekend.view.main.setting.viewmodel;

/**
 * Created by ryukgoo on 2017. 12. 26..
 */

public interface SettingEditProfileNavigator {

    void dismiss();

    void showUpdateError();
    void showTooLongIntroduce();
    void showInvalidCode();
    void showRequestCode();
    void showRequestCodeFailed();
    void showEditPhoneComplete();
    void showEditPhoneFailed();
}
