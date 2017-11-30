package com.entuition.wekend.view.main.store.viewmodel;

/**
 * Created by ryukgoo on 2017. 11. 17..
 */

public interface StoreNavigator {

    void purchaseItem(int position);

    void onPurchaseFinished();
    void onConsumePurchase();
    void onQueryInventoryFailed();
    void onPurchaseFailed();

}
