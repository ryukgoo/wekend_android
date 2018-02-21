package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 12. 20..
 */

public class SettingEditProfileViewModel extends AbstractViewModel {

    public static final String TAG = SettingEditProfileViewModel.class.getSimpleName();

    private static final int INTRODUCE_MAX_COUNT = 200;

    public final ObservableBoolean isVaildPhone = new ObservableBoolean();
    public final ObservableBoolean isValidCode = new ObservableBoolean();
    public final ObservableBoolean isShowEditPhone = new ObservableBoolean();

    public final ObservableField<UserInfo> user = new ObservableField<>();
    public final ObservableField<String> introduce = new ObservableField<>();
    public final ObservableField<String> company = new ObservableField<>();
    public final ObservableField<String> school = new ObservableField<>();
    public final ObservableField<String> area = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();
    public final ObservableField<String> code = new ObservableField<>();

    private final UserInfoDataSource userInfoDataSource;
    private final WeakReference<SettingEditProfileNavigator> navigator;

    public String userId;

    public SettingEditProfileViewModel(Context context,
                                       SettingEditProfileNavigator navigator,
                                       UserInfoDataSource userInfoDataSource) {
        super(context);
        this.userInfoDataSource = userInfoDataSource;
        this.navigator = new WeakReference<SettingEditProfileNavigator>(navigator);

        this.user.set(new UserInfo());

        isVaildPhone.set(false);
        isValidCode.set(false);
        isShowEditPhone.set(false);
    }

    @Override
    public void onCreate() {
        userId = userInfoDataSource.getUserId();
        userInfoDataSource.getUserInfo(userId, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo result) {
                user.set(result);
                introduce.set(result.getIntroduce());
                company.set(result.getCompany());
                school.set(result.getSchool());
                area.set(result.getArea());
                phone.set(result.getPhone());
            }

            @Override
            public void onDataNotAvailable() {}
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void editDone() {
        user.get().setIntroduce(introduce.get());
        user.get().setCompany(company.get());
        user.get().setSchool(school.get());
        user.get().setArea(area.get());

        userInfoDataSource.updateUserInfo(user.get(), new UserInfoDataSource.UpdateUserInfoCallback() {
            @Override
            public void onUpdateComplete(UserInfo userInfo) {
                user.set(userInfo);
                if (navigator.get() != null) {
                    navigator.get().dismiss();
                }
            }

            @Override
            public void onUpdateFailed() {
                if (navigator.get() != null) {
                    navigator.get().showUpdateError();
                }
            }
        });
    }

    public void onFocusPhone(boolean focus) {

        Log.d(TAG, "onFocusPhone > focus :" + focus);

        if (focus) {
            isShowEditPhone.set(true);
        }
    }

    public void onChangePhone() {
        isVaildPhone.set(!TextUtils.isNullorEmptyString(phone.get()) && TextUtils.isValidPhoneNumberExpression(phone.get()));
    }

    public void onChangeCode() {
        isValidCode.set(!TextUtils.isNullorEmptyString(code.get()) && code.get().length() == 6);
    }

    public void onChangeIntroduce() {
        if (introduce.get() != null) {
            if (introduce.get().length() >= INTRODUCE_MAX_COUNT) {
                if (navigator.get() != null) navigator.get().showTooLongIntroduce();
            }
        }
    }

    public void onClickRequestCode(View view) {
        userInfoDataSource.requestVerificationCode(phone.get(), new UserInfoDataSource.RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                isVaildPhone.set(false);
                if (navigator.get() != null) navigator.get().showRequestCode();
            }

            @Override
            public void onFailedRequest() {
                if (navigator.get() != null) navigator.get().showRequestCodeFailed();
            }
        });
    }

    public void onClickValidateCode(View view) {
        if (userInfoDataSource.validateVerificationCode(code.get())) {
            user.get().setPhone(phone.get());
            userInfoDataSource.updateUserInfo(user.get(), new UserInfoDataSource.UpdateUserInfoCallback() {
                @Override
                public void onUpdateComplete(UserInfo userInfo) {
                    isValidCode.set(false);
                    code.set("");
                    if (navigator.get() != null) navigator.get().showEditPhoneComplete();
                }

                @Override
                public void onUpdateFailed() {
                    if (navigator.get() != null) navigator.get().showEditPhoneFailed();
                }
            });
        } else {
            if (navigator.get() != null) navigator.get().showInvalidCode();
        }
    }
}
