package com.entuition.wekend.data.google.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2016. 10. 4..
 */

public class GoogleBillingController implements IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener,
        IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnConsumeFinishedListener, IabHelper.OnConsumeMultiFinishedListener {

    public static final String TAG = GoogleBillingController.class.getSimpleName();

    private static final String SKU_POINT_1000 = "com.entuition.wekend.billing.point.1000";
    private static final String SKU_POINT_3000 = "com.entuition.wekend.billing.point.3000";
    private static final String SKU_POINT_5000 = "com.entuition.wekend.billing.point.5000";
    private static final String SKU_POINT_10000 = "com.entuition.wekend.billing.point.10000";
    private static final String SKU_POINT_30000 = "com.entuition.wekend.billing.point.30000";
    private static final String SKU_POINT_50000 = "com.entuition.wekend.billing.point.50000";

    private static final int POINT_1000 = 1000;
    private static final int POINT_3000 = 3000 + 500;
    private static final int POINT_5000 = 5000 + 1000;
    private static final int POINT_10000 = 10000 + 2500;
    private static final int POINT_30000 = 30000 + 8500;

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
    private List<String> skuList;
    private Map<String, String> bonusMap;
    private Map<String, Integer> priceMap;
    private OnQueryInventory listener;

    public GoogleBillingController(Context context) {
        this.context = context;
    }

    public void init() {
        this.helper = new IabHelper(context, context.getString(R.string.google_public_key));
        this.helper.enableDebugLogging(true, TAG);
        this.helper.startSetup(this);

        skuList = new ArrayList<>();
        skuList.add(SKU_POINT_1000);
        skuList.add(SKU_POINT_3000);
        skuList.add(SKU_POINT_5000);
        skuList.add(SKU_POINT_10000);
        skuList.add(SKU_POINT_30000);

        List<String> bonuses = Arrays.asList(context.getResources().getStringArray(R.array.store_bonus));
        bonusMap = new HashMap<>();
        bonusMap.put(SKU_POINT_1000, bonuses.get(0));
        bonusMap.put(SKU_POINT_3000, bonuses.get(1));
        bonusMap.put(SKU_POINT_5000, bonuses.get(2));
        bonusMap.put(SKU_POINT_10000, bonuses.get(3));
        bonusMap.put(SKU_POINT_30000, bonuses.get(4));

        priceMap = new HashMap<>();
        priceMap.put(SKU_POINT_1000, POINT_1000);
        priceMap.put(SKU_POINT_3000, POINT_3000);
        priceMap.put(SKU_POINT_5000, POINT_5000);
        priceMap.put(SKU_POINT_10000, POINT_10000);
        priceMap.put(SKU_POINT_30000, POINT_30000);
    }

    public void setListener(OnQueryInventory listener) {
        this.listener = listener;
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public Map<String, String> getBonusMap() {
        return bonusMap;
    }

    public int getPrice(String sku) {
        return priceMap.get(sku);
    }

    public void launchPurchase(Activity activity, int product) {
        try {
            Log.d(TAG, "launchPurchase > product : " + product + ", sku : " + skuList.get(product));
            helper.launchPurchaseFlow(activity, skuList.get(product), REQUEST_CODE_PURCHASE, this);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "launchPurchase > error : " + e.getMessage());
            listener.onPurchaseFailed();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (helper == null) return false;

        Log.d(TAG, "handleActivityREsult > requestCode : " + requestCode + ", resultCode : " + resultCode);

        return helper.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {

        Log.d(TAG, "onIabSetupFinished > service connected");

        if (result.isSuccess()) {

            if (helper == null) return;

            // TODO : register receiveBroadcast
            try {

                helper.queryInventoryAsync(true, skuList, null, this);

            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e(TAG, "queryInventoryAsync > e : " + e.getMessage());
                listener.onQueryFailed();
            }
        } else {
            Log.e(TAG, "onIabSetupFinished error!!!!");
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

        Log.d(TAG, "Query Inventory finished");

        if (helper == null || result.isFailure()) {
            Log.e(TAG, "onQueryInventoryFinished error!!!!");
            listener.onQueryFailed();
        } else {
            try {
                // TODO : ?????
                helper.consumeAsync(inventory.getAllPurchases(), this);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e(TAG, "consumeAsync > error : " + e.getMessage());
                listener.onConsumeFailed();
            }

            for (String sku : skuList) {
                SkuDetails details = inventory.getSkuDetails(sku);
                Log.d(TAG, "title : " + details.getTitle());
                Log.d(TAG, "price : " + details.getPrice());
                Log.d(TAG, "currencyCode : " + details.getPriceCurrencyCode());
                Log.d(TAG, "type : " + details.getType());
                Log.d(TAG, "sku : " + details.getSku());
            }

            listener.onQueryFinished(inventory);
        }
    }

    public void dismiss() {

        Log.d(TAG, "dismiss");

        if (helper != null) {
            helper.disposeWhenFinished();
            helper = null;
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

        Log.d(TAG, "onIabPurchaseFinished > result is Success : " + result.isSuccess());

        if (helper == null || result.isFailure()) {
            listener.onPurchaseFailed();
        } else {

            Log.d(TAG, "itemType : " + purchase.getItemType());
            Log.d(TAG, "purchase State : " + purchase.getPurchaseState());
            Log.d(TAG, "Order Id : " + purchase.getOrderId());

            try {
                helper.consumeAsync(purchase, this);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e(TAG, "consumeAsync > error : " + e.getMessage());
                listener.onConsumeFailed();
            }
        }
    }

    @Override
    public void onConsumeFinished(Purchase purchase, IabResult result) {

        Log.d(TAG, "onConsumeFinished > result : " + result.isSuccess());

        if (helper == null || result.isFailure()) {
            listener.onConsumeFailed();
        } else {

            listener.onConsumePurchase(purchase.getSku());

            /*
            int point = 0;

            switch (purchase.getSku()) {
                case SKU_POINT_1000 :
                    point = POINT_1000;
                    break;
                case SKU_POINT_3000 :
                    point = POINT_3000;
                    break;
                case SKU_POINT_5000 :
                    point = POINT_5000;
                    break;
                case SKU_POINT_10000 :
                    point = POINT_10000;
                    break;
                case SKU_POINT_30000 :
                    point = POINT_30000;
                    break;
                case SKU_POINT_50000 :

                    break;
            }

            UserInfo userInfo = UserInfoDaoImpl.getInstance(context).getUserInfo();
            point = point + userInfo.getBalloon();
            userInfo.setBalloon(point);

            UpdateUserInfoTask task = new UpdateUserInfoTask(context);
            final int finalPoint = point;
            task.setCallback(new ISimpleTaskCallback() {

                @Override
                public void onPrepare() {

                }

                @Override
                public void onSuccess(@Nullable Object object) {
                    listener.onConsumePurchase();
                    ChangePointObservable.getInstance().change(finalPoint);
                }

                @Override
                public void onFailed() {

                }
            });
            task.execute(userInfo);
            */
        }
    }

    @Override
    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
        if (helper == null) {
            listener.onConsumeFailed();
        }
    }

    public interface OnQueryInventory {
        void onQueryFinished(Inventory inventory);
        void onPurchaseFinished(String sku);
        void onConsumePurchase(String sku);
        void onQueryFailed();
        void onPurchaseFailed();
        void onConsumeFailed();
    }
}
