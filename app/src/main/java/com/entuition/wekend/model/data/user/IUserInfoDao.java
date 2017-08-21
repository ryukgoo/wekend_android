package com.entuition.wekend.model.data.user;

import android.content.Context;

/**
 * Created by ryukgoo on 2016. 2. 22..
 */
public interface IUserInfoDao {
    String getTableStatus();
    String getUserId(Context context);
    UserInfo loadUserInfo(String userId);
    UserInfo getUserInfo(String userId);
    boolean updateUserInfo(UserInfo userInfo);
    void deleteUserInfo(String userid);
    boolean isNicknameAvailable(String nickname);
}
