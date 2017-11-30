package com.entuition.wekend.view.join;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.RegisterActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.join.viewmodel.InputAccountNavigator;
import com.entuition.wekend.view.join.viewmodel.InputAccountViewModel;

/**
 * Created by Kim on 2015-08-07.
 */
public class SignUpActivity extends AppCompatActivity implements InputAccountNavigator {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private InputAccountViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoDataSource userInfoDataSource = UserInfoRepository.getInstance(this);
        model = new InputAccountViewModel(this, this, userInfoDataSource);

        RegisterActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.register_activity);
        binding.setModel(model);

        model.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.onPause();
    }

    @Override
    protected void onDestroy() {
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    public void gotoInputUserInfo(@NonNull String username, @NonNull String password) {
        Intent intent = new Intent(this, InputUserInfoActivity.class);
        intent.putExtra(Constants.ExtraKeys.USERNAME, username);
        intent.putExtra(Constants.ExtraKeys.PASSWORD, password);
        startActivity(intent);
    }

    @Override
    public void showNotEqualConfirm() {
        AlertUtils.showAlertDialog(this, R.string.invalid_password,
                R.string.not_equal_password_message);
    }

    @Override
    public void showDuplicatedEmail() {
        AlertUtils.showAlertDialog(this, R.string.duplicated_email,
                R.string.duplicated_email_message);
    }
}