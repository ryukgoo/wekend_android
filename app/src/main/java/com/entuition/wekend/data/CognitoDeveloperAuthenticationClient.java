package com.entuition.wekend.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.apigateway.ApiClientException;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.entuition.clientsdk.AuthenticationAPIClient;
import com.entuition.clientsdk.model.GetTokenRequestModel;
import com.entuition.clientsdk.model.GetTokenResponseModel;
import com.entuition.clientsdk.model.LoginRequestModel;
import com.entuition.clientsdk.model.LoginResponseModel;
import com.entuition.clientsdk.model.RegisterRequestModel;
import com.entuition.clientsdk.model.RegisterResponseModel;
import com.entuition.wekend.data.source.like.LikeInfoRepository;
import com.entuition.wekend.data.source.mail.ReceiveMailRepository;
import com.entuition.wekend.data.source.mail.SendMailRepository;
import com.entuition.wekend.data.source.product.ProductInfoRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.join.LoginActivity;

import java.util.Map;
import java.util.UUID;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class CognitoDeveloperAuthenticationClient {

    private static final String TAG = "CognitoDeveloperAuthenticationClient";

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final String appName;

    public CognitoDeveloperAuthenticationClient(Context context, String appName) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context.getApplicationContext());
        this.appName = appName.toLowerCase();
    }

    public String getUsernameFromDevice() {
        return SharedPreferencesWrapper.getUsernameFromSharedPreferences(this.sharedPreferences);
    }

    /**
     *
     * @param requestModel
     * @return
     */
    public RegisterResponseModel register(RegisterRequestModel requestModel) {

        ApiClientFactory apiClientFactory = new ApiClientFactory();
        final AuthenticationAPIClient client = apiClientFactory.build(AuthenticationAPIClient.class);

        RegisterResponseModel result = null;

        try {
            result = client.registeruserPost(requestModel);
            Log.d(TAG, "Register response > result : " + result.getResult());
            Log.d(TAG, "Register response > userid : " + result.getUserid());
        } catch (ApiClientException e) {
            Log.e(TAG, "Failed to invoke RegisterUser", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to invoke RegisterUser", e);
        }

        if (result != null && result.getResult() != null && result.getUserid() != null) {
            SharedPreferencesWrapper.registerUserId(this.sharedPreferences, result.getUserid());
        }

        return result;
    }

    public GetTokenResponseModel getToken(Map<String, String> logins, String identityId) {
        Log.d(TAG, "getToken > logins : " + logins.toString() + ", identityId : " + identityId);

        String uid = SharedPreferencesWrapper.getUidForDevice(this.sharedPreferences);
        String key = SharedPreferencesWrapper.getKeyForDevice(this.sharedPreferences);
        String timestamp = Utilities.getTimestamp();

        StringBuilder loginString = new StringBuilder();
        for (Map.Entry<String, String> entry : logins.entrySet()) {
            loginString.append(entry.getKey() + entry.getValue());
        }

        String signature;
        if (identityId != null) {
            signature = Utilities.getSignature(timestamp + loginString + identityId, key);
        } else {
            signature = Utilities.getSignature(timestamp + loginString, key);
        }

        ApiClientFactory apiClientFactory = new ApiClientFactory();
        final AuthenticationAPIClient client = apiClientFactory.build(AuthenticationAPIClient.class);

        GetTokenRequestModel getTokenRequestModel = new GetTokenRequestModel();
        getTokenRequestModel.setUid(uid);
        getTokenRequestModel.setSignature(signature);
        getTokenRequestModel.setTimestamp(timestamp);
        getTokenRequestModel.setIdentityId(identityId);
        getTokenRequestModel.setLoginString(Utilities.mapToString(logins));

        try {
            return client.gettokenPost(getTokenRequestModel);
        } catch (ApiClientException e) {
            Log.e(TAG, "Failed to invoke GetToken", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to invoke GetToken", e);
        }

        return null;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public LoginResponseModel login(String username, String password) {
        Log.d(TAG, "username : " + username + ", password : " + password);

        LoginResponseModel responseModel = null;

        if (SharedPreferencesWrapper.getUidForDevice(this.sharedPreferences) == null ||
                SharedPreferencesWrapper.getUsernameFromSharedPreferences(this.sharedPreferences) == null ||
                SharedPreferencesWrapper.getUserIdFromSharedPreferences(this.sharedPreferences) == null) {

//            String uid = Utilities.getDeviceUUID(context);
            String uid = UUID.randomUUID().toString();
            String timestamp = Utilities.getTimestamp();
            String salt = username + this.appName;
            String decryptionKey = Utilities.getSignature(salt, password);
            String signature = Utilities.getSignature(timestamp, decryptionKey);

            ApiClientFactory apiClientFactory = new ApiClientFactory();
            final AuthenticationAPIClient client = apiClientFactory.build(AuthenticationAPIClient.class);

            LoginRequestModel requestModel = new LoginRequestModel();
            requestModel.setUsername(username);
            requestModel.setTimestamp(timestamp);
            requestModel.setSignature(signature);
            requestModel.setUid(uid);

            try {
                Log.d(TAG, "loginuserPost called");
                responseModel = client.loginuserPost(requestModel);
                Log.d(TAG, "loginuserPost finished");
            } catch (ApiClientException e) {
                Log.e(TAG, "Failed to invoke LoginUser", e);
            } catch (Exception e) {
                Log.e(TAG, "Failed to invoke LoginUser", e);
            }

            if (responseModel != null && responseModel.getEnable().equals("true")) {
                SharedPreferencesWrapper.registerDeviceId(this.sharedPreferences, uid, responseModel.getKey());
                SharedPreferencesWrapper.registerUsername(this.sharedPreferences, username);
                SharedPreferencesWrapper.registerUserId(this.sharedPreferences, responseModel.getUserid());
            }
        } else {
            responseModel = new LoginResponseModel();
            responseModel.setEnable("true");
            responseModel.setKey("");
        }

        return responseModel;
    }

    public void logout() {
        Log.d(TAG, "logout");
        CognitoSyncClientManager.getCredentialsProvider().setLogins(null);
        CognitoSyncClientManager.getCredentialsProvider().clear();

        UserInfoRepository.destroyInstance();
        ProductInfoRepository.destroyInstance();
        LikeInfoRepository.destroyInstance();
        ReceiveMailRepository.destroyInstance();
        SendMailRepository.destroyInstance();
        SharedPreferencesWrapper.wipe(this.sharedPreferences);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }
}
