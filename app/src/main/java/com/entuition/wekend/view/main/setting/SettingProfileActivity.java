package com.entuition.wekend.view.main.setting;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.SettingProfileActivityBinding;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerAdapter;
import com.entuition.wekend.view.main.setting.viewmodel.SelectImageViewModel;
import com.entuition.wekend.view.main.setting.viewmodel.SettingProfileNavigator;
import com.entuition.wekend.view.main.setting.viewmodel.SettingProfileViewModel;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 1. 6..
 */
public class SettingProfileActivity extends AppCompatActivity implements SettingProfileNavigator {

    public static final String TAG = SettingProfileActivity.class.getSimpleName();

    private SettingProfileActivityBinding binding;
    private SettingProfileViewModel viewModel;
    private SelectImageViewModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new SettingProfileViewModel(this, this, UserInfoRepository.getInstance(this));

        binding = DataBindingUtil.setContentView(this, R.layout.setting_profile_activity);
        binding.setModel(viewModel);

        setupToolbar();
        setupViewPager();

        viewModel.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onDestroy() {
        viewModel.onDestroy();
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
        binding.profileViewPager.addOnPageChangeListener(binding.indicator);
    }

    @Override
    public void gotoEditProfileView() {
        Intent intent = new Intent(this, SettingEditProfileActivity.class);
        startActivity(intent);
    }
}
