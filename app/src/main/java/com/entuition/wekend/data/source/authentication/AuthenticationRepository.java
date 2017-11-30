package com.entuition.wekend.data.source.authentication;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.entuition.clientsdk.model.LoginResponseModel;
import com.entuition.clientsdk.model.RegisterRequestModel;
import com.entuition.clientsdk.model.RegisterResponseModel;
import com.entuition.wekend.data.CognitoSyncClientManager;
import com.entuition.wekend.data.DeveloperAuthenticationProvider;
import com.entuition.wekend.data.source.userinfo.UserInfo;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public class AuthenticationRepository implements AuthenticationDataSource {

    private static final String TAG = AuthenticationRepository.class.getSimpleName();

    private static AuthenticationRepository INSTANCE = null;

    public static AuthenticationRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (AuthenticationRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthenticationRepository();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final String RESPONSE_SUCCESS = "success";
    private static final String RESPONSE_FAILED = "failed";

    private AuthenticationRepository() {}

    @Override
    public void login(@NonNull String username, @NonNull String password, @NonNull LoginCallback callback) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setHashedPassword(password);

        new LoginTask(callback).execute(userInfo);
    }

    @Override
    public void register(@NonNull String username, @NonNull String password, @NonNull String nickname, @NonNull String gender, int birth, @NonNull String phone, @NonNull RegisterCallback callback) {
        RegisterRequestModel registerRequestModel = new RegisterRequestModel();
        registerRequestModel.setUsername(username);
        registerRequestModel.setPassword(password);
        registerRequestModel.setNickname(nickname);
        registerRequestModel.setGender(gender);
        registerRequestModel.setBirth(birth);
        registerRequestModel.setPhone(phone);

        new RegisterTask(callback).execute(registerRequestModel);
    }

    @Override
    public void getToken(@NonNull GetTokenCallback callback) {
        String username = DeveloperAuthenticationProvider.getDevAuthClientInstance().getUsernameFromDevice();

        if (username != null) {
            new GetTokenTask(callback).execute(username);
        } else {
            callback.onFailedGetToken();
        }
    }

    private static class GetTokenTask extends AsyncTask<String, Void, String> {

        private final GetTokenCallback callback;

        GetTokenTask(GetTokenCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {
            DeveloperAuthenticationProvider authenticationProvider =
                    (DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider();
            CognitoSyncClientManager.addLogins(authenticationProvider.getProviderName(), strings[0]);
            return authenticationProvider.refresh();
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                callback.onFailedGetToken();
            } else {
                callback.onCompleteGetToken();
            }
        }
    }

    private static class LoginTask extends AsyncTask<UserInfo, Void, LoginResponseModel> {

        private final LoginCallback callback;
        private String username;

        LoginTask(LoginCallback callback) {
            this.callback = callback;
        }

        @Override
        protected LoginResponseModel doInBackground(UserInfo... userInfos) {
            UserInfo userInfo = userInfos[0];
            username = userInfo.getUsername();
            return DeveloperAuthenticationProvider.getDevAuthClientInstance()
                    .login(userInfo.getUsername(), userInfo.getHashedPassword());
        }

        @Override
        protected void onPostExecute(LoginResponseModel result) {
            if (result != null && result.getEnable() != null && result.getEnable().equals("true")) {
                new GetTokenTask(new GetTokenCallback() {
                    @Override
                    public void onCompleteGetToken() {
                        callback.onCompleteLogin();
                    }

                    @Override
                    public void onFailedGetToken() {
                        callback.onExpiredToken();
                    }
                }).execute(username);
            } else {
                callback.onFailedLogin();
            }
        }
    }

    private static class RegisterTask extends AsyncTask<RegisterRequestModel, Void, RegisterResponseModel> {

        private final RegisterCallback callback;

        RegisterTask(RegisterCallback callback) {
            this.callback = callback;
        }

        @Override
        protected RegisterResponseModel doInBackground(RegisterRequestModel... registerRequestModels) {
            return DeveloperAuthenticationProvider.getDevAuthClientInstance().register(registerRequestModels[0]);
        }

        @Override
        protected void onPostExecute(RegisterResponseModel result) {
            if (result == null || result.getResult().equals(RESPONSE_FAILED)) {
                callback.onFailedRegister();
            } else {
                callback.onCompleteRegister();
            }
        }
    }

}
