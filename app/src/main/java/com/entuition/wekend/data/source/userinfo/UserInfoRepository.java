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
    private String verficationCode = null;
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
    public void searchUserInfoFromNickname(@NonNull String nickname, final GetUserInfoCallback callback) {
        remoteDataSource.searchUserInfoFromNickname(nickname, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                callback.onUserInfoLoaded(new UserInfo(userInfo));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void searchUserInfoFromUsername(@NonNull String username, final GetUserInfoCallback callback) {
        remoteDataSource.searchUserInfoFromUsername(username, new GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                callback.onUserInfoLoaded(new UserInfo(userInfo));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void updateUserInfo(@NonNull UserInfo userInfo, final UpdateUserInfoCallback callback) {
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
    public void registerEndpointArn(@Nullable String token, final RegisterEndpointCallback callback) {
        String registrationToken = localDataSource.getRegistrationToken();

        if (TextUtils.isNullorEmptyString(registrationToken)) {
            remoteDataSource.getRegistrationToken(new GetRegisterationIdCallback() {
                @Override
                public void onGetRegistrationId(String registrationId) {
                    localDataSource.registerEndpointArn(registrationId, null);
                    remoteDataSource.registerEndpointArn(localDataSource.getUserId(), registrationId, callback);
                }

                @Override
                public void onFailedRegistrationId() {
                    callback.onRegisterFailed();
                }
            });
        } else {
            Log.d(TAG, "registerEndpointArn > Token : " + registrationToken);
            remoteDataSource.registerEndpointArn(localDataSource.getUserId(), registrationToken, null);
        }
    }

    @Override
    public void requestVerificationCode(@NonNull String phone, final RequestCodeCallback callback) {
        if (verficationCode != null) clearVerificationCode();

        remoteDataSource.requestVerificationCode(phone, new RequestCodeCallback() {
            @Override
            public void onReceiveCode(@NonNull String code) {
                verficationCode = code;
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
        return code.equals(verficationCode);
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
                    if (photos == null) { photos = new HashSet<>(); }
                    if (!photos.contains(imagePath)) { photos.add(imagePath); }
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
                if (getInfo.getPhotos().contains(key)) {
                    getInfo.getPhotos().remove(key);
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
                }
            }

            @Override
            public void onDataNotAvailable() {}
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

    private void clearVerificationCode() {
        verficationCode = null;
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
        });
    }

    private void clearCachedUserInfos() {
        if (cachedUserInfos == null) {
            cachedUserInfos = new LinkedHashMap<>();
        }
        cachedUserInfos.clear();
    }
}
