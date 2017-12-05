package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.content.DialogInterface;

/**
 * Created by ryukgoo on 2017. 11. 23..
 */

public interface MailProfileNavigator {

    void confirmPropose(DialogInterface.OnClickListener listener);

    void onProposeComplete(String nickname);
    void onAcceptComplete(String nickname);
    void onRejectComplete(String nickname);

    void showUserInputDialog();
    void showNotEnoughPoint();
    void showTryAgain();

}
