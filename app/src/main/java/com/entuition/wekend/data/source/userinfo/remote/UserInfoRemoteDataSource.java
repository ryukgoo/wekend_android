package com.entuition.wekend.data.source.userinfo.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amazonaws.mobileconnectors.apigateway.ApiClientException;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.entuition.clientsdk.AuthenticationAPIClient;
import com.entuition.clientsdk.NotificationAPIClient;
import com.entuition.clientsdk.model.CreateEndpointARNRequestModel;
import com.entuition.clientsdk.model.CreateEndpointARNResponseModel;
import com.entuition.clientsdk.model.VerifyPurchaseRequest;
import com.entuition.clientsdk.model.VerifyPurchaseResponse;
import com.entuition.wekend.R;
import com.entuition.wekend.data.CognitoSyncClientManager;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.util.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.surem.api.sms.SureSMSAPI;
import com.surem.net.SendReport;
import com.surem.net.sms.SureSMSDeliveryReport;

import java.io.IOException;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class UserInfoRemoteDataSource implements UserInfoDataSource {

    private static final String TAG = UserInfoRemoteDataSource.class.getSimpleName();

    private static UserInfoRemoteDataSource INSTANCE = null;

    public static UserInfoRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserInfoRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserInfoRemoteDataSource(context);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private Context context;
    private DynamoDBMapper mapper;

    private UserInfoRemoteDataSource(Context context) {
        this.context = context;
        mapper = CognitoSyncClientManager.getDynamoDBMapper();
    }

    @Override
    public void clear() {}

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getUsernameFromDevice() { return null; }

    @Override
    public void refreshUserInfo() {

    }

    @Override
    public void getUserInfo(@NonNull String userId, GetUserInfoCallback callback) {
        new GetUserInfoTask(mapper, callback).execute(userId);
    }

    @Override
    public void searchUserInfoByNickname(@NonNull String nickname, GetUserInfoCallback callback) {

        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(nickname);

        new SearchUserInfoTask(UserInfo.Index.NICKNAME, mapper, callback).execute(userInfo);
    }

    @Override
    public void searchUserInfoByUsername(@NonNull String username, GetUserInfoCallback callback) {

        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);

        new SearchUserInfoTask(UserInfo.Index.USERNAME, mapper, callback).execute(userInfo);
    }

    @Override
    public void searchUserInfoByPhone(@NonNull String phone, GetUserInfoCallback callback) {

        Log.d(TAG, "searchUserInfoByPhone > phone : " + phone);

        UserInfo userInfo = new UserInfo();
        userInfo.setPhone(phone);

        new SearchUserInfoTask(UserInfo.Index.PHONE_REGISTERED_TIME, mapper, callback).execute(userInfo);
    }

    @Override
    public void updateUserInfo(@NonNull UserInfo userInfo, UpdateUserInfoCallback callback) {
        new UpdateUserInfoTask(mapper, callback).execute(userInfo);
    }

    @Override
    public void purchasePoint(int point, @NonNull UpdateUserInfoCallback callback) {}

    @Override
    public void consumePoint(int point, @NonNull ConsumePointCallback callback) {}

    @Override
    public void deleteUserInfo(@NonNull UserInfo userInfo) {
        new DeleteUserInfoTask(mapper).execute(userInfo.getUserId());
    }

    @Override
    public void deleteUserInfo(@NonNull String userId) {
        new DeleteUserInfoTask(mapper).execute(userId);
    }

    @Override
    public void registerEndpointArn(@Nullable String token, @NonNull RegisterEndpointCallback callback) {}

    public void registerEndpointArn(String userId, @Nullable String token, @NonNull RegisterEndpointCallback callback) {
        new RegisterEndpointTask(callback).execute(userId, token);
    }

    public void getRegistrationToken(GetRegisterationIdCallback callback) {
        new GetRegistrationTokenTask(context, callback).execute();
    }

    @Override
    public void requestVerificationCode(@NonNull String phone, RequestCodeCallback callback) {
        new RequestCodeTask(callback).execute(phone);
    }

    @Override
    public boolean validateVerificationCode(@NonNull String code) { return false; }

    @Override
    public void uploadProfileImage(@NonNull String filePath, int index, UploadImageCallback callback) {}

    @Override
    public void deleteProfileImage(@NonNull String key, UpdateUserInfoCallback callback) {
        new DeleteProfileImageTask().execute(key);
    }

    @Override
    public void clearBadgeCount(String tag, UpdateUserInfoCallback callback) {}

    @Override
    public void validatePurchase(String userId, String purchaseId, String token, ValidatePurchaseCallback callback) {
        VerifyPurchaseRequest request = new VerifyPurchaseRequest();
        request.setUserId(userId);
        request.setPlatform("android");
        request.setPurchaseId(purchaseId);
        request.setPurchaseToken(token);

        Log.d(TAG, "validatePurchase > userId : " + userId + ", purchaseId : " + purchaseId);

        new VerifyPurchaseTask(callback).execute(request);
    }

    private static class GetUserInfoTask extends AsyncTask<String, Void, Void> {

        private final DynamoDBMapper mapper;
        private final GetUserInfoCallback callback;
        private UserInfo userInfo;

        GetUserInfoTask(DynamoDBMapper mapper, GetUserInfoCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                userInfo = mapper.load(UserInfo.class, params[0]);
            } catch (Exception e) {
                Log.e(TAG, "GetUserInfoTask : " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (userInfo == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onUserInfoLoaded(userInfo);
            }
        }
    }

    private static class SearchUserInfoTask extends AsyncTask<UserInfo, Void, Void> {

        private final DynamoDBMapper mapper;
        private final String indexName;
        private final GetUserInfoCallback callback;
        private UserInfo userInfo = null;

        SearchUserInfoTask(String indexName, DynamoDBMapper mapper, GetUserInfoCallback callback) {
            this.mapper = mapper;
            this.indexName = indexName;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(UserInfo... params) {
            DynamoDBQueryExpression<UserInfo> queryExpression = new DynamoDBQueryExpression<UserInfo>()
                    .withIndexName(indexName)
                    .withConsistentRead(false)
                    .withHashKeyValues(params[0]);

            try {
                List<UserInfo> results = mapper.query(UserInfo.class, queryExpression);
                if (results != null && results.size() > 0) {
                    userInfo = results.get(0);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                callback.onError();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (userInfo == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onUserInfoLoaded(userInfo);
            }
        }
    }

    private static class UpdateUserInfoTask extends AsyncTask<UserInfo, Void, Void> {

        private final DynamoDBMapper mapper;
        private final UpdateUserInfoCallback callback;
        private UserInfo userInfo;

        UpdateUserInfoTask(DynamoDBMapper mapper, UpdateUserInfoCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(UserInfo... params) {
            try {
                mapper.save(params[0]);
                userInfo = params[0];
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (userInfo == null) {
                callback.onUpdateFailed();
            } else {
                callback.onUpdateComplete(userInfo);
            }
        }
    }

    private static class DeleteUserInfoTask extends AsyncTask<String, Void, Void> {

        private final DynamoDBMapper mapper;

        DeleteUserInfoTask(DynamoDBMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                UserInfo userInfo = mapper.load(UserInfo.class, params[0]);
                mapper.delete(userInfo);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }
    }

    private static class DeleteProfileImageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            CognitoSyncClientManager.getS3Client().deleteObject(ImageUtils.PROFILE_THUMB_BUCKET_NAME, strings[0]);
            CognitoSyncClientManager.getS3Client().deleteObject(ImageUtils.PROFILE_IMAGE_BUCKET_NAME, strings[0]);
            return null;
        }
    }

    private static class RegisterEndpointTask extends AsyncTask<String, Void, String> {

        private final RegisterEndpointCallback callback;

        private RegisterEndpointTask(RegisterEndpointCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {

            String userId = strings[0];
            String token = strings[1];

            ApiClientFactory apiClientFactory = new ApiClientFactory();
            NotificationAPIClient client = apiClientFactory.build(NotificationAPIClient.class);

            CreateEndpointARNRequestModel requestModel = new CreateEndpointARNRequestModel();
            requestModel.setPlatform(Constants.PLATFORM);
            requestModel.setUserId(userId);
            requestModel.setSnsToken(token);

            try {
                CreateEndpointARNResponseModel responseModel = client.endpointarnPost(requestModel);
                return responseModel.getEndpointARN();
            } catch (ApiClientException e) {
                Log.e(TAG, "ApiClientException > e : " + e.getMessage());
                return null;
            } catch (Exception e) {
                Log.d(TAG, "Other Exception > e : " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                if (callback != null) callback.onRegisterFailed();
            } else {
                if (callback != null) callback.onRegisterComplete(result);
            }
        }
    }

    private static class GetRegistrationTokenTask extends AsyncTask<Void, Void, String> {

        private final Context context;
        private final GetRegisterationIdCallback callback;

        private GetRegistrationTokenTask(Context context, GetRegisterationIdCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return GoogleCloudMessaging.getInstance(context).register(context.getString(R.string.project_number));
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result ==  null) {
                callback.onFailedRegistrationId();
            } else {
                callback.onGetRegistrationId(result);
            }
        }
    }

    private static class RequestCodeTask extends AsyncTask<String, Void, String> {

        private final RequestCodeCallback callback;

        RequestCodeTask(RequestCodeCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... strings) {

            String phone = strings[0];
            String code = String.format("%06d", Utilities.generateRandomVerificationCode());

            int member = 100;
            String usercode = "picnic";
            String username = "guest";
            String callphone1 = phone.substring(0, 3);
            String callphone2 = phone.substring(3, 7);
            String callphone3 = phone.substring(7, 11);

            final String callmessage = "[위켄드] 인증번호는 " + code + " 입니다. 정확히 입력해 주세요.";
            String rdate = "00000000";
            String rtime = "000000";
            String reqphone1 = "070";
            String reqphone2 = "7565";
            String reqphone3 = "4702";
            String callname = "인증번호발신";
            String deptcode = "GC-OIJ-B5";

            SureSMSAPI sms = new SureSMSAPI() {

                @Override
                public void report(SureSMSDeliveryReport sureSMSDeliveryReport) {
                    Log.d(TAG, "msgkey = " + sureSMSDeliveryReport.getMember());	// 메시지 고유값
                    Log.d(TAG, "result = " + sureSMSDeliveryReport.getResult());	// '2': 전송 결과 성공.  '4': 전송 결과 실패
                    Log.d(TAG, "errorcode = " + sureSMSDeliveryReport.getErrorCode());	// 결과 코드
                    Log.d(TAG, "recvtime = " + sureSMSDeliveryReport.getRecvDate() + sureSMSDeliveryReport.getRecvTime());	// 단말기 수신 시간
                }
            };

            SendReport sendReport = sms.sendMain(member, usercode, deptcode, username,
                    callphone1, callphone2, callphone3, callname, reqphone1, reqphone2, reqphone3, callmessage, rdate, rtime);

            if (sendReport.getStatus().trim().equals("O")) {
                return code;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onReceiveCode(result);
            } else {
                callback.onFailedRequest();
            }
        }
    }

    private static class VerifyPurchaseTask extends AsyncTask<VerifyPurchaseRequest, Void, String> {

        private final ValidatePurchaseCallback callback;

        private VerifyPurchaseTask(ValidatePurchaseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(VerifyPurchaseRequest... verifyPurchaseRequests) {

            Log.d(TAG, "VerifyPurchaseTask > doInBackground in");

            VerifyPurchaseRequest request = verifyPurchaseRequests[0];

            ApiClientFactory apiClientFactory = new ApiClientFactory();
            AuthenticationAPIClient client = apiClientFactory.build(AuthenticationAPIClient.class);

            try {
                VerifyPurchaseResponse response = client.verifypurchasePost(request);
                if (response != null) return response.getState();
            } catch (Exception e) {
                Log.e(TAG, "VerifyPurchaseTask > error : " + e.getMessage());
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && result.equals("verified")) {
                callback.onValidateComplete();
            } else {
                callback.onValidateFailed();
            }
        }
    }
}
