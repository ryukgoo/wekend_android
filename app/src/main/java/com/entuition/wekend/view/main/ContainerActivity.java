package com.entuition.wekend.view.main;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.authentication.DeveloperAuthenticationProvider;
import com.entuition.wekend.model.data.SharedPreferencesWrapper;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.data.user.asynctask.UpdateUserInfoTask;
import com.entuition.wekend.model.googleservice.billing.GoogleBillingController;
import com.entuition.wekend.model.googleservice.gcm.BadgeNotificationObservable;
import com.entuition.wekend.model.googleservice.gcm.MessageReceivingService;
import com.entuition.wekend.view.common.WekendAbstractActivity;
import com.entuition.wekend.view.main.activities.setting.SettingAlarmActivity;
import com.entuition.wekend.view.main.activities.setting.SettingNoticeActivity;
import com.entuition.wekend.view.main.activities.setting.SettingProfileActivity;
import com.entuition.wekend.view.main.fragment.AbstractFragment;
import com.entuition.wekend.view.main.fragment.StoreFragment;
import com.entuition.wekend.view.main.fragment.campaign.CampaignListFragment;
import com.entuition.wekend.view.main.fragment.likelist.LikeListFragment;
import com.entuition.wekend.view.main.fragment.mailbox.MailBoxFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2016. 6. 28..
 */
