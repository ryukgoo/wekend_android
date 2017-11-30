package com.entuition.wekend.view.main.likelist.viewmodel;

import com.daimajia.swipe.SwipeLayout;

/**
 * Created by ryukgoo on 2017. 11. 13..
 */

public interface LikeListNavigator {

    void gotoCampaignDetail(int productId);
    void onRemoveSwipeLayout(SwipeLayout layout);

    void onDeleteLikeItem(int position);
    void onDeleteLikeItemFailed();

    void onRefreshLikeItem(int position);

}
