package com.entuition.wekend.view.main.container;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.entuition.wekend.R;
import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.source.product.ProductInfoRepository;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.databinding.ContainerActivityBinding;
import com.entuition.wekend.databinding.ProfileHeaderLayoutBinding;
import com.entuition.wekend.util.ActivityUtils;
import com.entuition.wekend.util.AlertUtils;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.common.AbstractFragment;
import com.entuition.wekend.view.main.campaign.CampaignListFragment;
import com.entuition.wekend.view.main.container.viewmodel.ContainerNavigator;
import com.entuition.wekend.view.main.container.viewmodel.ContainerViewModel;
import com.entuition.wekend.view.main.likelist.LikeListFragment;
import com.entuition.wekend.view.main.mailbox.MailBoxFragment;
import com.entuition.wekend.view.main.setting.SettingAlarmActivity;
import com.entuition.wekend.view.main.setting.SettingNoticeActivity;
import com.entuition.wekend.view.main.setting.SettingProfileActivity;
import com.entuition.wekend.view.main.store.StoreFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;

import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by ryukgoo on 2016. 6. 28..
 */
public class ContainerActivity extends AppCompatActivity implements ContainerNavigator {

    public static final String TAG = ContainerActivity.class.getSimpleName();

    private static final int MAX_FIXED_MENU_COUNT = 5;

    private Toolbar toolbar;
    private BottomBar bottomBar;
    private BottomBarBadge likeBadge;
    private BottomBarBadge mailBadge;

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private GuidePopupWindow guidePopupWindow;

    private ProfileHeaderLayoutBinding headerLayoutBinding;
    private ContainerViewModel model;
    private int selectedBottomBar = 0;

    private Map<String, AbstractFragment> fragmentMap = new HashMap<String, AbstractFragment>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new ContainerViewModel(this, this, UserInfoRepository.getInstance(this),
                ProductInfoRepository.getInstance(this));

        ContainerActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.container_activity);
        binding.setModel(model);

        toolbar = binding.toolbarContainer;
        drawerLayout = binding.drawerLayout;
        navigationView = binding.rightDrawer;

        setupToolbar();
        setupDrawerLayout();
        initFragments();
        setupNavigationView();
        setupBottomBar();

        model.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();

        ShortcutBadger.removeCount(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (guidePopupWindow != null && guidePopupWindow.isShowing()) {
            guidePopupWindow.dismiss();
        } else if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            if (getCurrentFragment().onBackPressed()) {
                AlertUtils.showAlertDialog(this, R.string.finish_app,
                        R.string.finish_app_message, true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    moveTaskToBack(true);
                                    finish();
                                }
                            }
                        });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleActivityResult > requestCode : " + requestCode + ", resultCode : " + resultCode);
        if (!GoogleBillingController.getInstance(this).handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "handleActivityResult handled by IABUtil");
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        model.onAttachedToWindow();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        bottomBar.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void onSelectedBottomBar(String tag) {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        }
        ActivityUtils.hideKeyboard(this);
        switchFragmentByTag(tag);
    }

    @Override
    public void onReSelectedBottomBar(String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        AbstractFragment fragment = (AbstractFragment) fragmentManager.findFragmentByTag(tag);
        fragment.reselect();
    }

    @Override
    public void onToggleDrawerLayout() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        } else {
            drawerLayout.openDrawer(Gravity.END);
        }
    }

    @Override
    public void onUserInfoLoaded(UserInfo userInfo) {
        displayCountOnBadge(likeBadge, userInfo.getNewLikeCount());
        displayCountOnBadge(mailBadge, userInfo.getNewReceiveCount() + userInfo.getNewSendCount());

        headerLayoutBinding.setUserInfo(userInfo);
    }

    @Override
    public void onClickTitle() {
        AbstractFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) currentFragment.onClickTitle();
    }

    @Override
    public void showGuidePopup(boolean isShowCheckBox) {
        guidePopupWindow = new GuidePopupWindow(ContainerActivity.this,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        guidePopupWindow.show(isShowCheckBox);
    }

    @Override
    public void gotoNoticeView() {
        Intent intent = new Intent(this, SettingNoticeActivity.class);
        startActivity(intent);
    }

    @Override
    public void gotoProfileView() {
        Intent intent = new Intent(this, SettingProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void sendMailToDeveloper(String username) {
        Uri uri = Uri.parse("mailto:entuitiondevelop@gmail.com");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_cc_mail_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_cc_mail_text, username));
        startActivity(intent);
    }

    @Override
    public void gotoAlarmSetting() {
        Intent intent = new Intent(this, SettingAlarmActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLogoutDialog() {
        AlertUtils.showAlertDialog(this, R.string.logout_app, R.string.logout_app_message,
                true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            model.logout();
                            dialog.dismiss();
                        }
                    }
                });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setupDrawerLayout() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View view) {}

            @Override
            public void onDrawerClosed(View view) {
                bottomBar.selectTabAtPosition(selectedBottomBar, false);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private void initFragments() {
        fragmentMap = new HashMap<String, AbstractFragment>();
        // use ViewModelProviders obtain activity view model
        CampaignListFragment campaignListFragment = CampaignListFragment.newInstance();
        campaignListFragment.setContainerViewModel(model);
        fragmentMap.put(CampaignListFragment.TAG, campaignListFragment);
        fragmentMap.put(LikeListFragment.TAG, LikeListFragment.newInstance());
        fragmentMap.put(MailBoxFragment.TAG, MailBoxFragment.newInstance());
        fragmentMap.put(StoreFragment.TAG, StoreFragment.newInstance());
    }

    private void setupBottomBar() {
        bottomBar = BottomBar.attach(this, null);
        bottomBar.setMaxFixedTabs(MAX_FIXED_MENU_COUNT);
        bottomBar.setItems(R.menu.menu_bottombar);

        int initPosition = getIntent().getIntExtra(Constants.START_ACTIVITY_POSITION, 0);
        bottomBar.setDefaultTabPosition(initPosition);
        bottomBar.setActiveTabColor(getResources().getColor(R.color.colorPrimaryDark));
        bottomBar.setOnMenuTabClickListener(model);

        int badgeColor = getResources().getColor(R.color.colorPrimaryDark);
        likeBadge = bottomBar.makeBadgeForTabAt(1, badgeColor, 0);
        mailBadge = bottomBar.makeBadgeForTabAt(2, badgeColor, 0);
    }

    private void setupNavigationView() {
        headerLayoutBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.profile_header_layout, null, false);
        navigationView.addHeaderView(headerLayoutBinding.getRoot());
        headerLayoutBinding.setModel(model);
        headerLayoutBinding.setUserInfo(new UserInfo());
    }

    private void switchFragmentByTag(String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for (String key : fragmentMap.keySet()) {
            Fragment fragment = fragmentMap.get(key);
            if (fragment.isAdded()) fragmentTransaction.hide(fragment);
        }

        Fragment targetFragment = fragmentManager.findFragmentByTag(tag);
        if (targetFragment != null && targetFragment.isAdded()) {
            fragmentTransaction.show(targetFragment);
        } else {
            fragmentTransaction.add(R.id.content_frame, fragmentMap.get(tag), tag);
        }

        fragmentTransaction.commit();
        selectedBottomBar = bottomBar.getCurrentTabPosition();
    }

    @Nullable
    private AbstractFragment getCurrentFragment() {
        for (Fragment fragment : fragmentMap.values()) {
            if (fragment != null && fragment.isVisible()) {
                if (fragment instanceof AbstractFragment) {
                    return (AbstractFragment) fragment;
                }
            }
        }
        return null;
    }

    private void displayCountOnBadge(BottomBarBadge badge, int count) {
        if (count > 0) {
            badge.setCount(count);
            badge.show();
        } else {
            badge.setCount(0);
            badge.hide();
        }
    }
}
