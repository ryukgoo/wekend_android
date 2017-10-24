package com.entuition.wekend.model.data.user;

/**
 * Created by ryukgoo on 2016. 2. 22..
 */
public interface IUserInfoDao {
    String getUserId();
    UserInfo getUserInfo();
    UserInfo getUserInfo(String userId);
    boolean updateUserInfo(UserInfo userInfo);
    boolean deleteUserInfo(String userId);
}
