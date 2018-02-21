package com.entuition.wekend.view.main.campaign;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.like.LikeInfoRepository;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.product.ProductInfoRepository;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.CampaignDetailActivityBinding;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.main.campaign.adapter.CampaignDetailAdapter;
import com.entuition.wekend.view.main.campaign.viewmodel.CampaignDetailNavigator;
import com.entuition.wekend.view.main.campaign.viewmodel.CampaignDetailViewModel;
import com.entuition.wekend.view.main.likelist.RecommendFriendListActivity;

/**
 * Created by ryukgoo on 2016. 8. 4..
 */
public class CampaignDetailActivity extends AppCompatActivity implements CampaignDetailNavigator {

    public static final String TAG = CampaignDetailActivity.class.getSimpleName();

    private CampaignDetailActivityBinding binding;
    private CampaignDetailViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int productId = getIntent().getIntExtra(Constants.ExtraKeys.PRODUCT_ID, -1);
        boolean show = getIntent().getBooleanExtra(Constants.ExtraKeys.PRODUCT_SHOW, true);

        ProductInfoDataSource dataSource = ProductInfoRepository.getInstance(this);

        binding = DataBindingUtil.setContentView(this, R.layout.campaign_detail_activity);
        model = new CampaignDetailViewModel(this, this, productId,
                UserInfoRepository.getInstance(this), dataSource, LikeInfoRepository.getInstance(this));

        setupToolbar();
        setupViewPager();

        binding.setModel(model);
        binding.setIsShow(show);
        model.setFacebookShare(this);

        model.onCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_campaign_detail, menu);
        MenuItem shareForKakao = menu.findItem(R.id.id_action_share_kakao);
        shareForKakao.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                model.sendKakaoTalkLink(CampaignDetailActivity.this);
                return false;
            }
        });

        MenuItem shareForFacebook = menu.findItem(R.id.id_action_share_facebook);
        shareForFacebook.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                model.sendFacebookLink();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        model.onActivityResult(requestCode, resultCode, data);
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
    public void gotoGoogleMapView(String title, String address) {
        Intent mapIntent = new Intent(this, GoogleMapsActivity.class);
        mapIntent.putExtra(Constants.ExtraKeys.PRODUCT_TITLE, title);
        mapIntent.putExtra(Constants.ExtraKeys.MAP_ADDRESS, address);
        startActivity(mapIntent);
    }

    @Override
    public void gotoRecommendFriendView(int productId) {
        Intent intent = new Intent(this, RecommendFriendListActivity.class);
        intent.putExtra(Constants.ExtraKeys.PRODUCT_ID, productId);
        startActivity(intent);
    }

    @Override
    public void call(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phone.trim()));
        startActivity(callIntent);
    }

    @Override
    public void onCancelFacebookLink() {

    }

    @Override
    public void onErrorFacebookLink() {
        AlertUtils.showAlertDialog(this, R.string.campaign_share_failed,
                R.string.campaign_share_failed_message);
    }

    @Override
    public void onFailedAddLike() {

    }

    private void setupToolbar() {
        setSupportActionBar(binding.campaignDetailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupViewPager() {
        CampaignDetailAdapter adapter = new CampaignDetailAdapter(new ProductInfo());
        binding.campaignDetailViewpager.setAdapter(adapter);
        binding.campaignDetailViewpager.addOnPageChangeListener(binding.campaignDetailIndicator);
    }
}
