package com.entuition.wekend.view.main.setting.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.util.ImageUtils;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 30..
 */

public class SelectImageViewModel {

    public static final String TAG = SelectImageViewModel.class.getSimpleName();

    private final int REQUEST_PERMISSION_REQ_CODE = 34;
    static final int ACTION_REQUEST_GALLERY = 1003;
    static final int ACTION_REQUEST_CAMERA = 1004;
    static final int ACTION_REQUEST_CROP = 1005;

    private final WeakReference<SelectImageNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;

    private File outputFile;

    public SelectImageViewModel(SelectImageNavigator navigator,
                                UserInfoDataSource userInfoDataSource) {
        this.navigator = new WeakReference<SelectImageNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
    }

    public boolean checkSelfPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    public void onActivityResult(Activity context, int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult > resultCode : " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY :
                case ACTION_REQUEST_CAMERA :
                    outputFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Intent intent = ImageUtils.getCroppedImageIntent(context, outputFile, data.getData());
                    if (intent != null) context.startActivityForResult(intent, ACTION_REQUEST_CROP);
                    break;
                case ACTION_REQUEST_CROP :
                    uploadImage(0);
                    break;
                default:
                    break;
            }
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPhotoFromGallery(activity);
                } else {
                    // denied
                    navigator.get().onPermissionDenied();
                }
                break;
            }
        }
    }

    public void onClickSelectImage() {
        navigator.get().selectProfileImage();
    }

    public void selectPhotoFromGallery(Activity activity) {
        startGalleryActivity(activity);
    }

    private void startGalleryActivity(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, ACTION_REQUEST_GALLERY);
    }

    void uploadImage(final int index) {
        if (navigator.get() != null) navigator.get().onImageSelected(ImageUtils.decodeFile(outputFile));
        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                userInfoDataSource.uploadProfileImage(Uri.fromFile(outputFile).getPath(), index,
                        new UserInfoDataSource.UploadImageCallback() {
                            @Override
                            public void onFailedUploadImage() {
                                Log.d(TAG, "onUploadImageFailed");
                                if (navigator.get() != null) navigator.get().onUploadImageFailed();
                            }

                            @Override
                            public void onCanceledUploadImage() {
                                Log.d(TAG, "onUploadImageCanceled");
                                if (navigator.get() != null) navigator.get().onUploadImageCanceled();
                            }

                            @Override
                            public void onFailedUpdateUserInfo() {
                                Log.d(TAG, "onFailedUpadteUserInfo");
                                if (navigator.get() != null) navigator.get().onUpdateUserInfoFailed();
                            }

                            @Override
                            public void onCompleteUploadImage() {
                                Log.d(TAG, "onUploadImageCompleted");
                                if (navigator.get() != null) navigator.get().onUploadImageCompleted();
                            }

                            @Override
                            public void onErrorUnknown() {
                                Log.e(TAG, "onErrorUnknown");
                            }
                        });
            }

            @Override
            public void onDataNotAvailable() {}
        });
    }

    public void deleteImage(int index) {
        String userId = userInfoDataSource.getUserId();
        String key = ImageUtils.getUploadedPhotoFileName(userId, index);

        userInfoDataSource.deleteProfileImage(key, new UserInfoDataSource.UpdateUserInfoCallback() {
            @Override
            public void onUpdateComplete(UserInfo userInfo) {
                navigator.get().onImageSelected(null);
            }

            @Override
            public void onUpdateFailed() {}
        });
    }
}
