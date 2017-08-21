package com.entuition.wekend.model.data.user;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.NoticeInfo;
import com.entuition.wekend.model.data.SharedPreferencesWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 2. 22..
 */
public class UserInfoDaoImpl implements IUserInfoDao {

    private static final String TAG = "UserInfoDaoImpl";

    private static UserInfoDaoImpl sInstance = null;

    public static UserInfoDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (UserInfoDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new UserInfoDaoImpl();
                }
            }
        }
        return sInstance;
    }

    private String hostUserId;
    private HashMap<String, UserInfo> userInfos;
    private DynamoDBMapper mapper;

    public UserInfoDaoImpl() {
        AmazonDynamoDBClient ddbClient = CognitoSyncClientManager.getDynamoDBClient();
        mapper = new DynamoDBMapper(ddbClient);
        userInfos = new HashMap<String, UserInfo>();
    }

    public void clear() {
        hostUserId = null;
        userInfos = new HashMap<String, UserInfo>();
    }

    @Override
    public String getTableStatus() {
        return null;
    }

    @Override
    public String getUserId(Context context) {
        if (hostUserId == null) {
            hostUserId = SharedPreferencesWrapper.getUserIdFromSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
        }
        return hostUserId;
    }

    public UserInfo loadUserInfo(String userId) {
        try {
            UserInfo userInfo = mapper.load(UserInfo.class, userId);
            userInfos.put(userId, userInfo);
            return userInfo;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    @Override
    public UserInfo getUserInfo(String userId) {

        UserInfo userInfo = userInfos.get(userId);

        if (userInfo == null) {
            userInfo = loadUserInfo(userId);
        }

        return userInfo;
    }

    @Override
    public boolean updateUserInfo(UserInfo userInfo) {
        Log.d(TAG, "updateUserInfo > userInfo : " + userInfo.toString());
        try {
            mapper.save(userInfo);

            String userId = userInfo.getUserId();
            userInfos.put(userId, userInfo);

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    @Override
    public void deleteUserInfo(String userid) {
        try {
            UserInfo deleteUser = mapper.load(UserInfo.class, userid);
            mapper.delete(deleteUser);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {

        Log.d(TAG, "isNicknameAvailable > nickname : " + nickname);

        UserInfo info = new UserInfo();
        info.setNickname(nickname);

        DynamoDBQueryExpression<UserInfo> queryExpression = new DynamoDBQueryExpression<UserInfo>()
                .withIndexName(Constants.INDEX_NICKNAME)
                .withConsistentRead(false)
                .withHashKeyValues(info);

        try {
            List<UserInfo> userInfos = mapper.query(UserInfo.class, queryExpression);
            return !(userInfos != null && userInfos.size() > 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return false;
    }

    public boolean isValidEmail(String email) {
        UserInfo info = new UserInfo();
        info.setUsername(email);

        DynamoDBQueryExpression<UserInfo> queryExpression = new DynamoDBQueryExpression<UserInfo>()
                .withIndexName(Constants.INDEX_USERNAME)
                .withConsistentRead(false)
                .withHashKeyValues(info);

        try {
            List<UserInfo> results = mapper.query(UserInfo.class, queryExpression);
            return !(results != null && results.size() > 0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return false;
    }

    public List<NoticeInfo> getNoticeInfoList(String type) {

        Log.d(TAG, "getNoticeInfoList > type : " + type);

        NoticeInfo noticeInfo = new NoticeInfo();
        noticeInfo.setType(type);

        DynamoDBQueryExpression<NoticeInfo> queryExpression = new DynamoDBQueryExpression<NoticeInfo>()
                .withIndexName(Constants.NOTICE_INFO_INDEX_TYPE_TIME)
                .withHashKeyValues(noticeInfo)
                .withConsistentRead(false)
                .withScanIndexForward(true);

        try {
            List<NoticeInfo> notices = new ArrayList<NoticeInfo>();
            PaginatedList<NoticeInfo> result = mapper.query(NoticeInfo.class, queryExpression);
            for (NoticeInfo info : result) { notices.add(info); }
            return notices;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return new ArrayList<NoticeInfo>();
        }
    }

    public static String getUploadedPhotoFileName(String folderName, int position) {
        return folderName + "/" + getPhotoFileName(position);
    }

    public static String getPhotoFileName(int position) {
        return getPhotoTempName(position) + Constants.JPG_FILE_FORMAT;
    }

    public static String getPhotoTempName(int position) {
        return Constants.PROFILE_IMAGE_NAME_PREFIX + "_" + position;
    }
}