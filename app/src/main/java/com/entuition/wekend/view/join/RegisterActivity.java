package com.entuition.wekend.view.join;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.asynctask.CheckAccountTask;
import com.entuition.wekend.view.common.WekendAbstractActivity;

/**
 * Created by Kim on 2015-08-07.
 */
public class RegisterActivity extends WekendAbstractActivity implements View.OnClickListener, DialogInterface.OnClickListener, TextWatcher {

    private static final String TAG = "RegisterActivity";

    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtPasswordConfirm;
    private Button btnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reinitialize(savedInstanceState)) return;
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        txtUsername = (EditText) findViewById(R.id.id_register_username);
        txtPassword = (EditText) findViewById(R.id.id_register_password);
        txtPasswordConfirm = (EditText) findViewById(R.id.id_register_password_confirm);

        txtUsername.addTextChangedListener(this);
        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtUsername.setHint("");
                } else {
                    txtUsername.setHint(getString(R.string.register_activity_email_hint));
                    if (!Utilities.isValidEmailExpression(txtUsername.getText().toString())) {
                        showInvalidEmailDialog();
                    }
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
                    if (!Utilities.isValidPasswordExpression(txtPassword.getText().toString())) {
                        showInvalidPasswordDialog();
                    }
                }
            }
        });

        txtPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    txtPasswordConfirm.setHint("");
                } else {
                    txtPasswordConfirm.setHint(getString(R.string.register_activity_password_confirm_hint));

                    final String password = txtPassword.getText().toString();
                    String passwordConfirm = txtPasswordConfirm.getText().toString();
                    if (passwordConfirm != null && passwordConfirm.length() > 0 && !password.equals(passwordConfirm)) {
                        showNotEqualPasswordDialog();
                    }
                }
            }
        });

        btnNext = (Button) findViewById(R.id.id_button_register_next);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_button_register_next :
                final String username = txtUsername.getText().toString();
                final String password = txtPassword.getText().toString();
                String passwordConfirm = txtPasswordConfirm.getText().toString();

                if (!Utilities.isValidEmailExpression(username)) { // validate Username Email Expression
                    showInvalidEmailDialog();
                } else if (!Utilities.isValidPasswordExpression(password)) { // validate password Expression
                    showInvalidPasswordDialog();
                } else if (!password.equals(passwordConfirm)) { // confirm password
                    showNotEqualPasswordDialog();
                } else {

                    CheckAccountTask task = new CheckAccountTask(new ISimpleTaskCallback() {
                        @Override
                        public void onPrepare() { }

                        @Override
                        public void onSuccess(@Nullable Object object) {

                            boolean isValidAccount = (boolean) object;

                            if (isValidAccount) {
                                Intent intent = new Intent(RegisterActivity.this, InsertUserInfoActivity.class);
                                intent.putExtra(Constants.PARAMETER_USERNAME, username);
                                intent.putExtra(Constants.PARAMETER_PASSWORD, password);
                                startActivity(intent);
                            } else {
                                showUsedEmailDialog();
                            }
                        }

                        @Override
                        public void onFailed() {
                            showUsedEmailDialog();
                        }
                    });
                    task.execute(username);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (Utilities.isValidEmailExpression(txtUsername.getText().toString())
                && Utilities.isValidPasswordExpression(txtPassword.getText().toString())) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
//        Log.d(TAG, "afterTextChanged > s : " + s.toString());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        Log.d(TAG, "onClickDialog > which : " + which);

        dialog.cancel();
    }

    private void showInvalidEmailDialog() {
        Log.d(TAG, "showInvalidEmailDialog");
        new AlertDialog.Builder(new ContextThemeWrapper(RegisterActivity.this, R.style.CustomAlertDialog))
                .setTitle(getString(R.string.register_activity_alert_title_invalid_email))
                .setMessage(getString(R.string.register_activity_alert_message_invalid_email))
                .setPositiveButton(R.string.dialog_positive_button, this)
                .show();
    }

    private void showInvalidPasswordDialog() {
        Log.d(TAG, "showInvalidPasswordDialog");
        new AlertDialog.Builder(new ContextThemeWrapper(RegisterActivity.this, R.style.CustomAlertDialog))
                .setTitle(getString(R.string.register_activity_alert_title_invalid_password))
                .setMessage(getString(R.string.register_activity_alert_message_invalid_password))
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void showNotEqualPasswordDialog() {
        Log.d(TAG, "showNotEqualPasswordDialog");
        new AlertDialog.Builder(new ContextThemeWrapper(RegisterActivity.this, R.style.CustomAlertDialog))
                .setTitle(getString(R.string.register_activity_alert_title_password_confirm))
                .setMessage(getString(R.string.register_activity_alert_message_password_confirm))
                .setPositiveButton(R.string.dialog_positive_button, this)
                .show();
    }

    private void showUsedEmailDialog() {
        Log.d(TAG, "showUsedEmailDialog");
        new AlertDialog.Builder(new ContextThemeWrapper(RegisterActivity.this, R.style.CustomAlertDialog))
                .setTitle(getString(R.string.register_activity_alert_title_used_email))
                .setMessage(getString(R.string.register_activity_alert_message_used_email))
                .setPositiveButton(R.string.dialog_positive_button, this)
                .show();
    }
}