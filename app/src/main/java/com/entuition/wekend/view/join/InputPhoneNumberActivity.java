package com.entuition.wekend.view.join;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.authentication.AuthenticationRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.InputPhoneActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.view.join.viewmodel.InputPhoneNavigator;
import com.entuition.wekend.view.join.viewmodel.InputPhoneViewModel;

/**
 * Created by Kim on 2015-08-17.
 */
public class InputPhoneNumberActivity extends AppCompatActivity implements InputPhoneNavigator {

    private static final String TAG = InputPhoneNumberActivity.class.getSimpleName();

    private ProgressDialog progressDialog;

    private InputPhoneViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoDataSource userInfoDataSource = UserInfoRepository.getInstance(this);
        viewModel = new InputPhoneViewModel(this, this, AuthenticationRepository.getInstance(), userInfoDataSource);

        InputPhoneActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.input_phone_activity);
        binding.setViewModel(viewModel);

        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.wait_signup_now));

        viewModel.setInfoFromIntent(getIntent());
        viewModel.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    protected void onDestroy() {
        viewModel.onDestroy();
        super.onDestroy();
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
    public void showRegisterFailed() {
        AlertUtils.showAlertDialog(this, R.string.signup_failed,
                R.string.signup_failed_message);
    }

    @Override
    public void showStartRegister() {
        progressDialog.show();
    }

    @Override
    public void showStopRegister() {
        progressDialog.dismiss();
    }

    @Override
    public void onCompleteLogin() {
        Intent intent = new Intent(this, InsertPhotoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showFailedLogin() {
        AlertUtils.showAlertDialog(this, R.string.login_not_match, R.string.login_not_match_message);
    }

    @Override
    public void showExpiredToken() {
        AlertUtils.showAlertDialog(this, R.string.session_expired, R.string.session_expired_message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        viewModel.logout();
                    }
                });
    }
}
