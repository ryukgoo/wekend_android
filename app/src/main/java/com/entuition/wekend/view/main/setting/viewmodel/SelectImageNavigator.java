package com.entuition.wekend.view.main.setting.viewmodel;

import android.graphics.Bitmap;

/**
 * Created by ryukgoo on 2017. 10. 30..
 */

public interface SelectImageNavigator {

    void selectProfileImage();

    void onImageSelected(Bitmap bitmap);

    void onPermissionDenied();

    void onUploadImagePrepare();
    void onUploadImageFailed();
    void onUploadImageCanceled();
    void onUpdateUserInfoFailed();
    void onUploadImageCompleted();

    void onDeleteImagePrepare();
    void onDeleteImageCompleted();
    void onDeleteImageFailed();
}

