package com.entuition.wekend.view.join.viewmodel;

import android.support.annotation.NonNull;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public interface InputAccountNavigator {

    void gotoInputUserInfo(@NonNull String username, @NonNull String password);

    void showNotEqualConfirm();
    void showDuplicatedEmail();
}
