package com.entuition.wekend.view.join;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.authentication.AuthenticationRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.AgreementLayoutBinding;
import com.entuition.wekend.databinding.LoginActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.join.viewmodel.AgreementNavigator;
import com.entuition.wekend.view.join.viewmodel.AgreementViewModel;
import com.entuition.wekend.view.join.viewmodel.LoginNavigator;
import com.entuition.wekend.view.join.viewmodel.LoginViewModel;
import com.entuition.wekend.view.join.viewmodel.SignUpViewModel;
import com.entuition.wekend.view.join.viewmodel.SignUpViewNavigator;
import com.entuition.wekend.view.main.container.ContainerActivity;

/**
 * Created by Kim on 2015-08-04.
 */
public class LoginActivity extends AppCompatActivity implements LoginNavigator, SignUpViewNavigator, AgreementNavigator {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private PopupWindow popupWindow;
    private LoginViewModel loginViewModel;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoDataSource userInfoDataSource = UserInfoRepository.getInstance(this);

        loginViewModel = new LoginViewModel(this, this, AuthenticationRepository.getInstance(), userInfoDataSource);
        signUpViewModel = new SignUpViewModel(this, this);

        LoginActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.setLoginModel(loginViewModel);
        binding.setSignUpModel(signUpViewModel);

        loginViewModel.onCreate();
        signUpViewModel.onCreate();

        String username = getIntent().getStringExtra(Constants.ExtraKeys.USERNAME);
        if (username != null) { loginViewModel.email.set(username); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginViewModel.onResume();
        signUpViewModel.onResume();

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        loginViewModel.onPause();
        signUpViewModel.onPause();
    }

    @Override
    protected void onDestroy() {
        loginViewModel.onDestroy();
        signUpViewModel.onDestroy();

        if (popupWindow != null) {
            popupWindow = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCompleteLogin() {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConfirmAgreement() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onShowAgreement() {
        AgreementViewModel agreementViewModel = new AgreementViewModel(this, this);

        AgreementLayoutBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.agreement_layout, null, false);
        binding.setModel(agreementViewModel);

        agreementViewModel.onCreate();

        popupWindow = new PopupWindow(binding.getRoot(), LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(-1);
        popupWindow.showAtLocation(binding.getRoot(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onFindAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setItems(R.array.find_account_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0) {
                    Intent intent = new Intent(LoginActivity.this, ConfirmPhoneActivity.class);
                    startActivity(intent);
                } else if (which == 1) {
                    Intent intent = new Intent(LoginActivity.this, ConfirmAccountActivity.class);
                    startActivity(intent);
                } else if (which == 2) {
                    Uri uri = Uri.parse("mailto:entuitiondevelop@gmail.com");
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_cc_mail_subject));
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_cc_mail_text, ""));
                    startActivity(intent);
                }
            }
        });
        builder.show();
    }

    @Override
    public void showInvalidInput() {
        AlertUtils.showAlertDialog(this, R.string.login_not_match, R.string.login_not_match_message);
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
                        loginViewModel.logout();
                    }
                });
    }
}
