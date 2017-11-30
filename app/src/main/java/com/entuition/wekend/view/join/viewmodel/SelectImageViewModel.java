package com.entuition.wekend.view.join.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

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

    private static final int REQUEST_PERMISSION_REQ_CODE = 34;
    private static final int ACTION_REQUEST_GALLERY = 1003;
    private static final int ACTION_REQUEST_CAMERA = 1004;
    private static final int ACTION_REQUEST_CROP = 1005;

    private final WeakReference<SelectImageNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;

    private Uri tempImageUri;
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
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY :
                case ACTION_REQUEST_CAMERA :
                    tempImageUri = data.getData();
                    outputFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    Intent intent = ImageUtils.getCroppedImageIntent(context, outputFile, tempImageUri);
                    if (intent != null) context.startActivityForResult(intent, ACTION_REQUEST_CROP);
                    break;
                case ACTION_REQUEST_CROP :
                    uploadImage();
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

    public void onClickSelectImage(View view) {
        navigator.get().selectProfileImage();
    }

    public void selectPhotoFromGallery(Activity activity) {
        selectImageFromGallery(activity);
    }

    private void selectImageFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, ACTION_REQUEST_GALLERY);
    }

    private void uploadImage() {
        if (navigator.get() != null) navigator.get().onImageSelected(ImageUtils.decodeFile(outputFile));
        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                userInfoDataSource.uploadProfileImage(Uri.fromFile(outputFile).getPath(),
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
}
