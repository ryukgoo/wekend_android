package com.entuition.wekend.view.main.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.product.asynctask.ILoadUserInfoAndProductInfoCallback;
import com.entuition.wekend.model.data.product.asynctask.LoadUserInfoAndProductInfoTask;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.WekendActivity;
import com.entuition.wekend.view.util.BigSizeImageLoadingListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 4. 18..
 */
public abstract class ProposeProfileActivity extends WekendActivity implements ViewPager.OnPageChangeListener {

    private final String TAG = getClass().getSimpleName();

    private TextView textNickname;
    private TextView textAge;
    private TextView textCampaignDescription;

    private ViewPager viewPager;
    private FriendProfileViewpagerAdapter viewpagerAdapter;
    private LinearLayout pageIndicator;
    private int dotsCount = 0;
    private ImageView[] dots;

    protected LinearLayout phoneLayout;
    protected TextView textPhone;
    protected TextView textPoint;
    protected TextView textMatchResult;
    protected FrameLayout sendButtons;
    protected TextView buttonPropose;
    protected LinearLayout receiveButtons;
    protected Button buttonAccept;
    protected Button buttonReject;
    protected ProgressBar progressBarPropose;

    protected int productId;
    protected String friendUserId;
    protected String senderId;
    protected String receiverId;
    protected String updatedTime;

    protected ProductInfo friendProductInfo;
    protected UserInfo friendUserInfo;

    private ArrayList<String> profilePhotoList;
    private DisplayImageOptions displayOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (reinitialize(savedInstanceState)) return;

        setContentView(R.layout.activity_propose_profile);

        productId = getIntent().getIntExtra(Constants.PARAMETER_PRODUCT_ID, -1);
        senderId = getIntent().getStringExtra(Constants.PARAMETER_PROPOSER_ID);
        receiverId = getIntent().getStringExtra(Constants.PARAMETER_PROPOSEE_ID);
        friendUserId = getIntent().getStringExtra(Constants.PARAMETER_FRIEND_ID);
        updatedTime = getIntent().getStringExtra(Constants.PARAMETER_PROPOSE_UPDATEDTIME);

        profilePhotoList = new ArrayList<String>();

        initViews();
        onInitViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadUserInfoAndProductInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textNickname = (TextView) findViewById(R.id.id_profile_nickname);
        textAge = (TextView) findViewById(R.id.id_profile_age);
        textCampaignDescription = (TextView) findViewById(R.id.id_friend_profile_campaign_description);
        buttonPropose = (TextView) findViewById(R.id.id_progress_button_text);
        buttonPropose.setText(getString(R.string.profile_propose_button));
        progressBarPropose = (ProgressBar) findViewById(R.id.id_progress_button_progressbar);
        phoneLayout = (LinearLayout) findViewById(R.id.id_friend_profile_phone_layout);
        textPhone = (TextView) findViewById(R.id.id_profile_phone);

        textPoint = (TextView) findViewById(R.id.id_textview_profile_point);
        textMatchResult = (TextView) findViewById(R.id.id_text_friend_profile_match_result);
        sendButtons = (FrameLayout) findViewById(R.id.id_layout_friend_profile_button_send);
        receiveButtons = (LinearLayout) findViewById(R.id.id_layout_friend_profile_button_receive);
        buttonAccept = (Button) findViewById(R.id.id_button_friend_profile_ok);
        buttonReject = (Button) findViewById(R.id.id_button_friend_profile_reject);

        viewPager = (ViewPager) findViewById(R.id.id_profile_viewpager);
        viewpagerAdapter = new FriendProfileViewpagerAdapter();
//        viewPager.setAdapter(viewpagerAdapter);

        pageIndicator = (LinearLayout) findViewById(R.id.id_profile_viewpager_indicator);

        displayOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.profile_default)
                .showImageOnFail(R.drawable.profile_default)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void loadUserInfoAndProductInfo() {

        Log.d(TAG, "loadUserInfoAndProductInfo started");

        LikeDBItem likeDBItem = new LikeDBItem();
        likeDBItem.setUserId(friendUserId);
        likeDBItem.setProductId(productId);

        LoadUserInfoAndProductInfoTask task = new LoadUserInfoAndProductInfoTask();
        task.setCallback(new LoadUserInfoAndProductInfoCallback());
        task.execute(likeDBItem);
    }

    private void setViews() {

        Log.d(TAG, "setViews!!!!!");

        textNickname.setText(friendUserInfo.getNickname());
        textPhone.setText(friendUserInfo.getPhone());
        textAge.setText(String.valueOf(Utilities.getAgeFromBirthYear(friendUserInfo.getBirth())));

        String description = friendProductInfo.getTitleKor() + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE
                + friendProductInfo.getDescription();

        textCampaignDescription.setText(Html.fromHtml(description));

        viewPager.setAdapter(viewpagerAdapter);
//        viewpagerAdapter.notifyDataSetChanged();

        dotsCount = viewpagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0 ; i < dotsCount ; i ++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.drawable_indicator_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(7, 0, 7, 0);

            pageIndicator.addView(dots[i], params);
        }

        if (dotsCount > 0) dots[0].setSelected(true);

        findViewById(R.id.id_profile_appbar).setVisibility(View.VISIBLE);
        findViewById(R.id.id_profile_description).setVisibility(View.VISIBLE);

        UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(UserInfoDaoImpl.getInstance().getUserId(this));

        textPoint.setText(getString(R.string.profile_owned_point) + " " + userInfo.getBalloon() + "P");

        setListeners();
    }

    protected abstract void setListeners();

    protected abstract void onInitViews();

    protected abstract void onSuccessGetProposeStatus(String proposeStatus);

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0 ; i < dotsCount ; i ++) {
            dots[i].setSelected(false);
        }

        dots[position].setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class LoadUserInfoAndProductInfoCallback implements ILoadUserInfoAndProductInfoCallback {

        @Override
        public void onPrePared() {

        }

        @Override
        public void onSuccess(UserInfo userInfo, ProductInfo productInfo) {
            friendUserInfo = userInfo;
            friendProductInfo = productInfo;

            if (userInfo.getPhotos() != null) {
                Log.d(TAG, "onSuccess > userInfo.getPhotos() : " + userInfo.getPhotos().toString());
                profilePhotoList = Utilities.asSortedArrayList(userInfo.getPhotos());

                Log.d(TAG, "onSuccess > userInfo.getPhotos().size() : " + userInfo.getPhotos().size());
            } else {
                profilePhotoList = new ArrayList<String>();
            }

            setViews();
        }

        @Override
        public void onFailed() {
            Log.d(TAG, "onFailed !!!!!!");
            // TODO : Exceptions!
        }
    }

    private class FriendProfileViewpagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Math.max(profilePhotoList.size(), 1);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Item : " + position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Log.d(TAG, "instantiateItem > position : " + position);
            Log.d(TAG, "instantiateItem > profilePhotoList.size() : " + profilePhotoList.size());

            View view = getLayoutInflater().inflate(R.layout.campaign_pager_item, container, false);
            view.setTag(position);
            container.addView(view);

            ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);

            if (profilePhotoList == null || profilePhotoList.size() == 0) {
                imageView.setImageResource(R.drawable.profile_default);
            } else {

                Log.d(TAG, "instantiateItem > position : " + position);

                String photoUrl = S3Utils.getS3Url(Constants.PROFILE_IMAGE_BUCKET_NAME, profilePhotoList.get(position));
                ImageLoader.getInstance().displayImage(photoUrl, imageView, displayOptions, new BigSizeImageLoadingListener(profilePhotoList.get(position)));
            }

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}