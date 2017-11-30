package com.entuition.wekend.data.source.like;

import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.like.local.FriendReadState;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.userinfo.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public interface LikeInfoDataSource {

    interface GetLikeInfoCallback {
        void onLikeInfoLoaded(LikeInfo info);
        void onDataNotAvailable();
    }

    interface LoadLikeInfoListCallback {
        void onLikeInfoListLoaded(List<LikeInfo> likeInfos);
        void onDataNotAvailable();
    }

    interface AddLikeCallback {
        void onCompleteAddLike(LikeInfo info);
        void onFailedAddLike();
    }

    interface DeleteLikeCallback {
        void onCompleteDeleteLike(LikeInfo info, int rem);
        void onFailedDeleteLike();
    }

    interface GetLikeCountCallback {
        void onFriendCountLoaded(int count);
        void onDataNotAvailable();
    }

    interface GetLikeReadStatesCallback {
        void onReadStateLoaded(Map<Integer, String> readStates);
        void onDataNotAvailable();
    }

    interface GetFriendReadStatesCallback {
        void onReadStateLoaded(Map<String, FriendReadState> readStateMap);
        void onDataNotAvailable();
    }

    void clear();

    void refreshLikeInfos();

    void getLikeInfo(String userId, int productId, @NonNull GetLikeInfoCallback callback);

    void loadLikeInfos(@NonNull LoadLikeInfoListCallback callback);

    void loadLikeInfos(int productId, String gender, @NonNull LoadLikeInfoListCallback callback);

    void readLike(LikeInfo info);

    void addLike(UserInfo userInfo, ProductInfo productInfo, @NonNull AddLikeCallback callback);

    void deleteLike(LikeInfo info, DeleteLikeCallback callback);

    void getLikeReadStates(GetLikeReadStatesCallback callback);

    void readFriend(LikeInfo info);

    void getFriendReadState(GetFriendReadStatesCallback callback);

    void getLikeCount(int productId, @NonNull GetLikeCountCallback callback);

    void getLikeCount(int productId, String gender, @NonNull GetLikeCountCallback callback);

    boolean containsProductKey(int productId);
}
