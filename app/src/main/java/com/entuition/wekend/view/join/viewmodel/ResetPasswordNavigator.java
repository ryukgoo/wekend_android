package com.entuition.wekend.view.join.viewmodel;

/**
 * Created by ryukgoo on 2018. 2. 27..
 */

public interface ResetPasswordNavigator {

    void notValidPasswordExpression();
    void notMatchConfirmPassword();
    void onResetPasswordComplete(String username);
}
