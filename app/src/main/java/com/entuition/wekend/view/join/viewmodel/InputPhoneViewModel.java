package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.data.DeveloperAuthenticationProvider;
import com.entuition.wekend.data.source.authentication.AuthenticationDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 27..
 */

public class InputPhoneViewModel extends AbstractViewModel {

    public static final String TAG = InputPhoneViewModel.class.getSimpleName();

    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableField<String> phone = new ObservableField<>();
    public final ObservableBoolean isValidPhone = new ObservableBoolean();
    public final ObservableField<String> code = new ObservableField<>();
    public final ObservableBoolean isValidCode = new ObservableBoolean();

    private final UserInfoDataSource userInfoDataSource;
    private final AuthenticationDataSource authenticationDataSource;
    private final WeakReference<InputPhoneNavigator> navigator;

    private String username;
    private String password;
    private String nickname;
    private String gender;
    private int birth;
    private String requestedPhone;

    public InputPhoneViewModel(Context context, InputPhoneNavigator navigator,
                               AuthenticationDataSource authenticationDataSource, UserInfoDataSource userInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<InputPhoneNavigator>(navigator);
        this.authenticationDataSource = authenticationDataSource;
        this.userInfoDataSource = userInfoDataSource;
    }

    @Override
    public void onCreate() {
        isLoading.set(false);
        isValidPhone.set(false);
        isValidCode.set(false);
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onChangePhoneText() {
        isValidPhone.set(!TextUtils.isNullorEmptyString(phone.get()) && TextUtils.isValidPhoneNumberExpression(phone.get()));
    }

    public void onChangeCodeText() {
        isValidCode.set(!TextUtils.isNullorEmptyString(code.get()) && code.get().length() == 6);
    }

    public void onClickRequestCode(View view) {
        Log.d(TAG, "onClickRequestCode");

        requestedPhone = phone.get();

        userInfoDataSource.requestVerificationCode(phone.get(), new UserInfoDataSource.RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                isValidPhone.set(false);
                if (navigator.get() != null) {
                    navigator.get().showRequestCode();
                }
            }

            @Override
            public void onFailedRequest() {
                if (navigator.get() != null) {
                    navigator.get().showRequestCodeFailed();
                }
            }
        });
    }

    public void onClickValidateCode(View view) {
        Log.d(TAG, "onClickValidateCode");
        if (userInfoDataSource.validateVerificationCode(code.get())) {
            if (navigator.get() != null) {
                navigator.get().showStartRegister();
            }
            authenticationDataSource.register(username, password, nickname, gender, birth, requestedPhone,
                    new AuthenticationDataSource.RegisterCallback() {
                @Override
                public void onCompleteRegister() {
                    authenticationDataSource.login(username, password, new AuthenticationDataSource.LoginCallback() {
                        @Override
                        public void onCompleteLogin() {

                            userInfoDataSource.registerEndpointArn(null, null);

                            if (navigator.get() != null) {
                                navigator.get().showStopRegister();
                                navigator.get().onCompleteLogin();
                            }
                        }

                        @Override
                        public void onFailedLogin() {
                            if (navigator.get() != null) {
                                navigator.get().showStopRegister();
                                navigator.get().showFailedLogin();
                            }
                        }

                        @Override
                        public void onExpiredToken() {
                            if (navigator.get() != null) {
                                navigator.get().showStopRegister();
                                navigator.get().showExpiredToken();
                            }
                        }
                    });
                }

                @Override
                public void onFailedRegister() {
                    if (navigator.get() != null) {
                        navigator.get().showStopRegister();
                        navigator.get().showRegisterFailed();
                    }
                }
            });

        } else {
            if (navigator.get() != null) navigator.get().showInvalidCode();
        }
    }

    public void setInfoFromIntent(Intent intent) {
        username = intent.getStringExtra(Constants.ExtraKeys.USERNAME);
        password = intent.getStringExtra(Constants.ExtraKeys.PASSWORD);
        nickname = intent.getStringExtra(Constants.ExtraKeys.NICKNAME);
        gender = intent.getStringExtra(Constants.ExtraKeys.GENDER);
        birth = intent.getIntExtra(Constants.ExtraKeys.BIRTH, UserInfo.DEFAULT_BIRTH_VALUE);
    }

    public void logout() {
        DeveloperAuthenticationProvider.getDevAuthClientInstance().logout();
    }
}
