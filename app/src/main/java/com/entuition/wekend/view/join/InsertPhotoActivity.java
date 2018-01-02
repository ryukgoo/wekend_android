package com.entuition.wekend.view.join;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.InsertPhotoActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.view.common.CircleDrawable;
import com.entuition.wekend.view.main.container.ContainerActivity;
import com.entuition.wekend.view.main.setting.viewmodel.SelectImageNavigator;
import com.entuition.wekend.view.main.setting.viewmodel.SelectImageViewModel;

/**
 * Created by Kim on 2015-08-17.
 */
public class InsertPhotoActivity extends AppCompatActivity implements SelectImageNavigator {

    public static final String TAG = InsertPhotoActivity.class.getSimpleName();

    private InsertPhotoActivityBinding binding;
    private SelectImageViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoDataSource userInfoDataSource = UserInfoRepository.getInstance(this);

        model = new SelectImageViewModel(this, userInfoDataSource);

        binding = DataBindingUtil.setContentView(this, R.layout.insert_photo_activity);
        binding.setModel(model);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        model.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        model.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void selectProfileImage() {
        if (model.checkSelfPermission(this)) {
            model.selectPhotoFromGallery(this);
        }
    }

    public void onClickNextButton(View view) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onPermissionDenied() {
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_failed,
                R.string.edit_profile_image_permission_denied);
    }

    @Override
    public void onImageSelected(Bitmap bitmap) {
        binding.insertProfileImage.setBackground(new CircleDrawable(bitmap, 20, 0, -4));
    }

    @Override
    public void onUploadImageFailed() {

    }

    @Override
    public void onUploadImageCanceled() {

    }

    @Override
    public void onUpdateUserInfoFailed() {

    }

    @Override
    public void onUploadImageCompleted() {
        Log.d(TAG, "onUploadImageCompleted > userInfo updated");
    }
}