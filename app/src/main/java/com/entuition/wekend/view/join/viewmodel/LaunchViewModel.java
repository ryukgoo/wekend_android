package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.entuition.wekend.data.source.authentication.AuthenticationDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public class LaunchViewModel extends AbstractViewModel {

    private static final String TAG = LaunchViewModel.class.getSimpleName();

    private final WeakReference<LaunchNavigator> navigator;
    private final AuthenticationDataSource authenticationDataSource;
    private final UserInfoDataSource userInfoDataSource;

    @Nullable
    String linkProductId;
    int notificationType = 0;

    public LaunchViewModel(Context context, LaunchNavigator navigator,
                           AuthenticationDataSource authenticationDataSource,
                           UserInfoDataSource userInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<LaunchNavigator>(navigator);
        this.authenticationDataSource = authenticationDataSource;
        this.userInfoDataSource = userInfoDataSource;
    }

    @Override
    public void onCreate() {
        authenticationDataSource.getToken(new AuthenticationDataSource.GetTokenCallback() {
            @Override
            public void onCompleteGetToken() {

                userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
                    @Override
                    public void onUserInfoLoaded(UserInfo userInfo) {
                        if (linkProductId == null) {
                            if (navigator.get() != null) {
                                navigator.get().onAutoLogin(notificationType);
                            }
                        } else {
                            if (navigator.get() != null) {
                                navigator.get().onReceiveLink(Integer.parseInt(linkProductId));
                            }
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        if (navigator.get() != null) {
                            navigator.get().onLoginView();
                        }
                    }

                    @Override
                    public void onError() {}
                });
            }

            @Override
            public void onFailedGetToken() {
                if (navigator.get() != null) {
                    navigator.get().onLoginView();
                }
            }
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void parseIntent(Intent intent) {
        notificationType = intent.getIntExtra(Constants.START_ACTIVITY_POSITION, 0);
        linkProductId = Utilities.getLinkIdFromIntent(getApplication(), intent);
    }
}
