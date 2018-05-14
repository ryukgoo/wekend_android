package com.entuition.wekend.data.source.userinfo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public interface UserInfoDataSource {

    interface GetUserInfoCallback {
        void onUserInfoLoaded(UserInfo userInfo);
        void onDataNotAvailable();
        void onError();
    }

    interface UpdateUserInfoCallback {
        void onUpdateComplete(UserInfo userInfo);
        void onUpdateFailed();
    }

    interface ConsumePointCallback {
        void onConsumePointComplete(UserInfo userInfo);
        void onPointNotEnough();
        void onConsumeNotAvailable();
    }

    interface RegisterEndpointCallback {
        void onRegisterComplete(String endpoint);
        void onRegisterFailed();
    }

    interface GetRegisterationIdCallback {
        void onGetRegistrationId(String registrationId);
        void onFailedRegistrationId();
    }

    interface RequestCodeCallback {
        void onReceiveCode(@NonNull String code);
        void onFailedRequest();
    }

    interface UploadImageCallback {
        void onFailedUploadImage();
        void onCanceledUploadImage();
        void onFailedUpdateUserInfo();
        void onCompleteUploadImage();
        void onErrorUnknown();
    }

    interface ValidatePurchaseCallback {
        void onValidateComplete();
        void onValidateFailed();
    }

    void clear();

    String getUserId();

    String getUsernameFromDevice();

    void refreshUserInfo();

    void getUserInfo(String userId, GetUserInfoCallback callback);

    void searchUserInfoByNickname(@NonNull String nickname, GetUserInfoCallback callback);

    void searchUserInfoByUsername(@NonNull String username, GetUserInfoCallback callback);

    void searchUserInfoByPhone(@NonNull String phone, GetUserInfoCallback callback);

    void updateUserInfo(@NonNull UserInfo userInfo, UpdateUserInfoCallback callback);

    void deleteUserInfo(@NonNull UserInfo userInfo);

    void deleteUserInfo(@NonNull String userId);

    void registerEndpointArn(@Nullable String token, RegisterEndpointCallback callback);

    void purchasePoint(int point, @NonNull UpdateUserInfoCallback callback);

    void consumePoint(int point, @NonNull ConsumePointCallback callback);

    void requestVerificationCode(@NonNull String phone, RequestCodeCallback callback);

    boolean validateVerificationCode(@NonNull String code);

    void uploadProfileImage(@NonNull String filePath, int index, UploadImageCallback callback);

    void deleteProfileImage(@NonNull String key, UpdateUserInfoCallback callback);

    void clearBadgeCount(String tag, UpdateUserInfoCallback callback);

    void validatePurchase(String userId, String purchaseId, String token, ValidatePurchaseCallback callback);
}
