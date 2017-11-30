package com.entuition.wekend.data.source.authentication;

import android.support.annotation.NonNull;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public interface AuthenticationDataSource {

    interface LoginCallback {
        void onCompleteLogin();
        void onFailedLogin();
        void onExpiredToken();
    }

    interface RegisterCallback {
        void onCompleteRegister();
        void onFailedRegister();
    }

    interface GetTokenCallback {
        void onCompleteGetToken();
        void onFailedGetToken();
    }

    void login(@NonNull String username, @NonNull String password, @NonNull LoginCallback callback);

    void register(@NonNull String username, @NonNull String password,
                  @NonNull String nickname, @NonNull String gender,
                  int birth, @NonNull String phone, @NonNull RegisterCallback callback);

    void getToken(@NonNull GetTokenCallback callback);

}
