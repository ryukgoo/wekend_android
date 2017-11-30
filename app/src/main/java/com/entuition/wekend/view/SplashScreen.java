package com.entuition.wekend.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.authentication.AuthenticationRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.SplashScreenActivityBinding;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.join.LoginActivity;
import com.entuition.wekend.view.join.viewmodel.LaunchNavigator;
import com.entuition.wekend.view.join.viewmodel.LaunchViewModel;
import com.entuition.wekend.view.main.campaign.CampaignDetailActivity;
import com.entuition.wekend.view.main.container.ContainerActivity;

/**
 * Created by Kim on 2015-08-04.
 */
public class SplashScreen extends AppCompatActivity implements LaunchNavigator {

    private static final String TAG = SplashScreen.class.getSimpleName();

    private LaunchViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserInfoDataSource userInfoDataSource = UserInfoRepository.getInstance(this);

        model = new LaunchViewModel(this, this, AuthenticationRepository.getInstance(), userInfoDataSource);

        SplashScreenActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.splash_screen_activity);
        binding.setModel(model);

        model.parseIntent(getIntent());
        model.onCreate();
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

    @Override
    public void onAutoLogin(int type) {
        Intent intent = new Intent(this, ContainerActivity.class);
        intent.putExtra(Constants.START_ACTIVITY_POSITION, type);
        startActivity(intent);
    }

    @Override
    public void onLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onReceiveLink(int productId) {
        Intent intent = new Intent(this, CampaignDetailActivity.class);
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, productId);
        startActivity(intent);
    }
}
