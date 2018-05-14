package com.entuition.wekend.view.join;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.ConfirmPhoneActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.join.viewmodel.ConfirmPhoneNavigator;
import com.entuition.wekend.view.join.viewmodel.ConfirmPhoneViewModel;

/**
 * Created by ryukgoo on 2018. 2. 12..
 */

public class ConfirmPhoneActivity extends AppCompatActivity implements ConfirmPhoneNavigator {

    private static final String TAG = ConfirmPhoneActivity.class.getSimpleName();

    private ConfirmPhoneViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = getIntent().getStringExtra(Constants.ExtraKeys.USERNAME);
        String phone = getIntent().getStringExtra(Constants.ExtraKeys.PHONE);

        viewModel = new ConfirmPhoneViewModel(this, this, UserInfoRepository.getInstance(this));

        ConfirmPhoneActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.confirm_phone_activity);
        binding.setViewModel(viewModel);

        viewModel.onCreate();
        viewModel.setUserInfo(username, phone);
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
    public void onRequestComplete() {
        Log.d(TAG, "onRequestComplete");
        AlertUtils.showAlertDialog(this, R.string.confirm_verification,
                R.string.send_verification_message);
    }

    @Override
    public void onRequestFailed() {
        Log.d(TAG, "onRequestFailed");
        AlertUtils.showAlertDialog(this, R.string.send_verification_failed,
                R.string.send_verification_failed_message);
    }

    @Override
    public void onConfirmCode(final String userId, final String username) {
        Log.d(TAG, "onConfirmCode > username : " + username);

        new AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("아이디 찾기")
                .setMessage("\n해당 정보로 가입된 아이디는\n\n" + username + "\n\n입니다")
                .setPositiveButton("로그인하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Login");
                        Intent intent = new Intent(ConfirmPhoneActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.ExtraKeys.USERNAME, username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("비밀번호 찾기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "find password");

                        // TODO: reset password
                        onResetPassword(userId, username);
                    }
                })
                .show();
    }

    @Override
    public void onConfirmCodeFailed() {
        Log.d(TAG, "onConfirmFailed");
        AlertUtils.showAlertDialog(this, R.string.not_match_verification,
                R.string.not_match_verification_message);
    }

    @Override
    public void onNotFoundAccount() {
        Log.d(TAG, "onNotFoundAccount");
        AlertUtils.showAlertDialog(this, R.string.not_found_account_title,
                R.string.not_found_account_message);
    }

    @Override
    public void notMatchRegisteredPhone() {
        Log.d(TAG, "notMatchRegisteredPhone");
        AlertUtils.showAlertDialog(this, R.string.not_match_phone_title,
                R.string.not_match_phone_message);
    }

    @Override
    public void onResetPassword(String userId, String username) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        intent.putExtra(Constants.ExtraKeys.USER_ID, userId);
        intent.putExtra(Constants.ExtraKeys.USERNAME, username);
        startActivity(intent);
    }
}
