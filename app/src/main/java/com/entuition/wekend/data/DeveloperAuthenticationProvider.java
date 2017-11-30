package com.entuition.wekend.data;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;
import com.entuition.clientsdk.model.GetTokenResponseModel;
import com.entuition.wekend.util.Constants;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {

    public static final String TAG = DeveloperAuthenticationProvider.class.getSimpleName();

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
                // TODO : nullpointerException
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

    public static CognitoDeveloperAuthenticationClient getDevAuthClientInstance() {
        if (devAuthClient == null) {
            throw new IllegalStateException("Dev Auth Client not initialized yet");
        }
        return devAuthClient;
    }
}
