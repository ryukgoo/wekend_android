package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

/**
 * Created by ryukgoo on 2018. 2. 12..
 */

public class FindAccountViewModel extends AbstractViewModel {

    private static final String TAG = FindAccountViewModel.class.getSimpleName();

    public final ObservableBoolean isValidPhone = new ObservableBoolean();
    public final ObservableBoolean isValidCode = new ObservableBoolean();
    public final ObservableField<String> code = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();

    private final UserInfoDataSource userInfoDataSource;

    public FindAccountViewModel(Context context, UserInfoDataSource userInfoDataSource) {
        super(context);
        this.userInfoDataSource = userInfoDataSource;

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

    public void onChangePhoneText() {
        isValidPhone.set(!TextUtils.isNullorEmptyString(phone.get()) && TextUtils.isValidPhoneNumberExpression(phone.get()));
    }

    public void onChangeCodeText() {
        isValidCode.set(!TextUtils.isNullorEmptyString(code.get()) && code.get().length() == 6);
    }

    public void onClickRequestCode(View view) {
        userInfoDataSource.requestVerificationCode(phone.get(), new UserInfoDataSource.RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                isValidPhone.set(false);
            }

            @Override
            public void onFailedRequest() {

            }
        });
    }

    public void onClickValidateCode(View view) {
        if (userInfoDataSource.validateVerificationCode(code.get())) {

        } else {

        }
    }

}
