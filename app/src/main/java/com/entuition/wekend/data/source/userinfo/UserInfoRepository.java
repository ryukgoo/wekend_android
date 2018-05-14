package com.entuition.wekend.data.source.userinfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.entuition.wekend.data.source.userinfo.local.UserInfoLocalDataSource;
import com.entuition.wekend.data.source.userinfo.remote.UserInfoRemoteDataSource;
import com.entuition.wekend.data.transfer.S3TransferUtilityManager;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.util.TextUtils;
import com.entuition.wekend.view.main.likelist.LikeListFragment;
import com.entuition.wekend.view.main.mailbox.MailBoxFragment;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class UserInfoRepository implements UserInfoDataSource {

    private static final String TAG = UserInfoRepository.class.getSimpleName();

    private static UserInfoRepository INSTANCE = null;

    public static UserInfoRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (UserInfoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserInfoRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        UserInfoLocalDataSource.destroyInstance();
        UserInfoRemoteDataSource.destroyInstance();
        INSTANCE = null;
    }

    private final Context context;
    private final UserInfoRemoteDataSource remoteDataSource;
    private final UserInfoLocalDataSource localDataSource;

    Map<String, UserInfo> cachedUserInfos;
    private String verificationCode = null;
    private boolean isCacheDirty = false;

    private UserInfoRepository(Context context) {
        this.context = context;
        this.remoteDataSource = UserInfoRemoteDataSource.getInstance(context);
        this.localDataSource = UserInfoLocalDataSource.getInstance(context);
        clearCachedUserInfos();
    }

    @Override
    public void clear() {
        remoteDataSource.clear();
        localDataSource.clear();
        clearCachedUserInfos();
    }

    @Override
    public String getUserId() {
        return localDataSource.getUserId();
    }

    @Override
    public String getUsernameFromDevice() {
        return localDataSource.getUsernameFromDevice();
    }

    @Override
    public void refreshUserInfo() {
        isCacheDirty = true;
    }

    @Override
    public void getUserInfo(@Nullable String userId, GetUserInfoCallback callback) {

        String requestUserId;
        if (!TextUtils.isNullorEmptyString(userId)) {
            requestUserId = userId;
        } else {
            requestUserId = getUserId();
        }

        if (cachedUserInfos != null && cachedUserInfos.get(requestUserId) != null && !isCacheDirty) {
            callback.onUserInfoLoaded(new UserInfo(cachedUserInfos.get(requestUserId)));
            return;
        }

        getUserInfoFromRemoteDataSource(requestUserId, callback);
    }

    @Override
    public void searchUserInfoByNickname(@NonNull String nickname, final GetUserInfoCallback callback) {
        remoteDataSource.searchUserInfoByNickname(nickname, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                callback.onUserInfoLoaded(new UserInfo(userInfo));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void searchUserInfoByUsername(@NonNull String username, final GetUserInfoCallback callback) {
        remoteDataSource.searchUserInfoByUsername(username, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                callback.onUserInfoLoaded(new UserInfo(userInfo));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void searchUserInfoByPhone(@NonNull String phone, final GetUserInfoCallback callback) {
        remoteDataSource.searchUserInfoByPhone(phone, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                callback.onUserInfoLoaded(new UserInfo((userInfo)));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void updateUserInfo(@NonNull UserInfo userInfo, final UpdateUserInfoCallback callback) {

        if (userInfo.getPhotos() != null && userInfo.getPhotos().isEmpty()) {
            userInfo.setPhotos(null);
        }

        remoteDataSource.updateUserInfo(userInfo, new UpdateUserInfoCallback() {
            @Override
            public void onUpdateComplete(UserInfo userInfo) {
                cachedUserInfos.put(userInfo.getUserId(), userInfo);
                callback.onUpdateComplete(new UserInfo(userInfo));
            }

            @Override
            public void onUpdateFailed() {
                callback.onUpdateFailed();
            }
        });
    }

    @Override
    public void purchasePoint(final int point, @NonNull final UpdateUserInfoCallback callback) {
        getUserInfo(null, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {

                userInfo.setBalloon(userInfo.getBalloon() + point);

                remoteDataSource.updateUserInfo(userInfo, new UpdateUserInfoCallback() {
                    @Override
                    public void onUpdateComplete(UserInfo userInfo) {
                        cachedUserInfos.put(userInfo.getUserId(), userInfo);
                        callback.onUpdateComplete(userInfo);
                    }

                    @Override
                    public void onUpdateFailed() {
                        callback.onUpdateFailed();
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                callback.onUpdateFailed();
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void consumePoint(final int point, @NonNull final ConsumePointCallback callback) {
        getUserInfo(null, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                if (userInfo.getBalloon() < point) {
                    callback.onPointNotEnough();
                } else {
                    userInfo.setBalloon(userInfo.getBalloon() - point);
                    remoteDataSource.updateUserInfo(userInfo, new UpdateUserInfoCallback() {
                        @Override
                        public void onUpdateComplete(UserInfo userInfo) {
                            cachedUserInfos.put(userInfo.getUserId(), userInfo);
                            callback.onConsumePointComplete(userInfo);
                        }

                        @Override
                        public void onUpdateFailed() {
                            callback.onConsumeNotAvailable();
                        }
                    });
                }
            }

            @Override
            public void onDataNotAvailable() {
                callback.onConsumeNotAvailable();
            }

            @Override
            public void onError() {}
        });
    }

    @Override
    public void deleteUserInfo(@NonNull UserInfo userInfo) {
        remoteDataSource.deleteUserInfo(userInfo);
    }

    @Override
    public void deleteUserInfo(@NonNull String userId) {
        remoteDataSource.deleteUserInfo(userId);
    }

    @Override
    public void registerEndpointArn(@Nullable String token, RegisterEndpointCallback callback) {
        if (token != null) {
            localDataSource.registerEndpointArn(token, callback);
        }

        String storedToken = localDataSource.getRegistrationToken();

        if (localDataSource.getUserId() != null && storedToken != null) {
            remoteDataSource.registerEndpointArn(localDataSource.getUserId(), storedToken, callback);
        }
    }

    @Override
    public void requestVerificationCode(@NonNull String phone, final RequestCodeCallback callback) {
        if (verificationCode != null) clearVerificationCode();

        remoteDataSource.requestVerificationCode(phone, new RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                verificationCode = code;
                callback.onReceiveCode(code);
            }

            @Override
            public void onFailedRequest() {
                callback.onFailedRequest();
            }
        });
    }

    @Override
    public boolean validateVerificationCode(@NonNull String code) {
        Log.d(TAG, "validateVerificationCode : verification Code : " + verificationCode + ", code : " + code);

        return code.equals(verificationCode);
    }

    @Override
    public void uploadProfileImage(@NonNull String filePath, int index, final UploadImageCallback callback) {

        final String userId = getUserId();
        final String imagePath = ImageUtils.getUploadedPhotoFileName(userId, index);

        S3TransferUtilityManager.getInstance(context).setUploadListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {

                    UserInfo userInfo = cachedUserInfos.get(userId);
                    if (userInfo == null) callback.onErrorUnknown();

                    Set<String> photos = userInfo.getPhotos();
                    if (photos == null) {
                        Log.d(TAG, "uploadProfileImage > photos is null");
                        photos = new HashSet<>();
                        photos.add(imagePath);
                    } else if (!photos.contains(imagePath)) {
                        Log.d(TAG, "uploadProfileImage > not contain key");
                        photos.add(imagePath);
                    }
                    userInfo.setPhotos(photos);

                    remoteDataSource.updateUserInfo(userInfo, new UpdateUserInfoCallback() {
                        @Override
                        public void onUpdateComplete(UserInfo userInfo) {

                            Log.d(TAG, "uploadProfileImage > onUpdateComplete > userInfo : " + userInfo.getUserId());

                            localDataSource.clearProfileImageCache(imagePath);
                            callback.onCompleteUploadImage();
                        }

                        @Override
                        public void onUpdateFailed() {
                            callback.onFailedUpdateUserInfo();
                        }
                    });
                } else if (state == TransferState.CANCELED) {
                    callback.onCanceledUploadImage();
                } else if (state == TransferState.FAILED) {
                    callback.onFailedUploadImage();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            @Override
            public void onError(int id, Exception ex) {}
        });

        S3TransferUtilityManager.getInstance(context).beginUpload(context, filePath, imagePath);
    }

    @Override
    public void deleteProfileImage(@NonNull final String key, final UpdateUserInfoCallback callback) {
        Log.d(TAG, "deleteProfileImage > key : " + key);
        remoteDataSource.deleteProfileImage(key, callback);

        getUserInfo(getUserId(), new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo getInfo) {
                if (getInfo.getPhotos() != null && getInfo.getPhotos().contains(key)) {
                    getInfo.getPhotos().remove(key);

                    Log.d(TAG, "userInfo's photos count : " + getInfo.getPhotos().isEmpty());
                    if (getInfo.getPhotos().isEmpty()) { getInfo.setPhotos(null); }

                    remoteDataSource.updateUserInfo(getInfo, new UpdateUserInfoCallback() {
                        @Override
                        public void onUpdateComplete(UserInfo updatedInfo) {
                            if (cachedUserInfos == null) cachedUserInfos = new LinkedHashMap<>();
                            cachedUserInfos.put(updatedInfo.getUserId(), updatedInfo);
                            localDataSource.clearProfileImageCache(key);
                            callback.onUpdateComplete(updatedInfo);
                        }

                        @Override
                        public void onUpdateFailed() {
                            callback.onUpdateFailed();
                        }
                    });
                } else {
                    callback.onUpdateFailed();
                }
            }

            @Override
            public void onDataNotAvailable() {
                callback.onUpdateFailed();
            }

            @Override
            public void onError() {
                callback.onUpdateFailed();
            }
        });
    }

    @Override
    public void clearBadgeCount(String tag, UpdateUserInfoCallback callback) {

        UserInfo userInfo = cachedUserInfos.get(getUserId());

        if (userInfo == null) { callback.onUpdateFailed(); }

        localDataSource.clearBadgeCount(tag, null);

        if (tag.equals(LikeListFragment.TAG)) {
            if (userInfo.getNewLikeCount() == 0) {
                callback.onUpdateFailed();
                return;
            }
            userInfo.setNewLikeCount(0);
        } else if (tag.equals(MailBoxFragment.TAG)) {
            if (userInfo.getNewReceiveCount() == 0 && userInfo.getNewSendCount() == 0) {
                callback.onUpdateFailed();
                return;
            }
            userInfo.setNewReceiveCount(0);
            userInfo.setNewSendCount(0);
        }
        remoteDataSource.updateUserInfo(userInfo, callback);
    }

    @Override
    public void validatePurchase(final String userId, String purchaseId, String token, final ValidatePurchaseCallback callback) {
        remoteDataSource.validatePurchase(userId, purchaseId, token, new ValidatePurchaseCallback() {
            @Override
            public void onValidateComplete() {
                getUserInfoFromRemoteDataSource(userId, new GetUserInfoCallback() {
                    @Override
                    public void onUserInfoLoaded(UserInfo userInfo) { callback.onValidateComplete(); }

                    @Override
                    public void onDataNotAvailable() { callback.onValidateFailed(); }

                    @Override
                    public void onError() { callback.onValidateFailed(); }
                });
            }

            @Override
            public void onValidateFailed() { callback.onValidateFailed(); }
        });
    }

    private void clearVerificationCode() {
        verificationCode = null;
    }

    private void getUserInfoFromRemoteDataSource(@NonNull String userId, final GetUserInfoCallback callback) {
        remoteDataSource.getUserInfo(userId, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                cachedUserInfos.put(userInfo.getUserId(), userInfo);
                isCacheDirty = false;
                callback.onUserInfoLoaded(new UserInfo(userInfo));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }

            @Override
            public void onError() {}
        });
    }

    private void clearCachedUserInfos() {
        if (cachedUserInfos == null) {
            cachedUserInfos = new LinkedHashMap<>();
        }
        cachedUserInfos.clear();
    }
}
