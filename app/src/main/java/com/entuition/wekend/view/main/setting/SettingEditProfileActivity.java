package com.entuition.wekend.view.main.setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.SettingEditProfileActivityBinding;
import com.entuition.wekend.util.ActivityUtils;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.view.main.setting.viewmodel.GridLayoutCell;
import com.entuition.wekend.view.main.setting.viewmodel.MultiSelectImageViewModel;
import com.entuition.wekend.view.main.setting.viewmodel.SelectImageNavigator;
import com.entuition.wekend.view.main.setting.viewmodel.SettingEditProfileNavigator;
import com.entuition.wekend.view.main.setting.viewmodel.SettingEditProfileViewModel;

/**
 * Created by ryukgoo on 2017. 12. 20..
 */

public class SettingEditProfileActivity extends AppCompatActivity implements SelectImageNavigator, SettingEditProfileNavigator {

    public static final String TAG = SettingEditProfileActivity.class.getSimpleName();

    private SettingEditProfileActivityBinding binding;
    private SettingEditProfileViewModel model;
    private MultiSelectImageViewModel imageModel;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageModel = new MultiSelectImageViewModel(this, UserInfoRepository.getInstance(this));
        model = new SettingEditProfileViewModel(this, this, UserInfoRepository.getInstance(this));
        binding = DataBindingUtil.setContentView(this, R.layout.setting_edit_profile_activity);

        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait_please));

        binding.setModel(model);
        binding.setImageModel(imageModel);
        binding.setActivity(this);

        setupToolbar();
        model.onCreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageModel.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageModel.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            case R.id.action_done :
                model.editDone();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSelectImage(View view, final int index) {
        Log.d(TAG, "onClickSelectImage > index : " + index);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.edit_profile_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    imageModel.onClickSelectImage(index);
                } else if (which == 1) {
                    imageModel.deleteImage(index);
                }
            }
        });
        builder.show();
    }

    @Override
    public void selectProfileImage() {
        if (imageModel.checkSelfPermission(this)) {
            imageModel.selectPhotoFromGallery(this);
        }
    }

    @Override
    public void onImageSelected(Bitmap bitmap) {
        GridLayoutCell cell = (GridLayoutCell) binding.gridLayout.getChildAt(imageModel.getCellIndex());
        if (cell != null) {
            ImageView view = cell.findViewById(R.id.imageView);
            if (view != null) {
                if (bitmap == null) {
                    view.setImageResource(R.drawable.profile_default);
                } else {
                    view.setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public void onPermissionDenied() {
        progressDialog.dismiss();
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_failed,
                R.string.edit_profile_image_permission_denied);
    }

    @Override
    public void onUploadImagePrepare() {
        progressDialog.show();
    }

    @Override
    public void onUploadImageFailed() {
        progressDialog.dismiss();
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_failed,
                R.string.edit_profile_image_failed_message);
    }

    @Override
    public void onUploadImageCanceled() {
        progressDialog.dismiss();
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_failed,
                R.string.edit_profile_image_failed_message);
    }

    @Override
    public void onUpdateUserInfoFailed() {
        progressDialog.dismiss();
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_failed,
                R.string.edit_profile_image_failed_message);
    }

    @Override
    public void onUploadImageCompleted() {
        progressDialog.dismiss();
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_title,
                R.string.edit_profile_image_message);
    }

    @Override
    public void onDeleteImagePrepare() {
        progressDialog.show();
    }

    @Override
    public void onDeleteImageCompleted() {
        progressDialog.dismiss();
        Log.d(TAG, "onDeleteImageCompleted");
        AlertUtils.showAlertDialog(this, R.string.delete_profile_image_title,
                R.string.delete_profile_image_message);
    }

    @Override
    public void onDeleteImageFailed() {
        progressDialog.dismiss();
        AlertUtils.showAlertDialog(this, R.string.delete_profile_image_failed_title,
                R.string.delete_profile_image_failed_message);
    }

    @Override
    public void dismiss() {
        super.onBackPressed();
    }

    @Override
    public void showUpdateError() {
        AlertUtils.showAlertDialog(this, R.string.update_user_failed,
                R.string.update_user_failed_message);
    }

    @Override
    public void showTooLongIntroduce() {
        AlertUtils.showAlertDialog(this, R.string.introduce_error_title,
                R.string.introduce_error_message);
    }

    @Override
    public void showInvalidCode() {
        AlertUtils.showAlertDialog(this, R.string.not_match_verification,
                R.string.not_match_verification_message);
    }

    @Override
    public void showRequestCode() {
        AlertUtils.showAlertDialog(this, R.string.confirm_verification,
                R.string.send_verification_message);
    }

    @Override
    public void showRequestCodeFailed() {
        AlertUtils.showAlertDialog(this, R.string.send_verification_failed,
                R.string.send_verification_failed_message);
    }

    @Override
    public void showEditPhoneComplete() {
        ActivityUtils.hideKeyboard(this);
        AlertUtils.showAlertDialog(this, R.string.edit_phone_done,
                R.string.edit_phone_done_message);
    }

    @Override
    public void showEditPhoneFailed() {
        ActivityUtils.hideKeyboard(this);
        AlertUtils.showAlertDialog(this, R.string.edit_phone_failed,
                R.string.edit_phone_failed_message);
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.settingToolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
