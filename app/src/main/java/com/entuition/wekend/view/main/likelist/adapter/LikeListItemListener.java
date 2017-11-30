package com.entuition.wekend.view.main.likelist.adapter;

import com.entuition.wekend.data.source.like.LikeInfo;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public interface LikeListItemListener {
    void onClickItem(LikeInfo info);
    void onClickDeleteItem(LikeInfo info);
}
