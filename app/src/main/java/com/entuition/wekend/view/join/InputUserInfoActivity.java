package com.entuition.wekend.view.join;

import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.InputUserInfoActivityBinding;
import com.entuition.wekend.util.ActivityUtils;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.join.viewmodel.InputUserInfoNavigator;
import com.entuition.wekend.view.join.viewmodel.InputUserInfoViewModel;

/**
 * Created by Kim on 2015-08-17.
 */
public class InputUserInfoActivity extends AppCompatActivity implements InputUserInfoNavigator {

    private static final String TAG = InputUserInfoActivity.class.getSimpleName();

    private NumberPicker birthPicker;
    private InputUserInfoViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoDataSource userInfoDataSource = UserInfoRepository.getInstance(this);

        model = new InputUserInfoViewModel(this, this, userInfoDataSource);

        InputUserInfoActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.input_user_info_activity);
        setNumberPicker(binding.birthPicker);
        binding.setModel(model);

        model.onCreate();
    }

    @Override
    public void onClickNextButton(@NonNull String nickname, @NonNull String gender) {
        Intent intent = new Intent(this, InputPhoneNumberActivity.class);
        intent.putExtra(Constants.ExtraKeys.USERNAME, getIntent().getStringExtra(Constants.ExtraKeys.USERNAME));
        intent.putExtra(Constants.ExtraKeys.PASSWORD, getIntent().getStringExtra(Constants.ExtraKeys.PASSWORD));
        intent.putExtra(Constants.ExtraKeys.NICKNAME, nickname);
        intent.putExtra(Constants.ExtraKeys.GENDER, gender);
        intent.putExtra(Constants.ExtraKeys.BIRTH, birthPicker.getValue());
        startActivity(intent);
    }

    @Override
    public void showInvalidNickname() {
        AlertUtils.showAlertDialog(this, R.string.confirm_input_nickname,
                R.string.confirm_input_nickname_message);
    }

    @Override
    public void showDuplicatedNickname() {
        AlertUtils.showAlertDialog(this, R.string.confirm_duplicated_nickname,
                R.string.confirm_duplicated_nickname_invalid);
    }

    @Override
    public void showAvailableNickname() {
        AlertUtils.showAlertDialog(this, R.string.confirm_duplicated_nickname,
                R.string.confirm_duplicated_nickname_available);
    }

    @Override
    public void hideKeyboard() {
        ActivityUtils.hideKeyboard(this);
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

    private void setNumberPicker(NumberPicker picker) {
        birthPicker = picker;

        int thisYear = Utilities.getThisYear();
        picker.setMaxValue(thisYear - 20);
        picker.setMinValue(thisYear - 100);
        picker.setWrapSelectorWheel(false);
        picker.setValue(thisYear - 20); // default : 20 years old

        // not recommended~
        setDividerColor(picker, Color.WHITE);
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
}
