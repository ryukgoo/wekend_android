package com.entuition.wekend.controller;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
public class CognitoSyncClientManager {

    private static String TAG = "CognitoSyncClientManager";

    private static final String ACCOUNT_ID = Constants.ACCOUNT_ID;
    private static final String IDENTITY_POOL_ID = Constants.IDENTITY_POOL_ID;
    private static final Regions REGION = Constants.REGIONS;

    private static CognitoSyncManager syncClient;
    protected static CognitoCachingCredentialsProvider credentialsProvider = null;
    protected static AWSAbstractCognitoIdentityProvider developerIdentityProvider;

    private static AmazonDynamoDBClient sDdbClient = null;
    private static AmazonS3Client sS3Client = null;
    private static TransferUtility sTranferUtility = null;

    public static void init(Context context) {

        Log.d(TAG, "CognitoSyncClientManager initialize");
        if (syncClient != null) return;

        developerIdentityProvider = new DeveloperAuthenticationProvider(ACCOUNT_ID, IDENTITY_POOL_ID, context, REGION);
        credentialsProvider = new CognitoCachingCredentialsProvider(context, developerIdentityProvider, REGION);
        syncClient = new CognitoSyncManager(context, REGION, credentialsProvider);
    }

    /**
     * Set the logins
     * should call it in a background thread.
     * @param providerName the name of the external identity provider
     * @param token openId token
     */
    public static void addLogins(String providerName, String token) {

        Log.d(TAG, "addLogins!!");

        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }

        Map<String, String> logins = credentialsProvider.getLogins();
        if (logins == null) {
            logins = new HashMap<String, String>();
        }
        logins.put(providerName, token);
        credentialsProvider.setLogins(logins);
    }

    /**
     * get CognitoSyncManager instance
     * @return CognitoSyncManager instance
     */
    public static CognitoSyncManager getInstance() {
        if (syncClient == null) {
            throw new IllegalStateException("CognitoSyncClientManager not initialized yet");
        }
        return syncClient;
    }

    /**
     * get CognitoCredentialProvider instance
     * @return CognitoCachingCredentialsProvider instance
     */
    public static CognitoCachingCredentialsProvider getCredentialsProvider() { return credentialsProvider; }

    /**
     * get DynamoDBClient
     * @return AmazonDynamoDBClient instance
     */
    public static AmazonDynamoDBClient getDynamoDBClient() {
        if (sDdbClient == null) {
            sDdbClient = new AmazonDynamoDBClient(credentialsProvider);
            sDdbClient.setRegion(Region.getRegion(REGION));
        }
        return sDdbClient;
    }

    /**
     * get S3Client
     * @return AmazonS3Client instance
     */
    public static AmazonS3Client getS3Client() {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(credentialsProvider);
            sS3Client.setRegion(Region.getRegion(REGION));
        }
        return sS3Client;
    }

    /**
     * get TransferUtility
     * @param context
     * @return TransferUtility instance
     */
    public static TransferUtility getTranferUtility(Context context) {
        if (sTranferUtility == null) {
            sTranferUtility = new TransferUtility(getS3Client(), context.getApplicationContext());
        }
        return sTranferUtility;
    }
}
