package com.entuition.wekend.model.data.user.asynctask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.transfer.S3TransferUtilityManager;
import com.entuition.wekend.view.util.ImageUtilities;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ryukgoo on 2016. 9. 26..
 */

public class UploadResizedImageTask extends AsyncTask<Uri, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private Context context;
    private boolean isSuccess;
    private String uploadedImagePath;
    private TransferListener listener;
    private IUploadResizedImageCallback callback;

    public UploadResizedImageTask(Context context) {
        this.context = context;
    }

    public void setCallback(IUploadResizedImageCallback callback) {
        this.callback = callback;
    }

    public void setListener(TransferListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Uri... params) {
        Uri imageUri = params[0];

        String userId = UserInfoDaoImpl.getInstance().getUserId(context);
        UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userId);
        S3TransferUtilityManager.getInstance(context).setUploadListener(listener);

        uploadedImagePath = UserInfoDaoImpl.getUploadedPhotoFileName(userId, 0);
        S3TransferUtilityManager.getInstance(context).beginUpload(context, imageUri.getPath(), uploadedImagePath);

        Set<String> photos = new HashSet<String>();
        photos.add(uploadedImagePath);

        userInfo.setPhotos(photos);
        isSuccess = UserInfoDaoImpl.getInstance().updateUserInfo(userInfo);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            callback.onSuccess(uploadedImagePath);
        } else {
            callback.onFailed();
        }
    }
}
