package com.entuition.wekend.view.main.activities.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.data.user.asynctask.ILoadUserInfoCallback;
import com.entuition.wekend.model.data.user.asynctask.LoadUserInfoTask;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.util.BigSizeImageLoadingListener;
import com.entuition.wekend.view.util.ChangeProfileImageObservable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2016. 1. 6..
 */
public class SettingProfileActivity extends AppCompatActivity {

    private static final String TAG = "SettingProfileActivity";

    private TextView txtNickname;
    private TextView txtAge;
    private TextView txtPhoneNumber;
    private TextView txtPinkBalloon;
    private TextView txtYellowBalloon;
    private TextView btnEdit;

    private ViewPager viewPager;
    private ProfileImageViewPager viewPagerAdapter;
    private LinearLayout pageIndicator;
    private int dotsCount = 0;
    private ImageView[] dots;

    private UserInfo userInfo;
    private ArrayList<String> profilePhotoList;

    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        Log.d(TAG, "onCreate");

        profilePhotoList = new ArrayList<String>();
        initViews();
        loadUserInfo();

        ChangeProfileImageObservable.getInstance().addObserver(new ChangeProfileImageObserver());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
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

        txtNickname = (TextView) findViewById(R.id.id_profile_nickname);
        txtAge = (TextView) findViewById(R.id.id_profile_age);
        txtPhoneNumber = (TextView) findViewById(R.id.id_profile_phone);
        txtPinkBalloon = (TextView) findViewById(R.id.id_profile_pink_balloon);
        txtYellowBalloon = (TextView) findViewById(R.id.id_profile_yellow_balloon);
        btnEdit = (TextView) findViewById(R.id.id_profile_edit_button);
        btnEdit.setText(getString(R.string.profile_edit));
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingProfileActivity.this, SettingEditProfileActivity.class);
                startActivity(intent);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.id_profile_viewpager);
        viewPagerAdapter = new ProfileImageViewPager();
//        viewPager.setAdapter(viewPagerAdapter);

        pageIndicator = (LinearLayout) findViewById(R.id.id_profile_viewpager_indicator);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.profile_default)
                .showImageOnFail(R.drawable.profile_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void loadUserInfo() {

        Log.d(TAG, "loadUserINfo");

        String userId = UserInfoDaoImpl.getInstance().getUserId(this);
        LoadUserInfoTask task = new LoadUserInfoTask();
        task.setCallback(new ILoadUserInfoCallback() {
            @Override
            public void onSuccess(UserInfo result) {
                userInfo = result;
                profilePhotoList = Utilities.asSortedArrayList(userInfo.getPhotos());

                setViews();
            }

            @Override
            public void onFailed() {
                // TODO : failed exceptions~
            }
        });

        task.execute(userId);
    }

    private void setViews() {

        Log.d(TAG, "setViews > userINfo is null : " + (userInfo == null));

        String nickname = userInfo.getNickname();
        if (nickname != null) txtNickname.setText(nickname);

        String phoneNumber = userInfo.getPhone();
        if (phoneNumber != null) txtPhoneNumber.setText(phoneNumber);

        int birthYear = userInfo.getBirth();
        txtAge.setText(String.valueOf(Utilities.getAgeFromBirthYear(birthYear)));

        txtPinkBalloon.setText(getString(R.string.profile_pink_balloon) + " : " + userInfo.getBalloon());
        txtYellowBalloon.setText(getString(R.string.profile_yellow_balloon) + " : " + userInfo.getBalloon());

        viewPager.setAdapter(viewPagerAdapter);
//        viewPagerAdapter.notifyDataSetChanged();

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        if (pageIndicator.getChildCount() > 0) {
            pageIndicator.removeAllViews();
        }

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

    }

    private class ProfileImageViewPager extends PagerAdapter {

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
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Log.d(TAG, "instantiateItem > position : " + position);

            View view = getLayoutInflater().inflate(R.layout.campaign_pager_item, container, false);
            view.setTag(position);
            container.addView(view);

            String photoUrl = S3Utils.getS3Url(Constants.PROFILE_IMAGE_BUCKET_NAME, profilePhotoList.get(position));

            final ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);
            ImageLoader.getInstance().displayImage(photoUrl, imageView, options, new BigSizeImageLoadingListener(profilePhotoList.get(position)));

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class ChangeProfileImageObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Bitmap bitmap = (Bitmap) data;

            View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
            ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);
            imageView.setImageBitmap(bitmap);
        }
    }
}
