package com.entuition.wekend.model.data.like;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 4. 8..
 */
public interface ILikeDBDao {
    LikeDBItem getLikeItem(String userId, int productId);
    void addLike(String userId, int productId, String gender, String productTitle, String productDesc, String endpointArn) throws Exception;
    void deleteLike(LikeDBItem likeDBItem) throws Exception;
    List<LikeDBItem> getLikedFriendList(int productId, String gender);
    int getLikedFriendCount(int productId, String gender);
    int getLikedTotalCount(int productId);

    void clearLikeData();
    void loadLikeProductList(String userId);

    List<LikeDBItem> getFriendSubList(int page);
}
