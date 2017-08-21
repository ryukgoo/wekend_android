package com.entuition.wekend.model.authentication;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;
import com.entuition.clientsdk.model.GetTokenResponseModel;
import com.entuition.clientsdk.model.RegisterRequestModel;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.authentication.asynctask.AuthenticationTask;
import com.entuition.wekend.model.authentication.asynctask.IAuthenticationCallback;
import com.entuition.wekend.model.authentication.asynctask.RegisterUserTask;
import com.entuition.wekend.model.data.user.UserInfo;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    private static final String TAG = "DeveloperAuthenticationProvider";

    private static final String DEVELOPER_PROVIDER_NAME = Constants.DEVELOPER_PROVIDER_NAME;
    private static final String DEVELOPER_APP_NAME = Constants.APP_NAME;

    private static CognitoDeveloperAuthenticationClient devAuthClient;

    public DeveloperAuthenticationProvider(String accountId, String identityPoolId, Context context, Regions region) {
        super(accountId, identityPoolId, region);

        devAuthClient = new CognitoDeveloperAuthenticationClient(context, DEVELOPER_APP_NAME);
    }

    @Override
    public String refresh() {

        Log.d(TAG, "refresh!!");

        setToken(null);

        if (getProviderName() != null && !this.loginsMap.isEmpty() && this.loginsMap.containsKey(getProviderName())) {

            Log.d(TAG, "refresh > enter if !!!!");

            GetTokenResponseModel getTokenResponse = devAuthClient.getToken(this.loginsMap, identityId);

            try {
                update(getTokenResponse.getIdentityId(), getTokenResponse.getToken());
                return getTokenResponse.getToken();
            } catch (NullPointerException e) {
                // TODO : nullpointerException > show alert dialog.....
                return null;
            }
        } else {

            Log.d(TAG, "refresh > enter else !!");

            this.getIdentityId();
            return null;
        }
    }

    @Override
    public String getIdentityId() {
        identityId = CognitoSyncClientManager.getCredentialsProvider().getCachedIdentityId();

        if (identityId == null) {
            if (getProviderName() != null && !this.loginsMap.isEmpty() && this.loginsMap.containsKey(getProviderName())) {
                GetTokenResponseModel getTokenResponse = devAuthClient.getToken(this.loginsMap, identityId);
                update(getTokenResponse.getIdentityId(), getTokenResponse.getToken());
                return getTokenResponse.getIdentityId();
            } else {
                return super.getIdentityId();
            }
        } else {
            return identityId;
        }
    }

    @Override
    public String getProviderName() {
        return DEVELOPER_PROVIDER_NAME;
    }

    /**
     * register user by asyncTask
     * @param username
     * @param password
     * @param context
     */
    public void register(String username, String password, String nickname, String gender, int birth, String phone, Context context, IAuthenticationCallback callback) {
        RegisterRequestModel requestModel = new RegisterRequestModel();
        requestModel.setUsername(username);
        requestModel.setPassword(password);
        requestModel.setNickname(nickname);
        requestModel.setGender(gender);
        requestModel.setBirth(birth);
        requestModel.setPhone(phone);

        RegisterUserTask task = new RegisterUserTask(context);
        task.setCallback(callback);
        task.execute(requestModel);
    }

    /**
     * login user by asyncTask
     * @param username
     * @param password
     * @param context
     */
    public void login(String username, String password, Context context, IAuthenticationCallback callback) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setHashedPassword(password);

        AuthenticationTask task = new AuthenticationTask(context);
        task.setCallback(callback);
        task.execute(userInfo);
    }

    public static CognitoDeveloperAuthenticationClient getDevAuthClientInstance() {
        if (devAuthClient == null) {
            throw new IllegalStateException("Dev Auth Client not initialized yet");
        }
        return devAuthClient;
    }
}
