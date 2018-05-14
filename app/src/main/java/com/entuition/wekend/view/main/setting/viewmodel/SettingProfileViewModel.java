package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;

import com.entuition.wekend.R;
import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerListener;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 11. 1..
 */

public class SettingProfileViewModel extends AbstractViewModel implements ProfileViewPagerListener {

    public static final String TAG = SettingProfileViewModel.class.getSimpleName();

    public final ObservableField<UserInfo> user = new ObservableField<>();
    public final ObservableField<String> subscription = new ObservableField<>();

    private final UserInfoDataSource userInfoDataSource;
    private final WeakReference<SettingProfileNavigator> navigator;

    public SettingProfileViewModel(Context context,
                                   SettingProfileNavigator navigator,
                                   UserInfoDataSource userInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<SettingProfileNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onResume() {
        loadUserInfo();
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onClickPagerItem(String photo) {}

    public void onClickEditButton() {
        if (navigator.get() != null) navigator.get().gotoEditProfileView();
    }

    private void loadUserInfo() {
        final String userId = userInfoDataSource.getUserId();
        userInfoDataSource.getUserInfo(userId, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo result) {
                user.set(result);
            }

            @Override
            public void onDataNotAvailable() {}

            @Override
            public void onError() {}
        });

        GoogleBillingController.getInstance(getApplication()).checkSubcribing(new GoogleBillingController.OnValidateSubcribe() {
            @Override
            public void onValidateSubcribed(boolean isSubcribed, UserInfo userInfo) {
                if (isSubcribed) {
                    subscription.set(getApplication().getString(R.string.subscription_enabled));
                } else {
                    subscription.set("");
                }
            }
        });
    }
}
