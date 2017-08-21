package com.entuition.wekend.view.join;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.user.asynctask.CheckNicknameTask;
import com.entuition.wekend.model.data.user.asynctask.ICheckNicknameCallback;
import com.entuition.wekend.view.util.WekendNumberPicker;

/**
 * Created by Kim on 2015-08-17.
 */
public class InsertUserInfoActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener {

    private static final String TAG = "InsertUserInfoActivity";

    private EditText txtNickname;
    private Button buttonCheckDuplicate;
    private Button buttonMale;
    private Button buttonFemale;
    private WekendNumberPicker pickerBirth;
    private Button buttonNext;
    private Button selectedButton;
    private FrameLayout progressbarHolder;

    private String gender;
    private boolean isAvailableNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_userinfo);

        initView();
    }

    private void initView() {
        txtNickname = (EditText) findViewById(R.id.id_nickname);
        txtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Utilities.isValidNicknameExpression(s.toString())) {
                    buttonCheckDuplicate.setEnabled(true);
                } else {
                    buttonCheckDuplicate.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonCheckDuplicate = (Button) findViewById(R.id.id_button_check_duplicate);
        buttonCheckDuplicate.setOnClickListener(this);

        buttonMale = (Button) findViewById(R.id.id_button_choose_male);
        buttonMale.setOnClickListener(this);
        selectedButton = buttonMale;
        buttonMale.setSelected(true);

        buttonFemale = (Button) findViewById(R.id.id_button_choose_female);
        buttonFemale.setOnClickListener(this);
        gender = Constants.VALUE_GENDER_MALE;

        buttonNext = (Button) findViewById(R.id.id_button_insert_userinfo_next);
        buttonNext.setOnClickListener(this);

        progressbarHolder = (FrameLayout) findViewById(R.id.id_screen_dim_progress);
        progressbarHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { return false; }
        });


        pickerBirth = (WekendNumberPicker) findViewById(R.id.id_birth_year_picker);
        int thisYear = Utilities.getThisYear();
        pickerBirth.setMaxValue(thisYear);
        pickerBirth.setMinValue(thisYear - 100);
        pickerBirth.setWrapSelectorWheel(false);
        pickerBirth.setValue(thisYear - 20); // default : 20 years old

        // not recommended~
        setDividerColor(pickerBirth, Color.WHITE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.id_button_check_duplicate :
                Log.d(TAG, "Check nickname duplicated");
                String nickname = txtNickname.getText().toString();

                new CheckNicknameTask(new CheckNicknameCallback()).execute(nickname);

                buttonMale.requestFocus();
                // hide IME..
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonMale.getWindowToken(), 0);

                break;

            case R.id.id_button_choose_male :

                Log.d(TAG, "button male clicked!!!");

                gender = Constants.VALUE_GENDER_MALE;
                if (selectedButton != null) selectedButton.setSelected(false);
                buttonMale.setSelected(true);
                selectedButton = buttonMale;
                break;

            case R.id.id_button_choose_female :

                Log.d(TAG, "button female clicked!!!");

                gender = Constants.VALUE_GENDER_FEMALE;
                if (selectedButton != null) selectedButton.setSelected(false);
                buttonFemale.setSelected(true);
                selectedButton = buttonFemale;
                break;

            case R.id.id_button_insert_userinfo_next :
                if (checkValidateInput()) {
                    Intent intent = new Intent(InsertUserInfoActivity.this, InputPhoneNumberActivity.class);
                    intent.putExtra(Constants.PARAMETER_USERNAME, getIntent().getStringExtra(Constants.PARAMETER_USERNAME));
                    intent.putExtra(Constants.PARAMETER_PASSWORD, getIntent().getStringExtra(Constants.PARAMETER_PASSWORD));
                    intent.putExtra(Constants.PARAMETER_NICKNAME, txtNickname.getText().toString());
                    intent.putExtra(Constants.PARAMETER_GENDER, gender);
                    intent.putExtra(Constants.PARAMETER_BIRTH, pickerBirth.getValue());
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * native Numberpick divider change....(Warning!!!)
     * @param picker
     * @param color
     */
    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {

                } catch (Resources.NotFoundException e) {

                } catch (IllegalAccessException e) {

                }
                break;
            }
        }

    }

    private boolean checkValidateInput() {

        if (!isAvailableNickname) {
            new AlertDialog.Builder(new ContextThemeWrapper(InsertUserInfoActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.insert_user_info_alert_title_nickname))
                    .setMessage(getString(R.string.insert_user_info_alert_message_nickname))
                    .setPositiveButton(R.string.dialog_positive_button, this)
                    .show();
            return false;
        } else if (gender == null) {
            new AlertDialog.Builder(new ContextThemeWrapper(InsertUserInfoActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.insert_user_info_alert_title_gender))
                    .setMessage(getString(R.string.insert_user_info_alert_message_gender))
                    .setPositiveButton(R.string.dialog_positive_button, this)
                    .show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private class CheckNicknameCallback implements ICheckNicknameCallback {

        @Override
        public void onPrepare() {
            progressbarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSuccess(boolean isAvailable) {

            progressbarHolder.setVisibility(View.GONE);

            isAvailableNickname = isAvailable;
            if (isAvailableNickname) {
                new AlertDialog.Builder(new ContextThemeWrapper(InsertUserInfoActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.insert_user_info_alert_title_duplicated_nickname))
                        .setMessage(getString(R.string.insert_user_info_alert_message_available_nickname))
                        .setPositiveButton(R.string.dialog_positive_button, InsertUserInfoActivity.this)
                        .show();
                buttonMale.requestFocus();
            } else {
                new AlertDialog.Builder(new ContextThemeWrapper(InsertUserInfoActivity.this, R.style.CustomAlertDialog))
                        .setTitle(InsertUserInfoActivity.this.getString(R.string.insert_user_info_alert_title_duplicated_nickname))
                        .setMessage(InsertUserInfoActivity.this.getString(R.string.insert_user_info_alert_message_duplicated_nickname))
                        .setPositiveButton(R.string.dialog_positive_button, InsertUserInfoActivity.this)
                        .show();
            }
        }

        @Override
        public void onFailed() {
            progressbarHolder.setVisibility(View.GONE);

            new AlertDialog.Builder(new ContextThemeWrapper(InsertUserInfoActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.insert_user_info_alert_title_wrong_nickname))
                    .setMessage(getString(R.string.insert_user_info_alert_message_wrong_nickname))
                    .setPositiveButton(R.string.dialog_positive_button, InsertUserInfoActivity.this)
                    .show();
        }
    }
}
