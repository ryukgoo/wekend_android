package com.entuition.wekend.model.googleservice.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.data.user.asynctask.UpdateUserInfoTask;
import com.entuition.wekend.view.main.ChangePointObservable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 10. 4..
 */

public class GoogleBillingController implements IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener,
        IabHelper.OnIabPurchaseFinishedListener, IabHelper.OnConsumeFinishedListener, IabHelper.OnConsumeMultiFinishedListener {

    private final String TAG = getClass().getSimpleName();

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
                    instance = new GoogleBillingController(context);
                }
            }
        }
        return instance;
    }

    private Context context;
    private IabHelper helper;
    private List<String> skuList;
    private OnQueryInventory listener;

    public GoogleBillingController(Context context) {
        this.context = context;
    }

    public void init() {
        this.helper = new IabHelper(context, context.getString(R.string.google_public_key));
        this.helper.startSetup(this);
    }

    public void setListener(OnQueryInventory listener) {
        this.listener = listener;
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public void launchPurchase(Activity activity, int product) {
        try {
            helper.launchPurchaseFlow(activity, skuList.get(product), REQUEST_CODE_PURCHASE, this, "");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "launchPurchase > error : " + e.getMessage());
            listener.onPurchaseFailed();
        }
    }

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (helper == null) return false;
        return helper.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {

        Log.d(TAG, "onIabSetupFinished > service connected");

        if (result.isSuccess()) {

            if (helper == null) return;

            // TODO : register receiveBroadcast
            try {

                skuList = new ArrayList<>();
                skuList.add(SKU_POINT_1000);
                skuList.add(SKU_POINT_3000);
                skuList.add(SKU_POINT_5000);
                skuList.add(SKU_POINT_10000);
                skuList.add(SKU_POINT_30000);
                skuList.add(SKU_POINT_50000);

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
            Log.d(TAG, "purchase size : " + inventory.getAllPurchases().size());
            Log.d(TAG, "AllOwnedSkus size : " + inventory.getAllOwnedSkus().size());
            try {
                helper.consumeAsync(inventory.getAllPurchases(), this);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.e(TAG, "consumeAsync > error : " + e.getMessage());
                listener.onConsumeFailed();
            }

            listener.onQueryFinished();
        }
    }

    public void dismiss() {
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
            return;
        } else {

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

            UserInfo userInfo = UserInfoDaoImpl.getInstance().getUserInfo(UserInfoDaoImpl.getInstance().getUserId(context));
            point = point + userInfo.getBalloon();
            userInfo.setBalloon(point);

            UpdateUserInfoTask task = new UpdateUserInfoTask();
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
        }
    }

    @Override
    public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
        if (helper == null) {
            listener.onConsumeFailed();
            return;
        }
    }

    public interface OnQueryInventory {
        void onQueryFinished();
        void onPurchaseFinished(String sku);
        void onConsumePurchase();
        void onQueryFailed();
        void onPurchaseFailed();
        void onConsumeFailed();
    }
}
