package com.entuition.wekend.view.main.mailbox.viewmodel;

/**
 * Created by ryukgoo on 2017. 11. 23..
 */

public interface MailProfileNavigator {

    void confirmPropose();

    void onProposeComplete(String nickname);
    void onAcceptComplete(String nickname);
    void onRejectComplete(String nickname);

    void showNotEnoughPoint();
    void showTryAgain();

}
