package com.entuition.wekend.view.main.container.viewmodel;

import com.entuition.wekend.data.source.userinfo.UserInfo;

/**
 * Created by ryukgoo on 2017. 10. 31..
 */

public interface ContainerNavigator {

    void onSelectedBottomBar(String tag);
    void onReSelectedBottomBar(String tag);
    void onToggleDrawerLayout();
    void onUserInfoLoaded(UserInfo userInfo);

    void onClickTitle();
    void showGuidePopup(boolean isShowCheckBox);
    void gotoNoticeView();
    void gotoProfileView();
    void sendMailToDeveloper(String username);
    void gotoAlarmSetting();
    void showLogoutDialog();
}
