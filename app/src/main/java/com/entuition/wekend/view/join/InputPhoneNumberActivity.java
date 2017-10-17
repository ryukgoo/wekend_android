package com.entuition.wekend.view.join;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.authentication.asynctask.IAuthenticationCallback;
import com.entuition.wekend.model.authentication.asynctask.RequestVerificationCodeTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.view.common.WekendAbstractActivity;

/**
 * Created by Kim on 2015-08-17.
 */
public class InputPhoneNumberActivity extends WekendAbstractActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private EditText editTextPhoneNumber;
    private Button buttonRequestVerification;
    private EditText editTextVerification;
    private Button buttonConfirmVerification;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reinitialize(savedInstanceState)) return;
        setContentView(R.layout.activity_input_phonenumber);

        initView();
    }

    private void initView() {

        editTextPhoneNumber = (EditText) findViewById(R.id.id_input_phone_edittext_phonenumber);
        editTextPhoneNumber.addTextChangedListener(new PhoneNumberTextWatcher());
        editTextPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextPhoneNumber.setHint("");
                } else {
                    editTextPhoneNumber.setHint(getString(R.string.input_phonenumber_hint));
                }
            }
        });

        editTextVerification = (EditText) findViewById(R.id.id_input_phone_edittext_verification);
        editTextVerification.addTextChangedListener(new VerificationCodeTextWatcher());
        editTextVerification.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextVerification.setHint("");
                } else {
                    editTextVerification.setHint(getString(R.string.input_verification_hint));
                }
            }
        });

        buttonRequestVerification = (Button) findViewById(R.id.id_input_phone_button_request_verification);
        buttonConfirmVerification = (Button) findViewById(R.id.id_input_phone_button_next);
        buttonRequestVerification.setOnClickListener(this);
        buttonConfirmVerification.setOnClickListener(this);

        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.input_number_progressdialog_message));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.id_input_phone_button_request_verification:
                requestVerificationCode();
                break;

            case R.id.id_input_phone_button_next:
                String inputVerificationCode = editTextVerification.getText().toString();
                if (RequestVerificationCodeTask.validateVerificationCode(inputVerificationCode)) {
                    registerUserInfo();
                } else {
                    new AlertDialog.Builder(new ContextThemeWrapper(InputPhoneNumberActivity.this, R.style.CustomAlertDialog))
                            .setTitle(getString(R.string.input_number_dialog_not_match_title))
                            .setMessage(getString(R.string.input_number_dialog_not_match_message))
                            .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    private void requestVerificationCode() {
        String phoneNumber = editTextPhoneNumber.getText().toString();

        if (Utilities.isValidPhoneNumberExpression(phoneNumber)) {
            RequestVerificationCodeTask task = new RequestVerificationCodeTask();
            task.setCallback(new RequestVerificationCodeCallback());
            task.execute(phoneNumber);

        } else {
            new AlertDialog.Builder(new ContextThemeWrapper(InputPhoneNumberActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.insert_phonenumber_alert_title_wrong_number))
                    .setMessage(getString(R.string.insert_phonenumber_alert_message_wrong_number))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void registerUserInfo() {
        String username = getIntent().getStringExtra(Constants.PARAMETER_USERNAME);
        String password = getIntent().getStringExtra(Constants.PARAMETER_PASSWORD);
        String nickname = getIntent().getStringExtra(Constants.PARAMETER_NICKNAME);
        String gender = getIntent().getStringExtra(Constants.PARAMETER_GENDER);
        int birth = getIntent().getIntExtra(Constants.PARAMETER_BIRTH, Constants.DEFAULT_BIRTH_VALUE);
        String phone = editTextPhoneNumber.getText().toString();

        ((DeveloperAuthenticationProvider) CognitoSyncClientManager.getCredentialsProvider().getIdentityProvider())
                .register(username, password, nickname, gender, birth, phone, InputPhoneNumberActivity.this, new RegisterAuthenticationCallback());
        RequestVerificationCodeTask.clearVerificationCode();
    }

    private class RegisterAuthenticationCallback implements IAuthenticationCallback {

        @Override
        public void onPrepare() {
            Log.d(TAG, "onPrepare > RegisterAuthencation");
            progressDialog.show();
        }

        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess > RegisterAuthencation");
            progressDialog.dismiss();
        }

        @Override
        public void onFailed() {
            progressDialog.dismiss();
        }
    }

    private class RequestVerificationCodeCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() { }

        @Override
        public void onSuccess(@Nullable Object object) {

            new AlertDialog.Builder(new ContextThemeWrapper(InputPhoneNumberActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.confirm_verification_code))
                    .setMessage(getString(R.string.insert_phonenumber_alert_message_receive_verification_number))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            buttonRequestVerification.setEnabled(false);
                            editTextVerification.requestFocus();
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }

        @Override
        public void onFailed() {

            new AlertDialog.Builder(new ContextThemeWrapper(InputPhoneNumberActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.send_verification_code_failed_title))
                    .setMessage(getString(R.string.send_verification_code_failed_message))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }

    private class PhoneNumberTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Utilities.isValidPhoneNumberExpression(s.toString())) {
                buttonRequestVerification.setEnabled(true);
            } else {
                buttonRequestVerification.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private class VerificationCodeTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 6) {
                buttonConfirmVerification.setEnabled(true);
            } else {
                buttonConfirmVerification.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }
}
