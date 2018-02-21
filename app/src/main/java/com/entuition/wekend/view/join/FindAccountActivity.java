package com.entuition.wekend.view.join;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.FindAccountActivityBinding;
import com.entuition.wekend.view.join.viewmodel.FindAccountViewModel;

/**
 * Created by ryukgoo on 2018. 2. 12..
 */

public class FindAccountActivity extends AppCompatActivity {

    private static final String TAG = FindAccountActivity.class.getSimpleName();

    private FindAccountViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new FindAccountViewModel(this, UserInfoRepository.getInstance(this));

        FindAccountActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.find_account_activity);
        binding.setViewModel(viewModel);

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
}
