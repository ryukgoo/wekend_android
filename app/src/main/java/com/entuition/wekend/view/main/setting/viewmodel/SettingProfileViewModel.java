package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerListener;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 11. 1..
 */

public class SettingProfileViewModel extends AbstractViewModel implements ProfileViewPagerListener {

    public static final String TAG = SettingProfileViewModel.class.getSimpleName();

    public final ObservableBoolean isEditMode = new ObservableBoolean();
    public final ObservableBoolean isVaildPhone = new ObservableBoolean();
    public final ObservableBoolean isValidCode = new ObservableBoolean();

    public final ObservableField<String> nickname = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();
    public final ObservableArrayList<String> photos = new ObservableArrayList<>();
    public final ObservableField<Integer> point = new ObservableField<>();
    public final ObservableField<String> code = new ObservableField<>();

    public int birth;

    private final UserInfoDataSource userInfoDataSource;
    private final WeakReference<SettingProfileNavigator> navigator;

    private UserInfo userInfo;

    public SettingProfileViewModel(Context context, SettingProfileNavigator navigator, UserInfoDataSource userInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<SettingProfileNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;

        isEditMode.set(false);
        isVaildPhone.set(false);
        isValidCode.set(false);
    }

    @Override
    public void onCreate() {
        final String userId = userInfoDataSource.getUserId();
        userInfoDataSource.getUserInfo(userId, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo result) {
                userInfo = result;
                nickname.set(result.getNickname());
                birth = result.getBirth();
                phone.set(result.getPhone());
                point.set(result.getBalloon());
                if (result.getPhotos() != null) photos.addAll(result.getPhotos());
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

    @Override
    public void onClickPagerItem(String photo) {

    }

    public void onClickButton(View view) {
        isEditMode.set(!isEditMode.get());

        if (!isEditMode.get()) {
            phone.set(userInfo.getPhone());
        }
    }

    public void onClickRequestCode(View view) {
        userInfoDataSource.requestVerificationCode(phone.get(), new UserInfoDataSource.RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                isVaildPhone.set(false);
                navigator.get().showRequestCode();
            }

            @Override
            public void onFailedRequest() {
                navigator.get().showRequestCodeFailed();
            }
        });
    }

    public void onClickValidateCode(View view) {
        if (userInfoDataSource.validateVerificationCode(code.get())) {
            userInfo.setPhone(phone.get());
            userInfoDataSource.updateUserInfo(userInfo, new UserInfoDataSource.UpdateUserInfoCallback() {
                @Override
                public void onUpdateComplete(UserInfo userInfo) {
                    isValidCode.set(false);
                    code.set("");
                    navigator.get().showEditPhoneComplete();
                }

                @Override
                public void onUpdateFailed() {
                    navigator.get().showEditPhoneFailed();
                }
            });
        } else {
            navigator.get().showInvalidCode();
        }
    }

    public void onFocusPhone(boolean isFocus) {
        if (isFocus) { phone.set(""); }
    }

    public void onChangeNickname() {

    }

    public void onChangePhone() {
        isVaildPhone.set(!TextUtils.isNullorEmptyString(phone.get()) && TextUtils.isValidPhoneNumberExpression(phone.get()));
    }

    public void onChangeCode() {
        isValidCode.set(!TextUtils.isNullorEmptyString(code.get()) && code.get().length() == 6);
    }

}
