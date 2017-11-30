package com.entuition.wekend.view.main.store.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.entuition.wekend.data.google.billing.GoogleBillingController;
import com.entuition.wekend.data.google.billing.Inventory;
import com.entuition.wekend.data.google.billing.SkuDetails;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;
import com.entuition.wekend.view.main.store.adapter.StoreAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 17..
 */

public class StoreViewModel extends AbstractViewModel implements GoogleBillingController.OnQueryInventory {

    public static final String TAG = StoreViewModel.class.getSimpleName();

    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableField<UserInfo> userInfo = new ObservableField<>();
    public final ObservableArrayList<SkuDetails> items = new ObservableArrayList<>();

    private final WeakReference<StoreNavigator> navigator;
    private final UserInfoDataSource userInfoDataSource;
    private final GoogleBillingController billingController;

    private Map<String, String> bonusMap = new HashMap<>();

    public StoreViewModel(Context context, StoreNavigator navigator,
                          UserInfoDataSource userInfoDataSource,
                          GoogleBillingController billingController) {
        super(context);

        this.navigator = new WeakReference<StoreNavigator>(navigator);
        this.userInfoDataSource = userInfoDataSource;
        this.billingController = billingController;

        isLoading.set(false);
    }

    @Override
    public void onCreate() {

        isLoading.set(true);

        billingController.init();
        billingController.setListener(this);

        userInfoDataSource.getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo info) {
                userInfo.set(info);
            }

            @Override
            public void onDataNotAvailable() {}
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {
        billingController.dismiss();
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "handleActivityResult > requestCode : " + requestCode + ", resultCode : " + resultCode);
        return billingController.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onQueryFinished(Inventory inventory) {

        bonusMap = billingController.getBonusMap();

        List<SkuDetails> skuDetails = new ArrayList<>();
        for (String sku : billingController.getSkuList()) {
            if (inventory.hasDetails(sku)) {
                skuDetails.add(inventory.getSkuDetails(sku));
            }
        }

        items.clear();
        items.addAll(skuDetails);
        isLoading.set(false);
    }

    @Override
    public void onPurchaseFinished(String sku) {
        isLoading.set(false);
    }

    @Override
    public void onConsumePurchase(String sku) {
        isLoading.set(false);

        int point = billingController.getPrice(sku);

        userInfoDataSource.purchasePoint(point, new UserInfoDataSource.UpdateUserInfoCallback() {
            @Override
            public void onUpdateComplete(UserInfo info) {
                userInfo.set(info);
            }

            @Override
            public void onUpdateFailed() {

            }
        });
    }

    @Override
    public void onQueryFailed() {
        Log.d(TAG, "onQueryFailed");
        isLoading.set(false);
        navigator.get().onQueryInventoryFailed();
    }

    @Override
    public void onPurchaseFailed() {
        Log.d(TAG, "onPurchaseFailed");
        isLoading.set(false);
        navigator.get().onPurchaseFailed();
    }

    @Override
    public void onConsumeFailed() {
        Log.d(TAG, "onConsumeFailed");
        isLoading.set(false);
        navigator.get().onPurchaseFailed();
    }

    public void onClickItem(int position) {
        navigator.get().purchaseItem(position);
    }

    public String getBonusForSku(String sku) {
        return bonusMap.get(sku);
    }

    public void launchPurchase(Activity activity, int position) {

        Log.d(TAG, "launcherPurchase > position : " + position);

        isLoading.set(true);
        billingController.launchPurchase(activity, position);
    }

    @BindingAdapter("refreshStore")
    public static void refreshPurchases(RecyclerView view, List<SkuDetails> skuDetail) {
        StoreAdapter adapter = (StoreAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.replaceData(skuDetail);
        }
    }

}
