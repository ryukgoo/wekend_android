package com.entuition.wekend.model.googleservice.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.apigateway.ApiClientException;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.entuition.clientsdk.NotificationAPIClient;
import com.entuition.clientsdk.model.CreateEndpointARNRequestModel;
import com.entuition.clientsdk.model.CreateEndpointARNResponseModel;
import com.entuition.wekend.R;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by ryukgoo on 2016. 5. 27..
 */
public class StoreEndpointArnTask extends AsyncTask<Void, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private Context context;

    private GoogleCloudMessaging gcm;
    private IStoreEndpointArnCallback callback;
    private String registrationId;
    private String endpointARN;
    private boolean isSuccess;

    public StoreEndpointArnTask(Context context) {
        this.context = context;
    }

    public void setCallback(IStoreEndpointArnCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String userId = UserInfoDaoImpl.getInstance(context).getUserId();

        gcm = GoogleCloudMessaging.getInstance(context);
        try {
            registrationId = gcm.register(context.getString(R.string.project_number));
        } catch (Exception e) {
            Log.e(TAG, "RegistrationError > e : " + e.getMessage());

            isSuccess = false;
            return null;
        }

        ApiClientFactory apiClientFactory = new ApiClientFactory();
        final NotificationAPIClient client = apiClientFactory.build(NotificationAPIClient.class);

        CreateEndpointARNRequestModel requestModel = new CreateEndpointARNRequestModel();
        requestModel.setPlatform("Android");
        requestModel.setUserId(userId);
        requestModel.setSnsToken(registrationId);

        CreateEndpointARNResponseModel responseModel = null;

        try {
            responseModel = client.endpointarnPost(requestModel);
        } catch (ApiClientException e) {
            Log.e(TAG, "ApiClientException > e : " + e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "Other Exception > e : " + e.getMessage());
        }

        isSuccess = responseModel != null && responseModel.getEndpointARN() != null;

        if (isSuccess) {
            endpointARN = responseModel.getEndpointARN();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            UserInfoDaoImpl.getInstance(context).getUserInfo().setEndpointARN(endpointARN);
            callback.onSuccess(registrationId);
        } else {
            callback.onFailed();
        }
    }
}
