package com.entuition.wekend.view.join;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.entuition.wekend.R;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.authentication.asynctask.IAuthenticationCallback;

import static com.entuition.wekend.R.id.id_text_agree_agreement;

/**
 * Created by Kim on 2015-08-04.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private final String TAG = getClass().getSimpleName();

    private EditText txtUsername;
    private EditText txtPassword;

    private Button btnLogin;
    private Button btnSignUp;

    private FrameLayout progressbarHolder;

    private PopupWindow popupWindow;
    private CheckBox agreementCheckBox;
    private TextView agreementCheckText;
    private CheckBox policyCheckBox;
    private TextView policyCheckText;
    private TextView agreementDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        txtUsername = (EditText) findViewById(R.id.id_user_name);
        txtPassword = (EditText) findViewById(R.id.id_user_password);
        btnLogin = (Button) findViewById(R.id.id_button_login);
        btnSignUp = (Button) findViewById(R.id.id_button_signup);

        progressbarHolder = (FrameLayout) findViewById(R.id.id_screen_dim_progress);

        txtUsername.addTextChangedListener(this);
        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtUsername.setHint("");
                } else {
                    txtUsername.setHint(getString(R.string.register_activity_email_hint));
                }
            }
        });

        txtPassword.addTextChangedListener(this);
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtPassword.setHint("");
                } else {
                    txtPassword.setHint(getString(R.string.register_activity_password_hint));
                }
            }
        });

        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_button_login :
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username or Password cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
//                    CognitoSyncClientManager.getCredentialsProvider().clearCredentials();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);

                    ((DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider())
                            .login(username, password, LoginActivity.this, new LoginAuthenticationCallback());
                }
                break;
            case R.id.id_button_signup :
                createPopupView();

                break;

            case R.id.id_text_agree_agreement :
                if (agreementCheckBox != null) agreementCheckBox.setChecked(!agreementCheckBox.isChecked());

                Log.d(TAG, "agreementCheckBox > enable : " + agreementCheckBox.isChecked());

                if (agreementCheckBox.isChecked() && policyCheckBox.isChecked()) {
                    agreementDone.setEnabled(true);
                } else {
                    agreementDone.setEnabled(false);
                }

                break;

            case R.id.id_text_agree_privacy_policy :
                if (policyCheckBox != null) policyCheckBox.setChecked(!policyCheckBox.isChecked());

                Log.d(TAG, "policyCheckBox > enable : " + policyCheckBox.isChecked());

                if (agreementCheckBox.isChecked() && policyCheckBox.isChecked()) {
                    agreementDone.setEnabled(true);
                } else {
                    agreementDone.setEnabled(false);
                }

                break;

            case R.id.id_checkbox_agreement :
            case R.id.id_checkbox_privacy_policy :

                if (agreementCheckBox.isChecked() && policyCheckBox.isChecked()) {
                    agreementDone.setEnabled(true);
                } else {
                    agreementDone.setEnabled(false);
                }

                break;

            case R.id.id_button_agreement_done :

                Log.d(TAG, "agreement Button Done!!!!");

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

//                popupWindow.dismiss();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (Utilities.isValidEmailExpression(txtUsername.getText().toString())
                && Utilities.isValidPasswordExpression(txtPassword.getText().toString())) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void createPopupView() {

        View popupView = getLayoutInflater().inflate(R.layout.layout_popupwindow_agreement, null);

        TextView agreementText = (TextView) popupView.findViewById(R.id.id_text_agreement);
        agreementText.setText(Html.fromHtml(getString(R.string.text_agreement)));
        TextView policyText = (TextView) popupView.findViewById(R.id.id_text_privacy_policy);
        policyText.setText(Html.fromHtml(getString(R.string.privacy_policy)));

        agreementCheckBox = (CheckBox) popupView.findViewById(R.id.id_checkbox_agreement);
        agreementCheckText = (TextView) popupView.findViewById(id_text_agree_agreement);

        agreementCheckBox.setOnClickListener(this);
        agreementCheckText.setOnClickListener(this);

        policyCheckBox = (CheckBox) popupView.findViewById(R.id.id_checkbox_privacy_policy);
        policyCheckText = (TextView) popupView.findViewById(R.id.id_text_agree_privacy_policy);

        policyCheckBox.setOnClickListener(this);
        policyCheckText.setOnClickListener(this);

        agreementDone = (TextView) popupView.findViewById(R.id.id_button_agreement_done);
        agreementDone.setOnClickListener(this);
        agreementDone.setEnabled(false);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(-1);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private class LoginAuthenticationCallback implements IAuthenticationCallback {

        @Override
        public void onPrepare() {
            progressbarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess() {
            progressbarHolder.setVisibility(View.GONE);
        }

        @Override
        public void onFailed() {
            progressbarHolder.setVisibility(View.GONE);
        }
    }
}
