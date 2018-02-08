package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.data.DeveloperAuthenticationProvider;
import com.entuition.wekend.data.source.authentication.AuthenticationDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class LoginViewModel extends AbstractViewModel {

    private static final String TAG = LoginViewModel.class.getSimpleName();

    public final ObservableField<String> email = new ObservableField<>();
    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableBoolean isLoginButtonValid = new ObservableBoolean();
    public final ObservableBoolean isLoading = new ObservableBoolean();

    private final WeakReference<LoginNavigator> navigator;
    private final AuthenticationDataSource authenticationDataSource;
    private final UserInfoDataSource userInfoDataSource;

    public LoginViewModel(Context context, LoginNavigator navigator,
                          AuthenticationDataSource authenticationDataSource,
                          UserInfoDataSource userInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<LoginNavigator>(navigator);
        this.authenticationDataSource = authenticationDataSource;
        this.userInfoDataSource = userInfoDataSource;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        isLoading.set(false);
        isLoginButtonValid.set(false);
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void logout() {
        DeveloperAuthenticationProvider.getDevAuthClientInstance().logout();
    }

    public void onClickLoginButton(View view) {
        Log.d(TAG, "onClickLoginButton");

        if (isValidInput()) {
            isLoading.set(true);
            authenticationDataSource.login(email.get(), password.get(), new AuthenticationDataSource.LoginCallback() {
                @Override
                public void onCompleteLogin() {

                    userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
                        @Override
                        public void onUserInfoLoaded(UserInfo userInfo) {
                            isLoading.set(false);
                            if (navigator.get() != null) {
                                navigator.get().onCompleteLogin();
                            }
                        }

                        @Override
                        public void onDataNotAvailable() {
                            isLoading.set(false);
                            if (navigator.get() != null) {
                                navigator.get().showFailedLogin();
                            }
                        }
                    });
                }

                @Override
                public void onFailedLogin() {
                    isLoading.set(false);
                    if (navigator.get() != null) {
                        navigator.get().showFailedLogin();
                    }
                }

                @Override
                public void onExpiredToken() {
                    isLoading.set(false);
                    if (navigator.get() != null) {
                        navigator.get().showExpiredToken();
                    }
                }
            });
        } else {
            isLoading.set(false);
            if (navigator.get() != null) {
                navigator.get().showInvalidInput();
            }
        }
    }

    public void onChangeEmailText() {
        isLoginButtonValid.set(isValidInput());
    }

    public void onChangePasswordText() {
        isLoginButtonValid.set(isValidInput());
    }

    protected boolean isValidInput() {
        boolean isValidEmail = !TextUtils.isNullorEmptyString(email.get()) && TextUtils.isValidEmailExpression(email.get());
        boolean isValidPassword = !TextUtils.isNullorEmptyString(password.get()) && TextUtils.isValidPasswordExpression(password.get());
        return isValidEmail && isValidPassword;
    }
}
