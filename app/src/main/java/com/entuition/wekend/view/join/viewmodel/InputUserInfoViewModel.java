package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 27..
 */

public class InputUserInfoViewModel extends AbstractViewModel {

    public static final String TAG = InputUserInfoViewModel.class.getSimpleName();

    public final ObservableField<String> nickname = new ObservableField<>();
    public final ObservableBoolean isValidNickname = new ObservableBoolean();
    public final ObservableBoolean isSelectedMale = new ObservableBoolean();
    public final ObservableBoolean isSelectedFemale = new ObservableBoolean();
    public final ObservableBoolean isLoading = new ObservableBoolean();

    private final UserInfoDataSource dataSource;
    private final WeakReference<InputUserInfoNavigator> navigator;

    private boolean isAvailableNickname = false;

    public InputUserInfoViewModel(Context context, InputUserInfoNavigator navigator, UserInfoDataSource dataSource) {
        super(context);
        this.dataSource = dataSource;
        this.navigator = new WeakReference<InputUserInfoNavigator>(navigator);
    }

    @Override
    public void onCreate() {
        isLoading.set(false);
        isValidNickname.set(false);
        isSelectedMale.set(true);
        isSelectedFemale.set(false);
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onChangeNicknameText() {
        isValidNickname.set(TextUtils.isValidNicknameExpression(nickname.get()));
    }

    public void onClickCheckNicknameButton(View view) {
        Log.d(TAG, "onClickCheckNicknameButton");

        if (navigator.get() != null) {
            navigator.get().hideKeyboard();
        }

        isLoading.set(true);
        dataSource.searchUserInfoFromNickname(nickname.get(), new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                isLoading.set(false);
                isAvailableNickname = false;
                if (navigator.get() != null) {
                    navigator.get().showDuplicatedNickname();
                }
            }

            @Override
            public void onDataNotAvailable() {
                isLoading.set(false);
                isAvailableNickname = true;
                if (navigator.get() != null) {
                    navigator.get().showAvailableNickname();
                }
            }
        });
    }

    public void onClickMaleButton(View view) {
        isSelectedMale.set(true);
        isSelectedFemale.set(false);
    }

    public void onClickFemaleButton(View view) {
        isSelectedFemale.set(true);
        isSelectedMale.set(false);
    }

    public void onClickNextButton(View view) {
        Log.d(TAG, "onClickNextButton");

        if (!isAvailableNickname) {
            if (navigator.get() != null) {
                navigator.get().showInvalidNickname();
            }
        } else {
            String gender = isSelectedMale.get() ? Constants.GenderValue.male.toString() : Constants.GenderValue.female.toString();
            if (navigator.get() != null) {
                navigator.get().onClickNextButton(nickname.get(), gender);
            }
        }
    }
}
