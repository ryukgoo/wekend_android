package com.entuition.wekend.view.main.store.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.google.billing.HasSubscriptionObserverable;
import com.entuition.wekend.data.google.billing.Purchase;
import com.entuition.wekend.data.google.billing.SkuDetails;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.entuition.wekend.view.main.store.adapter.StoreAdapter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2017. 11. 17..
 */

public class StoreViewModel extends AbstractViewModel implements GoogleBillingController.OnQueryInventory {

    public static final String TAG = StoreViewModel.class.getSimpleName();

    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableBoolean isRefreshing = new ObservableBoolean();
    public final ObservableField<UserInfo> userInfo = new ObservableField<>();
    public final ObservableField<String> point = new ObservableField<>();
    public final ObservableArrayList<SkuDetails> items = new ObservableArrayList<>();

    private final WeakReference<StoreNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final GoogleBillingController billingController;

    public StoreViewModel(Context context, StoreNavigator navigator,
                          UserInfoDataSource userInfoDataSource, GoogleBillingController controller) {
        super(context);

        this.navigator = new WeakReference<StoreNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.billingController = controller;

        this.isLoading.set(false);
    }

    @Override
    public void onCreate() {

        loadUserInfo();

        billingController.setListener(this);
        items.addAll(billingController.getSkuDetails());

        HasSubscriptionObserverable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                loadUserInfo();
            }
        });
    }

    private void loadUserInfo() {

        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo info) {
                userInfo.set(info);
            }

            @Override
            public void onDataNotAvailable() {}

            @Override
            public void onError() {}
        });

        billingController.checkSubcribing(new GoogleBillingController.OnValidateSubcribe() {
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

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onRefresh() {
        isRefreshing.set(true);
        billingController.queryInventory();
    }

    @Override
    public void onQueryFinished(List<SkuDetails> skuDetails) {
        items.clear();
        items.addAll(skuDetails);
        isLoading.set(false);
        isRefreshing.set(false);
    }

    @Override
    public void onPurchaseFinished(Purchase purchase) {

        Log.d(TAG, "onPurchaseFinished > purchase : " + purchase.getSku());

        isLoading.set(true);
        // TODO : ....

        String id = purchase.getSku();
        String token = purchase.getToken();

        userInfoDataSource.validatePurchase(userInfo.get().getUserId(), id, token, new UserInfoDataSource.ValidatePurchaseCallback() {
            @Override
            public void onValidateComplete() {
                isLoading.set(false);
                isRefreshing.set(false);
                Log.d(TAG, "onPurchaseFinished > onValidateComplete");
                if (navigator.get() != null) navigator.get().onPurchaseFinished();
                HasSubscriptionObserverable.getInstance().hasSubscribe();
            }

            @Override
            public void onValidateFailed() {
                Log.d(TAG, "onPurchaseFinished > onValidateFailed");
                isLoading.set(false);
                isRefreshing.set(false);
                if (navigator.get() != null) navigator.get().onPurchaseFailed();
            }
        });

    }

    @Override
    public void onConsumePurchase(String sku) {
        isLoading.set(false);
    }

    @Override
    public void onQueryFailed() {
        Log.d(TAG, "onQueryFailed");
        isLoading.set(false);
        isRefreshing.set(false);
        if (navigator.get() != null) navigator.get().onQueryInventoryFailed();
    }

    @Override
    public void onPurchaseFailed() {
        Log.d(TAG, "onPurchaseFailed");
        isLoading.set(false);
        if (navigator.get() != null) navigator.get().onPurchaseFailed();
    }

    @Override
    public void onConsumeFailed() {
        Log.d(TAG, "onConsumeFailed");
        isLoading.set(false);
        if (navigator.get() != null) navigator.get().onPurchaseFailed();
    }

    public void onClickItem(int position) {
        if (navigator.get() != null) navigator.get().purchaseItem(position);
    }

    public String getBonusForSku(String sku) {
        return billingController.getPurchaseTitles().get(sku);
    }

    public void launchPurchase(final Activity activity, final int position) {
        Log.d(TAG, "launcherPurchase > position : " + position);

        billingController.checkSubcribing(new GoogleBillingController.OnValidateSubcribe() {
            @Override
            public void onValidateSubcribed(boolean isSubcribed, UserInfo info) {
                if (isSubcribed) {
                    if (navigator.get() != null) navigator.get().onAlreayHasSubscription();
                } else {
                    isLoading.set(true);
                    billingController.launchPurchase(activity, position, info.getUserId());
                }
            }
        });

        /*
        if (billingController.isAvailablePurchase(position)) {
            isLoading.set(true);
            billingController.launchPurchase(activity, position, userInfo.get().getUserId());
        } else {
            if (navigator.get() != null) navigator.get().onAlreayHasSubscription();
        }
        */
    }

    @BindingAdapter("refreshStore")
    public static void refreshPurchases(RecyclerView view, List<SkuDetails> skuDetail) {
        StoreAdapter adapter = (StoreAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.replaceData(skuDetail);
        }
    }

}