package com.entuition.wekend.view.main.container.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.DeveloperAuthenticationProvider;
import com.entuition.wekend.data.SharedPreferencesWrapper;
import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.google.billing.HasSubscriptionObserverable;
import com.entuition.wekend.data.google.fcm.NotificationCountingObservable;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.entuition.wekend.view.main.campaign.CampaignListFragment;
import com.entuition.wekend.view.main.likelist.LikeListFragment;
import com.entuition.wekend.view.main.mailbox.MailBoxFragment;
import com.entuition.wekend.view.main.store.StoreFragment;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2017. 10. 30..
 */

public class ContainerViewModel extends AbstractViewModel implements OnMenuTabClickListener {

    public static final String TAG = ContainerViewModel.class.getSimpleName();

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableBoolean isShowDropdown = new ObservableBoolean();
    public final ObservableField<String> point = new ObservableField<>();

    private final WeakReference<ContainerNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final ProductInfoDataSource productInfoDataSource;

    private String username;

    public ContainerViewModel(Context context, ContainerNavigator navigator,
                              UserInfoDataSource userInfoDataSource,
                              ProductInfoDataSource productInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<ContainerNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.productInfoDataSource = productInfoDataSource;
    }

    @Override
    public void onCreate() {
        title.set(getApplication().getString(R.string.title_campaign));
        isShowDropdown.set(true);

        NotificationCountingObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                userInfoDataSource.refreshUserInfo();
                refreshUserInfo();
            }
        });

        HasSubscriptionObserverable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                refreshUserInfo();
            }
        });
    }

    @Override
    public void onResume() {
        refreshUserInfo();
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {
        NotificationCountingObservable.getInstance().deleteObservers();
    }

    public void onClickTitle() {
        if (navigator.get() != null) {
            navigator.get().onClickTitle();
        }
    }

    public void onClickProfileImage() {
        if (navigator.get() != null) {
            navigator.get().gotoProfileView();
        }
    }

    public void onAttachedToWindow() {

        boolean showNoMore = SharedPreferencesWrapper.getShowNoMoreGuide(PreferenceManager.getDefaultSharedPreferences(getApplication()));

        Log.d(TAG, "onAttachedToWindow > showNoMore : " + showNoMore);

        if (!showNoMore) {
            if (navigator.get() != null) {
                navigator.get().showGuidePopup(true);
            }
        }
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (navigator.get() != null) {
            switch (item.getItemId()) {
                case R.id.menu_drawer_notice:
                    navigator.get().gotoNoticeView();
                    break;
                case R.id.menu_drawer_help:
                    navigator.get().showGuidePopup(false);
                    break;
                case R.id.menu_drawer_profile:
                    navigator.get().gotoProfileView();
                    break;
                case R.id.menu_drawer_cc:
                    navigator.get().sendMailToDeveloper(username);
                    break;
                case R.id.menu_drawer_setting:
                    navigator.get().gotoAlarmSetting();
                    break;
                case R.id.menu_drawer_logout:
                    navigator.get().showLogoutDialog();
                    break;
            }
        }
        return true;
    }

    @Override
    public void onMenuTabSelected(int menuItemId) {
        Log.d(TAG, "onMenuTagbSelected");
        String tag = getTagFromId(menuItemId);
        if (tag == null) {
            if (navigator.get() != null) {
                navigator.get().onToggleDrawerLayout();
            }
        } else {
            if (navigator.get() != null) {
                navigator.get().onSelectedBottomBar(tag);
            }
            title.set(getTitleFromTag(tag));

            if (tag.equals(CampaignListFragment.TAG)) {
                isShowDropdown.set(true);
            } else {
                isShowDropdown.set(false);
            }

            if (tag.equals(LikeListFragment.TAG) || tag.equals(MailBoxFragment.TAG)) {
                userInfoDataSource.clearBadgeCount(tag, new UserInfoDataSource.UpdateUserInfoCallback() {
                    @Override
                    public void onUpdateComplete(UserInfo userInfo) {
                        if (navigator.get() != null) {
                            navigator.get().onUserInfoLoaded(userInfo);
                        }
                    }

                    @Override
                    public void onUpdateFailed() {}
                });
            }
        }
    }

    @Override
    public void onMenuTabReSelected(int menuItemId) {
        Log.d(TAG, "onMenuTabReSelected");
        String tag = getTagFromId(menuItemId);
        if (tag == null) {
            if (navigator.get() != null) {
                navigator.get().onToggleDrawerLayout();
            }
        } else {
            if (navigator.get() != null) {
                navigator.get().onReSelectedBottomBar(tag);
            }
        }
    }

    public void logout() {
        DeveloperAuthenticationProvider.getDevAuthClientInstance().logout();
    }

    private void refreshUserInfo() {

        userInfoDataSource.getUserInfo(userInfoDataSource.getUserId(), new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                username = userInfo.getUsername();

                if (navigator.get() != null) {
                    navigator.get().onUserInfoLoaded(userInfo);
                }
            }

            @Override
            public void onDataNotAvailable() {}

            @Override
            public void onError() {}
        });

        GoogleBillingController.getInstance(getApplication()).checkSubcribing(new GoogleBillingController.OnValidateSubcribe() {
            @Override
            public void onValidateSubcribed(boolean isSubcribed, UserInfo userInfo) {
                if (isSubcribed) {
                    point.set(getApplication().getString(R.string.subscription_enabled));
                } else {
                    if (userInfo != null) {
                        point.set(getApplication().getString(R.string.formatted_point, userInfo.getBalloon()));
                    }
                }
            }
        });

    }

    @Nullable
    private String getTagFromId(int resourceId) {
        switch (resourceId) {
            case R.id.bb_menu_campaign :
                return CampaignListFragment.TAG;
            case R.id.bb_menu_likelist:
                return LikeListFragment.TAG;
            case R.id.bb_menu_mailbox:
                return MailBoxFragment.TAG;
            case R.id.bb_menu_store:
                return StoreFragment.TAG;
        }
        return null;
    }

    @NonNull
    private String getTitleFromTag(String tag) {
        if (tag.equals(CampaignListFragment.TAG)) {
            return getTitleFromFilterOptions();
        } else if (tag.equals(LikeListFragment.TAG)) {
            return getApplication().getString(R.string.title_likelist);
        } else if (tag.equals(MailBoxFragment.TAG)) {
            return getApplication().getString(R.string.title_mailbox);
        } else if (tag.equals(StoreFragment.TAG)) {
            return getApplication().getString(R.string.title_store);
        }

        return getApplication().getString(R.string.title_campaign);
    }

    @NonNull
    private String getTitleFromFilterOptions() {
        return productInfoDataSource.getFilterOptions().getTitle(getApplication());
    }
}
