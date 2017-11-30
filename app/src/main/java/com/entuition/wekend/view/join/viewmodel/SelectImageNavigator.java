package com.entuition.wekend.view.join.viewmodel;

import android.graphics.Bitmap;

/**
 * Created by ryukgoo on 2017. 10. 30..
 */

public interface SelectImageNavigator {

    void selectProfileImage();

    void onImageSelected(Bitmap bitmap);

    void onPermissionDenied();

    void onUploadImageFailed();
    void onUploadImageCanceled();
    void onUpdateUserInfoFailed();
    void onUploadImageCompleted();
}

