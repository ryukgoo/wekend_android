package com.entuition.wekend.data.source.userinfo.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.entuition.wekend.data.SharedPreferencesWrapper;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.view.main.likelist.LikeListFragment;
import com.entuition.wekend.view.main.mailbox.MailBoxFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class UserInfoLocalDataSource implements UserInfoDataSource {

    private static final String TAG = UserInfoLocalDataSource.class.getSimpleName();

    private static UserInfoLocalDataSource INSTANCE = null;

    public static UserInfoLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UserInfoLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserInfoLocalDataSource(context);
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private Context context;
    private String userId;
    private SharedPreferences sharedPreferences;

    private UserInfoLocalDataSource(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void clear() {

    }

    @Override
    public String getUserId() {
        if (userId == null) {
            userId = SharedPreferencesWrapper.getUserIdFromSharedPreferences(sharedPreferences);
        }
        return userId;
    }

    @Override
    public void refreshUserInfo() {

    }

    @Override
    public void getUserInfo(@NonNull String userId, GetUserInfoCallback callback) {}

    @Override
    public void searchUserInfoFromNickname(@NonNull String nickname, GetUserInfoCallback callback) {}

    @Override
    public void searchUserInfoFromUsername(@NonNull String username, GetUserInfoCallback callback) {}

    @Override
    public void updateUserInfo(@NonNull UserInfo userInfo, UpdateUserInfoCallback callback) {}

    @Override
    public void purchasePoint(int point, @NonNull UpdateUserInfoCallback callback) {}

    @Override
    public void consumePoint(int point, @NonNull ConsumePointCallback callback) {}

    @Override
    public void deleteUserInfo(@NonNull UserInfo userInfo) {}

    @Override
    public void deleteUserInfo(@NonNull String userId) {}

    @Override
    public void registerEndpointArn(@Nullable String token, RegisterEndpointCallback callback) {
        SharedPreferencesWrapper.registerRegistrationId(sharedPreferences, token);
    }

    @Override
    public void requestVerificationCode(@NonNull String phone, RequestCodeCallback callback) {}

    @Override
    public boolean validateVerificationCode(@NonNull String code) {
        return false;
    }

    @Override
    public void uploadProfileImage(@NonNull String filePath, int index, UploadImageCallback callback) {}

    @Override
    public void deleteProfileImage(@NonNull String key, UpdateUserInfoCallback callback) {}

    @Override
    public void clearBadgeCount(String tag, @Nullable UpdateUserInfoCallback callback) {
        if (tag.equals(LikeListFragment.TAG)) {
            SharedPreferencesWrapper.saveNotificationLikeNum(sharedPreferences, 0);
        } else if (tag.equals(MailBoxFragment.TAG)) {
            SharedPreferencesWrapper.saveNotificationMailNum(sharedPreferences, 0);
        }
    }

    public void clearProfileImageCache(String imagePath) {
        String cachedPhotoKey = ImageUtils.getHttpUrl(ImageUtils.PROFILE_IMAGE_BUCKET_NAME, imagePath);
        String cachedThumbKey = ImageUtils.getHttpUrl(ImageUtils.PROFILE_THUMB_BUCKET_NAME, imagePath);

        MemoryCacheUtils.removeFromCache(cachedPhotoKey, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(cachedPhotoKey, ImageLoader.getInstance().getDiskCache());
        MemoryCacheUtils.removeFromCache(cachedThumbKey, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(cachedThumbKey, ImageLoader.getInstance().getDiskCache());
    }

    public String getRegistrationToken() {
        return SharedPreferencesWrapper.getRegistrationIdFromSharedPreferences(sharedPreferences);
    }
}
