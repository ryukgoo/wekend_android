package com.entuition.wekend.view.join.viewmodel;

import android.support.annotation.NonNull;

/**
 * Created by ryukgoo on 2017. 10. 27..
 */

public interface InputUserInfoNavigator {

    void onClickNextButton(@NonNull String nickname, @NonNull String gender);

    void showInvalidNickname();
    void showDuplicatedNickname();
    void showAvailableNickname();

    void hideKeyboard();
}
