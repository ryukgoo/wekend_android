package com.entuition.wekend.view.join;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.ConfirmAccountActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.join.viewmodel.ConfirmAccountNavigator;
import com.entuition.wekend.view.join.viewmodel.ConfirmAccountViewModel;

/**
 * Created by ryukgoo on 2018. 2. 22..
 */

public class ConfirmAccountActivity extends AppCompatActivity implements ConfirmAccountNavigator {

    private static final String TAG = ConfirmAccountActivity.class.getSimpleName();

    private ConfirmAccountViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ConfirmAccountViewModel(this, this,
                UserInfoRepository.getInstance(this));

        ConfirmAccountActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.confirm_account_activity);
        binding.setViewModel(viewModel);

        viewModel.onCreate();
    }

    @Override
    public void onConfirmAccount(UserInfo userInfo) {
        Log.d(TAG, "onConfirmAccount");

        Intent intent = new Intent(this, ConfirmPhoneActivity.class);
        intent.putExtra(Constants.ExtraKeys.USERNAME, userInfo.getUsername());
        intent.putExtra(Constants.ExtraKeys.PHONE, userInfo.getPhone());
        startActivity(intent);
    }

    @Override
    public void onConfirmAccountFailed() {
        AlertUtils.showAlertDialog(this, R.string.not_found_account_title,
                R.string.not_found_account_message);
    }
}
