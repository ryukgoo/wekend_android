package com.entuition.wekend.view.main.setting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.SettingProfileActivityBinding;
import com.entuition.wekend.util.ActivityUtils;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.view.join.viewmodel.SelectImageNavigator;
import com.entuition.wekend.view.join.viewmodel.SelectImageViewModel;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerAdapter;
import com.entuition.wekend.view.main.setting.viewmodel.SettingProfileNavigator;
import com.entuition.wekend.view.main.setting.viewmodel.SettingProfileViewModel;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 1. 6..
 */
public class SettingProfileActivity extends AppCompatActivity implements SettingProfileNavigator, SelectImageNavigator {

    public static final String TAG = SettingProfileActivity.class.getSimpleName();

    private SettingProfileActivityBinding binding;
    private SettingProfileViewModel viewModel;
    private SelectImageViewModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new SettingProfileViewModel(this, this, UserInfoRepository.getInstance(this));
        imageModel = new SelectImageViewModel(this, UserInfoRepository.getInstance(this));

        binding = DataBindingUtil.setContentView(this, R.layout.setting_profile_activity);
        binding.setModel(viewModel);
        binding.setImageModel(imageModel);

        setupToolbar();
        setupViewPager();

        viewModel.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageModel.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageModel.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupViewPager() {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(binding.getModel(), new ArrayList<String>(0));
        binding.profileViewPager.setAdapter(adapter);
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

    @Override
    public void selectProfileImage() {
        if (imageModel.checkSelfPermission(this)) {
            imageModel.selectPhotoFromGallery(this);
        }
    }

    @Override
    public void onImageSelected(Bitmap bitmap) {
        View view = binding.profileViewPager.findViewWithTag(binding.profileViewPager.getCurrentItem());
        if (view != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);
            if (imageView != null) imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPermissionDenied() {
        AlertUtils.showAlertDialog(this, R.string.edit_profile_image_failed,
                R.string.edit_profile_image_permission_denied);
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

    }
}
