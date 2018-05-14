package com.entuition.wekend.view.join;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.authentication.AuthenticationRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.ResetPasswordActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.join.viewmodel.ResetPasswordNavigator;
import com.entuition.wekend.view.join.viewmodel.ResetPasswordViewModel;

/**
 * Created by ryukgoo on 2018. 2. 22..
 */

public class ResetPasswordActivity extends AppCompatActivity implements ResetPasswordNavigator {

    private static final String TAG = ResetPasswordActivity.class.getSimpleName();

    private ResetPasswordViewModel viewModel;

    private String username;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userId = getIntent().getStringExtra(Constants.ExtraKeys.USER_ID);
        username = getIntent().getStringExtra(Constants.ExtraKeys.USERNAME);

        viewModel = new ResetPasswordViewModel(this, this, UserInfoRepository.getInstance(this),
                AuthenticationRepository.getInstance());

        ResetPasswordActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.reset_password_activity);
        binding.setViewModel(viewModel);

        viewModel.onCreate();
        viewModel.setUserInfo(userId, username);
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
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public void notValidPasswordExpression() {
        AlertUtils.showAlertDialog(this, R.string.not_valid_password_expression_title,
                R.string.not_valid_password_expression_message);
    }

    @Override
    public void notMatchConfirmPassword() {
        AlertUtils.showAlertDialog(this, R.string.not_match_confirm_password_title,
                R.string.not_match_confirm_password_message);
    }

    @Override
    public void onResetPasswordComplete(final String username) {
        AlertUtils.showAlertDialog(this, R.string.reset_password_complete_title,
                R.string.reset_password_complete_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.ExtraKeys.USERNAME, username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }
}
