package com.entuition.wekend.model.data.like.asynctask;

import com.entuition.wekend.model.data.like.LikeDBItem;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 4. 11..
 */
public interface IGetLikeFriendCallback {
    void onSuccess(List<LikeDBItem> friendList);
    void onFailed();
}
