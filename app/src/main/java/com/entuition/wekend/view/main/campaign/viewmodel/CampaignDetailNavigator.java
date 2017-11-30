package com.entuition.wekend.view.main.campaign.viewmodel;

/**
 * Created by ryukgoo on 2017. 11. 12..
 */

public interface CampaignDetailNavigator {

    void showLoadMapFailed();
    void gotoGoogleMapView(String title, String address);
    void gotoRecommendFriendView(int productId);

    void call(String phone);

    void onCancelFacebookLink();
    void onErrorFacebookLink();

    void onFailedAddLike();
}