public class ContainerActivity extends WekendAbstractActivity implements OnMenuTabClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = getClass().getSimpleName();

    public static final int POSITION_NOT_SELECTED = -1;
    public static final int POSITION_CAMPAIGN = 0;
    public static final int POSITION_LIKE = 1;
    public static final int POSITION_MAIL = 2;
    public static final int POSITION_STORE = 3;

    private final int MAX_FIXED_MENU_COUNT = 5;

    private TextView titleTextView;
    private ImageView dropdown;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView toolbarDropdownButton;
    private BottomBar bottomBar;
    private BottomBarBadge likeBadge;
    private BottomBarBadge mailBadge;
    private int selectedBottomBar;
    private int defaultStartPosition;

    private NavigationView navigationView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private GuidePopupWindow guidePopupWindow;

    private CampaignListFragment campaignListFragment;
    private LikeListFragment likeListFragment;
    private MailBoxFragment mailBoxFragment;
    private StoreFragment storeFragment;
    private AbstractFragment fragment;

    // Observsers
    private UpdateBadgeObserver updateBadgeObserver;
    private ChangeTitleObserver changeTitleObserver;
    private DropdownVisibleObserver dropdownVisibleObserver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (reinitialize(savedInstanceState)) return;

        setContentView(R.layout.activity_container);

        defaultStartPosition = getIntent().getIntExtra(Constants.START_ACTIVITY_POSITION, 0);
        Log.d(TAG, "onCreate > defaultStartPosition : " + defaultStartPosition);

        initView(null);
        startService(new Intent(this, MessageReceivingService.class));
        addObservers();
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "onDestroy");

        removeObservers();
        super.onDestroy();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!SharedPreferencesWrapper.getShowNoMoreGuide(PreferenceManager.getDefaultSharedPreferences(this))) {
            guidePopupWindow.show(true);
        }
    }

    private void addObservers() {
        updateBadgeObserver = new UpdateBadgeObserver();
        BadgeNotificationObservable.getInstance().addObserver(updateBadgeObserver);
        changeTitleObserver = new ChangeTitleObserver();
        ChangeTitleObservable.getInstance().addObserver(new ChangeTitleObserver());
        dropdownVisibleObserver = new DropdownVisibleObserver();
        DropdownVisibleObservable.getInstance().addObserver(new DropdownVisibleObserver());
    }

    private void removeObservers() {
        BadgeNotificationObservable.getInstance().deleteObserver(updateBadgeObserver);
        ChangeTitleObservable.getInstance().deleteObserver(changeTitleObserver);
        DropdownVisibleObservable.getInstance().deleteObserver(dropdownVisibleObserver);
    }

    private void initView(@Nullable Bundle savedInstanceState) {

        titleTextView = (TextView) findViewById(R.id.id_toolbar_title);
        dropdown = (ImageView) findViewById(R.id.id_toolbar_btn_dropdown);

        toolbar = (Toolbar) findViewById(R.id.id_toolbar_container);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        toolbarTitle = (TextView) findViewById(R.id.id_toolbar_title);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    fragment.onClickTitle();
                }
            }
        });

        toolbarDropdownButton = (ImageView) findViewById(R.id.id_toolbar_btn_dropdown);
        toolbarDropdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    fragment.onClickTitle();
                }
            }
        });

        selectedBottomBar = POSITION_NOT_SELECTED;

        navigationView = (NavigationView) findViewById(R.id.right_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        ((ProfileHeaderView) navigationView.getHeaderView(0)).init();
        ((ProfileHeaderView) navigationView.getHeaderView(0)).setViews();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View view) {
                ((ProfileHeaderView) navigationView.getHeaderView(0)).setViews();
            }

            @Override
            public void onDrawerClosed(View view) {
                bottomBar.selectTabAtPosition(selectedBottomBar, false);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        campaignListFragment = new CampaignListFragment();
        likeListFragment = new LikeListFragment();
        mailBoxFragment = new MailBoxFragment();
        storeFragment = new StoreFragment();

        bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setMaxFixedTabs(MAX_FIXED_MENU_COUNT);
        bottomBar.setItems(R.menu.menu_bottombar);
        bottomBar.setDefaultTabPosition(defaultStartPosition);
        bottomBar.setActiveTabColor(getResources().getColor(R.color.colorPrimaryDark));
        bottomBar.setOnMenuTabClickListener(this);

        int badgeColor = getResources().getColor(R.color.colorPrimaryDark);
        likeBadge = bottomBar.makeBadgeForTabAt(POSITION_LIKE, badgeColor, 0);
        mailBadge = bottomBar.makeBadgeForTabAt(POSITION_MAIL, badgeColor, 0);

        guidePopupWindow = new GuidePopupWindow(ContainerActivity.this, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        UserInfo userInfo = UserInfoDaoImpl.getInstance(this).getUserInfo();
        updateBadgeCount(likeBadge, userInfo.getNewLikeCount());
        int mailCount = userInfo.getNewReceiveCount() + userInfo.getNewSendCount();
        updateBadgeCount(mailBadge, mailCount);
    }

    private void displayCampaignListFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (campaignListFragment.isAdded()) {
            ft.show(campaignListFragment);
        } else {
            ft.add(R.id.content_frame, campaignListFragment);
        }

        if (likeListFragment.isAdded()) ft.hide(likeListFragment);
        if (mailBoxFragment.isAdded()) ft.hide(mailBoxFragment);
        if (storeFragment.isAdded()) ft.hide(storeFragment);
        ft.commit();

        fragment = campaignListFragment;
    }

    private void displayLikeListFragment() {

        Log.d(TAG, "displayLikeListFragment");

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (likeListFragment.isAdded()) {
            ft.show(likeListFragment);
        } else {
            ft.add(R.id.content_frame, likeListFragment);
        }

        if (campaignListFragment.isAdded()) ft.hide(campaignListFragment);
        if (mailBoxFragment.isAdded()) ft.hide(mailBoxFragment);
        if (storeFragment.isAdded()) ft.hide(storeFragment);
        ft.commit();

        fragment = likeListFragment;

        MessageReceivingService.clearNotificationLikeNum(this);
        clearBadgeCount(Constants.TYPE_NOTIFICATION_LIKE);

        Log.d(TAG, "displayLikeListFragment end");
    }

    private void displayMailBoxFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (mailBoxFragment.isAdded()) {
            ft.show(mailBoxFragment);
        } else {
            ft.add(R.id.content_frame, mailBoxFragment);
        }

        if (campaignListFragment.isAdded()) ft.hide(campaignListFragment);
        if (likeListFragment.isAdded()) ft.hide(likeListFragment);
        if (storeFragment.isAdded()) ft.hide(storeFragment);
        ft.commit();

        fragment = mailBoxFragment;

        MessageReceivingService.clearNotificationMailNum(this);
        clearBadgeCount(Constants.TYPE_NOTIFICATION_RECEIVE_MAIL);
    }

    private void displayStoreFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (storeFragment.isAdded()) {
            ft.show(storeFragment);
        } else {
            ft.add(R.id.content_frame, storeFragment);
        }

        if (campaignListFragment.isAdded()) ft.hide(campaignListFragment);
        if (likeListFragment.isAdded()) ft.hide(likeListFragment);
        if (mailBoxFragment.isAdded()) ft.hide(mailBoxFragment);
        ft.commit();

        fragment = storeFragment;
    }

    private void changeTitle(String title) {
        this.titleTextView.setText(title);
    }

    private void updateBadgeCount(BottomBarBadge badge, int count) {
        if (count > 0) {
            badge.setCount(count);
            badge.show();
        } else {
            badge.setCount(0);
            badge.hide();
        }
    }

    private void updateNewMessageCount(String type) {

        Log.d(TAG, "updateNewMessageCount > type : " + type);

        switch (type) {

            case Constants.TYPE_NOTIFICATION_LIKE :
                int likeCount = MessageReceivingService.getNewLikeCount(this);
                updateBadgeCount(likeBadge, likeCount);
                break;

            case Constants.TYPE_NOTIFICATION_RECEIVE_MAIL :
                int receiveMailCount = MessageReceivingService.getNewMailCount(this);
                updateBadgeCount(mailBadge, receiveMailCount);
                break;

            case Constants.TYPE_NOTIFICATION_SEND_MAIL :
                int sendMailCount = MessageReceivingService.getNewMailCount(this);
                updateBadgeCount(mailBadge, sendMailCount);
                break;
        }

    }

    private void clearBadgeCount(String type) {
        UserInfo userInfo = UserInfoDaoImpl.getInstance(this).getUserInfo();

        switch (type) {
            case Constants.TYPE_NOTIFICATION_LIKE :
                userInfo.setNewLikeCount(0);
                break;
            case Constants.TYPE_NOTIFICATION_RECEIVE_MAIL :
                userInfo.setNewReceiveCount(0);
                userInfo.setNewSendCount(0);
                break;
            case Constants.TYPE_NOTIFICATION_SEND_MAIL :
                userInfo.setNewReceiveCount(0);
                userInfo.setNewSendCount(0);
                break;
        }

        UpdateUserInfoTask task = new UpdateUserInfoTask(this);
        task.execute(userInfo);
    }

    private void clearTaskAndFinish() {
        moveTaskToBack(true);
        finish();
    }

    public void refreshNavigationView() {
        ((ProfileHeaderView) navigationView.getHeaderView(0)).setViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult > requestCode : " + requestCode + ", resultCode : " + resultCode);

        if (!GoogleBillingController.getInstance(this).handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Container > onResume!!@@!@!");

//        ((ProfileHeaderView) navigationView.getHeaderView(0)).setViews();

        Log.d(TAG, "onResume > selectedBottomBar : " + selectedBottomBar);
        switch (selectedBottomBar) {
            case POSITION_CAMPAIGN :
                if (campaignListFragment != null) campaignListFragment.refresh();
                break;
            case POSITION_LIKE :
                if (likeListFragment != null) likeListFragment.refresh();
                break;
            case POSITION_MAIL :
                if (mailBoxFragment != null) mailBoxFragment.refresh();
                break;
            case POSITION_STORE :
                if (storeFragment != null) storeFragment.refresh();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState");

        bottomBar.onSaveInstanceState(outState);

        outState.clear();
    }

    @Override
    public void onBackPressed() {

        if (guidePopupWindow.isShowing()) {
            guidePopupWindow.dismiss();
        } else if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
        } else {
            if (fragment.onBackPressed()) {

                new AlertDialog.Builder(new ContextThemeWrapper(ContainerActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.finish_application_alertdialog_title))
                        .setMessage(getString(R.string.finish_application_alertdialog_message))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                clearTaskAndFinish();
                            }
                        })
                        .setNegativeButton(getString(R.string.finish_application_alertdialog_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onMenuTabSelected(@IdRes int menuItemId) {

        Log.d(TAG, "onMenuTabSelected > selectedBottomBar : " + selectedBottomBar);

        switch (menuItemId) {
            case R.id.bb_menu_campaign:
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }

                if (selectedBottomBar == POSITION_CAMPAIGN) return;
                selectedBottomBar = POSITION_CAMPAIGN;
                displayCampaignListFragment();
                break;
            case R.id.bb_menu_likelist :
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }

                if (selectedBottomBar == POSITION_LIKE) return;
                selectedBottomBar = POSITION_LIKE;
                fragment = likeListFragment;
                displayLikeListFragment();
                break;
            case R.id.bb_menu_mailbox :
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }

                if (selectedBottomBar == POSITION_MAIL) return;
                selectedBottomBar = POSITION_MAIL;
                fragment = mailBoxFragment;
                displayMailBoxFragment();
                break;
            case R.id.bb_menu_store :
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }

                if (selectedBottomBar == POSITION_STORE) return;
                selectedBottomBar = POSITION_STORE;
                fragment = storeFragment;
                displayStoreFragment();
                break;
            case R.id.bb_menu_setting :

                if (selectedBottomBar == POSITION_CAMPAIGN) campaignListFragment.closeInputMethod();

                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else  {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            default:
                Toast.makeText(this, "Bottom bar select!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onMenuTabReSelected(@IdRes int menuItemId) {
        switch (menuItemId) {
            case R.id.bb_menu_setting :
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else  {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
            default:

                fragment.reselect();

                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {

            case R.id.menu_drawer_notice :
                intent = new Intent(ContainerActivity.this, SettingNoticeActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_drawer_help :
//                intent = new Intent(ContainerActivity.this, SettingHelpActivity.class);
//                startActivity(intent);

                guidePopupWindow.show(false);

                break;

            case R.id.menu_drawer_profile :
                intent = new Intent(ContainerActivity.this, SettingProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_drawer_cc :
                Uri uri = Uri.parse("mailto:entuitiondevelop@gmail.com");
                intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "고객센터 문의메일");
                intent.putExtra(Intent.EXTRA_TEXT, "계정이메일:\n문의내용:");
                startActivity(intent);
                break;

            case R.id.menu_drawer_setting :
                intent = new Intent(ContainerActivity.this, SettingAlarmActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_drawer_logout :
                new AlertDialog.Builder(new ContextThemeWrapper(ContainerActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.logout_alertdialog_title))
                        .setMessage(getString(R.string.logout_alertdialog_message))
                        .setPositiveButton(getString(R.string.logout_alertdialog_positive), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                DeveloperAuthenticationProvider.getDevAuthClientInstance().logout();
                            }
                        })
                        .setNegativeButton(getString(R.string.logout_alertdialog_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
        }
        return true;
    }

    private class UpdateBadgeObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {

            Log.d(TAG, "UpdateBadgeObserver > update > data : " + data.toString());

            String type = String.valueOf(data);
            updateNewMessageCount(type);
        }
    }

    private class ChangeTitleObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            String title = String.valueOf(data);
            changeTitle(title);
        }
    }

    private class DropdownVisibleObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            boolean visible = (boolean) data;

            if (visible) {
                dropdown.setVisibility(View.VISIBLE);
            } else {
                dropdown.setVisibility(View.GONE);
            }
        }
    }
}
