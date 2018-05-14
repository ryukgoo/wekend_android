package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2018. 2. 12..
 */

public class ConfirmPhoneViewModel extends AbstractViewModel {

    private static final String TAG = ConfirmPhoneViewModel.class.getSimpleName();

    public final ObservableBoolean isValidPhone = new ObservableBoolean();
    public final ObservableBoolean isValidCode = new ObservableBoolean();
    public final ObservableField<String> code = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();

    private final UserInfoDataSource userInfoDataSource;
    private final WeakReference<ConfirmPhoneNavigator> navigator;

    private String registeredPhone;
    private String registeredUsername;

    public ConfirmPhoneViewModel(Context context, ConfirmPhoneNavigator navigator, UserInfoDataSource userInfoDataSource) {
        super(context);
        this.userInfoDataSource = userInfoDataSource;
        this.navigator = new WeakReference<ConfirmPhoneNavigator>(navigator);

        isValidPhone.set(false);
        isValidCode.set(false);
    }

    @Override
    public void onCreate() {
        isValidPhone.set(false);
        isValidCode.set(false);
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void setUserInfo(String username, String phone) {
        registeredUsername = username;
        registeredPhone = phone;
    }

    public void onChangePhoneText() {
        isValidPhone.set(!TextUtils.isNullorEmptyString(phone.get()) && TextUtils.isValidPhoneNumberExpression(phone.get()));
    }

    public void onChangeCodeText() {
        isValidCode.set(!TextUtils.isNullorEmptyString(code.get()) && code.get().length() == 6);
    }

    public void onClickRequestCode(View view) {

        if (registeredPhone != null) {
            if (registeredPhone.equals(phone.get())) {
                requestCode();
            } else {
                if (navigator.get() != null) { navigator.get().notMatchRegisteredPhone(); }
            }
        } else {
            requestCode();
        }
    }

    public void onClickValidateCode(View view) {
        if (userInfoDataSource.validateVerificationCode(code.get())) {
            if (registeredUsername == null) {
                userInfoDataSource.searchUserInfoByPhone(phone.get(), new UserInfoDataSource.GetUserInfoCallback() {
                    @Override
                    public void onUserInfoLoaded(UserInfo userInfo) {
                        if (navigator.get() != null) {
                            navigator.get().onConfirmCode(userInfo.getUserId(), userInfo.getUsername());
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        if (navigator.get() != null) {
                            navigator.get().onNotFoundAccount();
                        }
                    }

                    @Override
                    public void onError() {}
                });
            } else {
                userInfoDataSource.searchUserInfoByUsername(registeredUsername, new UserInfoDataSource.GetUserInfoCallback() {
                    @Override
                    public void onUserInfoLoaded(UserInfo userInfo) {
                        if (navigator.get() != null) {
                            navigator.get().onResetPassword(userInfo.getUserId(), userInfo.getUsername());
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        if (navigator.get() != null) {
                            navigator.get().onNotFoundAccount();
                        }
                    }

                    @Override
                    public void onError() {}
                });
            }
        } else {
            if (navigator.get() != null) {
                navigator.get().onConfirmCodeFailed();
            }
        }
    }

    private void requestCode() {
        userInfoDataSource.requestVerificationCode(phone.get(), new UserInfoDataSource.RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                isValidPhone.set(false);
                if (navigator.get() != null) {
                    navigator.get().onRequestComplete();
                }
            }

            @Override
            public void onFailedRequest() {
                if (navigator.get() != null) {
                    navigator.get().onRequestFailed();
                }
            }
        });
    }
}
