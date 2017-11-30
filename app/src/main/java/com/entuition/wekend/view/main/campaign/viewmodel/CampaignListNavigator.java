package com.entuition.wekend.view.main.campaign.viewmodel;

/**
 * Created by ryukgoo on 2017. 11. 12..
 */

public interface CampaignListNavigator {

    void gotoCampaignDetail(int productId);

    void onNotifyItemChanged(int position);
    void onFailedAddLike();
    void onFailedDeleteLike();

}
