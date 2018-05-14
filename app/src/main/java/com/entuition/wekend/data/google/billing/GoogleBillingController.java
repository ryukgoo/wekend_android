package com.entuition.wekend.data.google.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoDataSource;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2016. 10. 4..
 */

public class GoogleBillingController implements IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener,
        IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnConsumeFinishedListener, IabHelper.OnConsumeMultiFinishedListener,
        IabBroadcastReceiver.IabBroadcastListener {

    public static final String TAG = GoogleBillingController.class.getSimpleName();

    private static final String SKU_SUBSCRIPTION = "com.entuition.wekend.billing.subscription.1monthly";

    private static final List<String> SUBS = Arrays.asList(SKU_SUBSCRIPTION);

    private static final int REQUEST_CODE_PURCHASE = 10001;

    private static GoogleBillingController instance = null;

    public static GoogleBillingController getInstance(Context context) {
        if (instance == null) {
            synchronized (GoogleBillingController.class) {
                if (instance == null) {
                    instance = new GoogleBillingController(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private Context context;

    private IabHelper helper;
    private IabBroadcastReceiver broadcastReceiver;

    private List<SkuDetails> skuDetails;
    private Map<String, String> purchaseTitles;
    private OnQueryInventory listener;

    private boolean isAutoRenewEnabled = false;
    private boolean isSubscribed = false;

    public GoogleBillingController(Context context) {
        this.context = context;
    }

    public void init() {

        List<String> titles = Arrays.asList(context.getResources().getStringArray(R.array.inapp_purchase_title));
        purchaseTitles = new HashMap<>();
//        purchaseTitles.put(SKU_POINT_1000, titles.get(0));
//        purchaseTitles.put(SKU_POINT_3000, titles.get(1));
//        purchaseTitles.put(SKU_POINT_5000, titles.get(2));
//        purchaseTitles.put(SKU_POINT_10000, titles.get(3));
//        purchaseTitles.put(SKU_POINT_30000, titles.get(4));
        purchaseTitles.put(SKU_SUBSCRIPTION, titles.get(5));

        this.helper = new IabHelper(context, context.getString(R.string.google_public_key));
        this.helper.enableDebugLogging(true, TAG);
        this.helper.startSetup(this);
    }

    public void setListener(OnQueryInventory listener) {
        this.listener = listener;
    }

    public Map<String, String> getPurchaseTitles() {
        return purchaseTitles;
    }

    public List<SkuDetails> getSkuDetails() {

        if (skuDetails == null) {
            if (helper != null) helper.startSetup(this);
            return new ArrayList<>(0);
        }

        return skuDetails;
    }

    public void queryInventory() {
        if (helper == null) {
            if (listener != null) listener.onQueryFailed();
            return;
        }

        try {
            helper.queryInventoryAsync(true, null, SUBS, this);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "queryInventoryAsync > e : " + e.getMessage());
            listener.onQueryFailed();
        }
    }

    public void launchPurchase(Activity activity, int product, String userId) {
        try {
            Log.d(TAG, "launchPurchase > product : " + product);

            SkuDetails details = skuDetails.get(product);

            if (details.getType().equals(IabHelper.ITEM_TYPE_INAPP)) {
                helper.launchPurchaseFlow(activity, details.getSku(), REQUEST_CODE_PURCHASE, this);
            } else if (details.getType().equals(IabHelper.ITEM_TYPE_SUBS)) {
                helper.launchSubscriptionPurchaseFlow(activity, details.getSku(), REQUEST_CODE_PURCHASE, this, userId);
            }

        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "launchPurchase > error : " + e.getMessage());
            listener.onPurchaseFailed();
        }
    }

    public boolean isAvailablePurchase(int position) {
        Log.d(TAG, "isAvailablePurchase > position : " + position);
        SkuDetails details = skuDetails.get(position);
        return !(details.getSku().equals(SKU_SUBSCRIPTION) && isSubscribed && isAutoRenewEnabled);
    }

    public void checkSubcribing(final OnValidateSubcribe callback) {
        UserInfoRepository.getInstance(context).getUserInfo(null, new UserInfoDataSource.GetUserInfoCallback() {
            @Override
            public void onUserInfoLoaded(UserInfo userInfo) {
                String expiresTimestamp = userInfo.getExpiresTime();

                if (expiresTimestamp != null) {
                    long expiresTimeMillis = Utilities.getDateFromTimeStamp(expiresTimestamp).getTime();
                    long nowMillis = new Date().getTime();

                    if (expiresTimeMillis >= nowMillis) {
                        callback.onValidateSubcribed(true, userInfo);
                        return;
                    }

                    callback.onValidateSubcribed(false, userInfo);
                }
            }

            @Override
            public void onDataNotAvailable() { callback.onValidateSubcribed(false, null); }

            @Override
            public void onError() { callback.onValidateSubcribed(false, null); }
        });
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (helper == null) return false;
        Log.d(TAG, "handleActivityResult > requestCode : " + requestCode + ", resultCode : " + resultCode);
        return helper.handleActivityResult(requestCode, resultCode, data);
    }

    /**
     * implements IabHelper.OnIabSetupFinishedListener
     * @param result The result of the setup process.
     */
    @Override
    public void onIabSetupFinished(IabResult result) {
        Log.d(TAG, "onIabSetupFinished > service connected");
        if (result.isSuccess()) {

            if (helper == null) return;

            broadcastReceiver = new IabBroadcastReceiver(this);
            IntentFilter intentFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
            context.registerReceiver(broadcastReceiver, intentFilter);

            try {
//                helper.queryInventoryAsync(true, SKUS, SUBS, this);
                helper.queryInventoryAsync(true, null, SUBS, this);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e(TAG, "queryInventoryAsync > e : " + e.getMessage());
                listener.onQueryFailed();
            }
        } else {
            Log.e(TAG, "onIabSetupFinished error!!!!");
        }
    }

    /**
     * IabHelper.onQueryInventoryFinished
     * @param result The result of the operation.
     * @param inventory
     */
    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        Log.d(TAG, "Query Inventory finished");

        if (helper == null || result.isFailure()) {
            Log.e(TAG, "onQueryInventoryFinished error!!!!");
            listener.onQueryFailed();
        } else {
            Purchase subscription = inventory.getPurchase(SKU_SUBSCRIPTION);

            if (subscription != null) {
                isAutoRenewEnabled = subscription.isAutoRenewing();

                Log.d(TAG, "isAutoRenewEnabled : " + isAutoRenewEnabled);
                Log.d(TAG, "purchaseToken : " + subscription.getToken());

                if (verifyDeveloperPayload(subscription)) {
                    isSubscribed = true;
                }
            } else {
                isAutoRenewEnabled = false;
                HasSubscriptionObserverable.getInstance().hasChanged();
            }

            List<Purchase> purchases = inventory.getAllPurchases();
            if (purchases != null && purchases.size() > 0) {
                try {
                    helper.consumeAsync(inventory.getAllPurchases(), this);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e(TAG, "consumeAsync > error : " + e.getMessage());
                    listener.onConsumeFailed();
                }
            }

            skuDetails = new ArrayList<>();

            if (inventory.hasDetails(SKU_SUBSCRIPTION)) {
                skuDetails.add(inventory.getSkuDetails(SKU_SUBSCRIPTION));
            }

            if (listener != null) listener.onQueryFinished(skuDetails);
        }
    }

    /**
     * IabHelper.onIabPurchaseFinished
     * @param result The result of the purchase.
     * @param purchase
     */
    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

        Log.d(TAG, "onIabPurchaseFinished > result is Success : " + result.isSuccess());

        if (helper == null || result.isFailure()) {
            listener.onPurchaseFailed();
        } else {

            Log.d(TAG, "itemType : " + purchase.getItemType());
            Log.d(TAG, "purchase State : " + purchase.getPurchaseState());
            Log.d(TAG, "Order Id : " + purchase.getOrderId());

            if (purchase.getItemType().equals(IabHelper.ITEM_TYPE_INAPP)) {
                try {
                    helper.consumeAsync(purchase, this);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.e(TAG, "consumeAsync > error : " + e.getMessage());
                    listener.onConsumeFailed();
                }
            } else {
                isSubscribed = true;
                isAutoRenewEnabled = purchase.isAutoRenewing();
                if (listener != null) listener.onPurchaseFinished(purchase);
            }
        }
    }

    /**
     * IabHelper.onConsumeFinished
     * @param purchase The purchase that was (or was to be) consumed.
     * @param result The result of the consumption operation.
     */
    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {
        Log.d(TAG, "onConsumeFinished > result : " + result.isSuccess());
        if (helper == null || result.isFailure()) {
            listener.onConsumeFailed();
        } else {
            listener.onConsumePurchase(purchase.getSku());
        }
    }

    /**
     * IabHelper.onConsumeMultiFinished
     * @param purchases The items that were (or were to be) consumed.
     * @param results The results of each consumption operation, corresponding to each
     */
    @Override
    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
        if (helper == null) {
            listener.onConsumeFailed();
        }
    }

    /**
     * IabBroadcastReceiver.IabBroadcastListener
     */
    @Override
    public void receivedBroadcast() {
        try {
            helper.queryInventoryAsync(this);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "receiveBroadcast Error");
        }
    }

    public void dismiss() {

        Log.d(TAG, "dismiss");

        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
        }

        if (helper != null) {
            helper.disposeWhenFinished();
            helper = null;
        }
    }

    private boolean verifyDeveloperPayload(Purchase purchase) {

        String payload = purchase.getDeveloperPayload();
        String userId = UserInfoRepository.getInstance(context).getUserId();

        Log.d(TAG, "verifyDeveloperPayload > payload : " + payload);

        String sku = purchase.getSku();
        String token = purchase.getToken();

        if (sku != null && token != null) {
            UserInfoRepository.getInstance(context).validatePurchase(userId, sku, token, new UserInfoDataSource.ValidatePurchaseCallback() {
                @Override
                public void onValidateComplete() {
                    HasSubscriptionObserverable.getInstance().hasSubscribe();
                }

                @Override
                public void onValidateFailed() {
                    Log.e(TAG, "onValidateFailed");
                }
            });
        }

        return payload.equals(userId);
    }

    public interface OnQueryInventory {
        void onQueryFinished(List<SkuDetails> skuDetails);
        void onPurchaseFinished(Purchase purchase);
        void onConsumePurchase(String sku);
        void onQueryFailed();
        void onPurchaseFailed();
        void onConsumeFailed();
    }

    public interface OnValidateSubcribe {
        void onValidateSubcribed(boolean isSubcribed, UserInfo userInfo);
    }
}
