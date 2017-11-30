package com.entuition.wekend.view.main.campaign.adapter;

import com.entuition.wekend.data.source.product.ProductInfo;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public interface CampaignItemListener {
    void onClickCampaignItem(ProductInfo info);
    void onClickLikeCampaign(ProductInfo info);
}
