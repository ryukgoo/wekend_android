package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;

import com.entuition.wekend.data.source.authentication.AuthenticationDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2018. 2. 22..
 */

public class ResetPasswordViewModel extends AbstractViewModel {

    private static final String TAG = ResetPasswordViewModel.class.getSimpleName();

    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<String> passwordConfirm = new ObservableField<>();

    private final WeakReference<ResetPasswordNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final AuthenticationDataSource authenticationDataSource;

    private String userId;
    private String username;

    public ResetPasswordViewModel(Context context,
                                  ResetPasswordNavigator navigator,
                                  UserInfoDataSource userInfoDataSource,
                                  AuthenticationDataSource authenticationDataSource) {
        super(context);
        this.navigator = new WeakReference<ResetPasswordNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.authenticationDataSource = authenticationDataSource;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void setUserInfo(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public void onClickButton() {
        if (password.get() != null && TextUtils.isValidPasswordExpression(password.get())) {
            if (passwordConfirm.get() != null && passwordConfirm.get().equals(password.get())) {
                authenticationDataSource.resetPassword(userId, password.get(), new AuthenticationDataSource.RegisterCallback() {
                    @Override
                    public void onCompleteRegister() {
                        if (navigator.get() != null) {
                            navigator.get().onResetPasswordComplete(username);
                        }
                    }

                    @Override
                    public void onFailedRegister() {
                        if (navigator.get() != null) {
                            navigator.get().notMatchConfirmPassword();
                        }
                    }
                });
            }
        } else {
            if (navigator.get() != null) {
                navigator.get().notValidPasswordExpression();
            }
        }
    }
}
