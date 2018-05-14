package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.util.Log;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2018. 2. 22..
 */

public class ConfirmAccountViewModel extends AbstractViewModel {

    private static final String TAG = ConfirmAccountViewModel.class.getSimpleName();

    public final ObservableField<String> email = new ObservableField<>();
    public final ObservableBoolean isButtonEnabled = new ObservableBoolean();

    private final WeakReference<ConfirmAccountNavigator> navigator;
    private final UserInfoDataSource dataSource;

    public ConfirmAccountViewModel(Context context, ConfirmAccountNavigator navigator, UserInfoDataSource dataSource) {
        super(context);

        this.navigator = new WeakReference<ConfirmAccountNavigator>(navigator);
        this.dataSource = dataSource;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onChangeEmailText() {
        Log.d(TAG, "onChangeEmailText > text : " + email.get());
        isButtonEnabled.set(!TextUtils.isNullorEmptyString(email.get()) && TextUtils.isValidEmailExpression(email.get()));
    }

    public void onClickButton() {
        Log.d(TAG, "onClickButton");

        dataSource.searchUserInfoByUsername(email.get(), new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {

                Log.d(TAG, "userInfo > phone : " + userInfo.getPhone());

                if (navigator.get() != null) {
                    navigator.get().onConfirmAccount(userInfo);
                }
            }

            @Override
            public void onDataNotAvailable() {
                if (navigator.get() != null) {
                    navigator.get().onConfirmAccountFailed();
                }
            }

            @Override
            public void onError() {
                // TODO: old account throw exception(IllegalParameter?) -> expired token
            }
        });
    }
}
