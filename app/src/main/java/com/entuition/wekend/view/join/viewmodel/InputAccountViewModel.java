package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public class InputAccountViewModel extends AbstractViewModel {

    public static final String TAG = InputAccountViewModel.class.getSimpleName();

    public final ObservableField<String> email = new ObservableField<>();
    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<String> passwordConfirm = new ObservableField<>();
    public final ObservableBoolean isValidInputs = new ObservableBoolean();

    private final WeakReference<InputAccountNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;

    public InputAccountViewModel(Context context, InputAccountNavigator navigator, UserInfoDataSource userInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<InputAccountNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
    }

    @Override
    public void onCreate() {
        isValidInputs.set(false);
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onChangeEmailText() {
        isValidInputs.set(isValidInput());
    }

    public void onChangePasswordText() {
        isValidInputs.set(isValidInput());
    }

    public void onClickButton(View view) {
        if (TextUtils.isNullorEmptyString(passwordConfirm.get()) ||
                !password.get().equals(passwordConfirm.get())) {
            navigator.get().showNotEqualConfirm();
        } else {
            userInfoDataSource.searchUserInfoFromUsername(email.get(), new UserInfoDataSource.GetUserInfoCallback() {
                @Override
                public void onUserInfoLoaded(UserInfo userInfo) {
                    navigator.get().showDuplicatedEmail();
                }

                @Override
                public void onDataNotAvailable() {
                    navigator.get().gotoInputUserInfo(email.get(), password.get());
                }
            });
        }
    }

    private boolean isValidInput() {
        boolean isValidEmail = !TextUtils.isNullorEmptyString(email.get()) && TextUtils.isValidEmailExpression(email.get());
        boolean isValidPassword = !TextUtils.isNullorEmptyString(password.get()) && TextUtils.isValidPasswordExpression(password.get());
        return isValidEmail && isValidPassword;
    }
}
