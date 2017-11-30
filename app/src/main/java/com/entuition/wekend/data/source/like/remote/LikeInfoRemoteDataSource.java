package com.entuition.wekend.data.source.like.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.entuition.wekend.data.CognitoSyncClientManager;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoDataSource;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;
import com.entuition.wekend.util.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class LikeInfoRemoteDataSource implements LikeInfoDataSource {

    public static final String TAG = LikeInfoRemoteDataSource.class.getSimpleName();

    private static LikeInfoRemoteDataSource INSTANCE = null;

    public static LikeInfoRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LikeInfoRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LikeInfoRemoteDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final String FILTER_KEY_LIKE_GENDER = ":gender";

    private final Context context;
    private final DynamoDBMapper mapper;

    private LikeInfoRemoteDataSource(Context context) {
        this.context = context;
        this.mapper = CognitoSyncClientManager.getDynamoDBMapper();
    }

    @Override
    public void clear() {

    }

    @Override
    public void refreshLikeInfos() {

    }

    @Override
    public void getLikeInfo(String userId, int productId, @NonNull GetLikeInfoCallback callback) {
        LikeInfo args = new LikeInfo();
        args.setUserId(userId);
        args.setProductId(productId);
        new LoadLikeInfoTask(mapper, callback).execute(args);
    }

    @Override
    public void loadLikeInfos(LoadLikeInfoListCallback callback) {
        String userId = UserInfoRepository.getInstance(context).getUserId();
        new LoadLikeInfosTask(mapper, callback).execute(userId);
    }

    @Override
    public void loadLikeInfos(int productId, String gender, @NonNull LoadLikeInfoListCallback callback) {
        LikeInfo args = new LikeInfo();
        args.setProductId(productId);
        args.setGender(gender);
        new LoadLikedFriendTask(mapper, callback).execute(args);
    }

    @Override
    public void readLike(LikeInfo info) {
        new UpdateLikeTask(mapper).execute(info);
    }

    @Override
    public void addLike(UserInfo userInfo, ProductInfo productInfo, @NonNull AddLikeCallback callback) {

        LikeInfo info = new LikeInfo();
        info.setUserId(userInfo.getUserId());
        info.setProductId(productInfo.getId());
        info.setNickname(userInfo.getNickname());
        info.setGender(userInfo.getGender());
        info.setProductTitle(productInfo.getTitleKor());
        info.setProductDesc(productInfo.getDescription());
        info.setUpdatedTime(Utilities.getTimestamp());
        info.setLikeId(UUID.randomUUID().toString());

        new AddLikeTask(mapper, callback).execute(info);
    }

    @Override
    public void deleteLike(LikeInfo info, DeleteLikeCallback callback) {
        new DeleteLikeTask(mapper, callback).execute(info);
    }

    @Override
    public void getLikeReadStates(GetLikeReadStatesCallback callback) {

    }

    @Override
    public void readFriend(LikeInfo info) {

    }

    @Override
    public void getFriendReadState(GetFriendReadStatesCallback callback) {

    }

    @Override
    public void getLikeCount(int productId, @NonNull GetLikeCountCallback callback) {
        LikeInfo info = new LikeInfo();
        info.setProductId(productId);
        new GetLikedFriendCountTask(mapper, callback).execute(info);
    }

    @Override
    public void getLikeCount(int productId, String gender, @NonNull GetLikeCountCallback callback) {
        LikeInfo info = new LikeInfo();
        info.setProductId(productId);
        info.setGender(gender);
        new GetLikedFriendCountTask(mapper, callback).execute(info);
    }

    @Override
    public boolean containsProductKey(int productId) {
        return false;
    }

    private static class AddLikeTask extends AsyncTask<LikeInfo, Void, LikeInfo> {

        private final DynamoDBMapper mapper;
        private final AddLikeCallback callback;

        AddLikeTask(DynamoDBMapper mapper, AddLikeCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected LikeInfo doInBackground(LikeInfo... likeInfos) {
            try {
                mapper.save(likeInfos[0]);
                return likeInfos[0];
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(LikeInfo info) {
            if (info != null) {
                callback.onCompleteAddLike(info);
            } else {
                callback.onFailedAddLike();
            }
        }
    }

    private static class DeleteLikeTask extends AsyncTask<LikeInfo, Void, LikeInfo> {

        private final DynamoDBMapper mapper;
        private final DeleteLikeCallback callback;

        DeleteLikeTask(DynamoDBMapper mapper, DeleteLikeCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected LikeInfo doInBackground(LikeInfo... likeInfos) {
            try {
                mapper.delete(likeInfos[0]);
                return likeInfos[0];
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(LikeInfo info) {
            if (info != null) {
                callback.onCompleteDeleteLike(info, 0);
            } else {
                callback.onFailedDeleteLike();
            }
        }
    }

    private static class UpdateLikeTask extends AsyncTask<LikeInfo, Void, LikeInfo> {

        private final DynamoDBMapper mapper;

        UpdateLikeTask(DynamoDBMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        protected LikeInfo doInBackground(LikeInfo... likeInfos) {
            LikeInfo item = likeInfos[0];
            try {
                mapper.save(item);
                return item;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(LikeInfo info) {}
    }

    private static class LoadLikeInfoTask extends AsyncTask<LikeInfo, Void, LikeInfo> {

        private final DynamoDBMapper mapper;
        private final GetLikeInfoCallback callback;

        LoadLikeInfoTask(DynamoDBMapper mapper, GetLikeInfoCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected LikeInfo doInBackground(LikeInfo... likeInfos) {
            try {
                return mapper.load(LikeInfo.class, likeInfos[0].getUserId(), likeInfos[0].getProductId());
            } catch (Exception e) {
                Log.e(TAG, "load LikeInfo Error : " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(LikeInfo info) {
            if (info == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onLikeInfoLoaded(info);
            }
        }
    }

    private static class LoadLikeInfosTask extends AsyncTask<String, Void, List<LikeInfo>> {

        private final DynamoDBMapper mapper;
        private final LoadLikeInfoListCallback callback;

        LoadLikeInfosTask(DynamoDBMapper mapper, LoadLikeInfoListCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected List<LikeInfo> doInBackground(String... strings) {

            LikeInfo keyValue = new LikeInfo();
            keyValue.setUserId(strings[0]);

            DynamoDBQueryExpression<LikeInfo> queryExpression = new DynamoDBQueryExpression<LikeInfo>()
                    .withIndexName(LikeInfo.Index.USERID_UPDATEDTIME)
                    .withHashKeyValues(keyValue)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            try {
                List<LikeInfo> result = new ArrayList<>();
                result.addAll(mapper.query(LikeInfo.class, queryExpression));
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<LikeInfo> list) {
            if (list != null) {
                callback.onLikeInfoListLoaded(list);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    private static class GetLikedFriendCountTask extends AsyncTask<LikeInfo, Void, Integer> {

        private final DynamoDBMapper mapper;
        private final GetLikeCountCallback callback;

        GetLikedFriendCountTask(DynamoDBMapper mapper, GetLikeCountCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(LikeInfo... likeInfos) {

            LikeInfo item = new LikeInfo();
            item.setProductId(likeInfos[0].getProductId());

            DynamoDBQueryExpression<LikeInfo> queryExpression = new DynamoDBQueryExpression<LikeInfo>()
                    .withIndexName(LikeInfo.Index.PRODUCTID_UPDATEDTIME)
                    .withHashKeyValues(item)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            if (likeInfos[0].getGender() != null) {

                String filterExpression = LikeInfo.Attribute.GENDER + " <> " + FILTER_KEY_LIKE_GENDER;
                Map<String, AttributeValue> filterKeyMap = new HashMap<String, AttributeValue>();
                filterKeyMap.put(FILTER_KEY_LIKE_GENDER, new AttributeValue().withS(likeInfos[0].getGender()));

                queryExpression.withFilterExpression(filterExpression)
                        .withExpressionAttributeValues(filterKeyMap);
            }

            try {
                return mapper.count(LikeInfo.class, queryExpression);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer != null && integer >= 0) {
                callback.onFriendCountLoaded(integer);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    private static class LoadLikedFriendTask extends AsyncTask<LikeInfo, Void, List<LikeInfo>> {

        private final DynamoDBMapper mapper;
        private final LoadLikeInfoListCallback callback;

        LoadLikedFriendTask(DynamoDBMapper mapper, LoadLikeInfoListCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected List<LikeInfo> doInBackground(LikeInfo... likeInfos) {

            LikeInfo info = new LikeInfo();
            info.setProductId(likeInfos[0].getProductId());

            String filterExpression = LikeInfo.Attribute.GENDER + " <> " + FILTER_KEY_LIKE_GENDER;
            Map<String, AttributeValue> filterKeyMap = new HashMap<String, AttributeValue>();
            filterKeyMap.put(FILTER_KEY_LIKE_GENDER, new AttributeValue().withS(likeInfos[0].getGender()));

            DynamoDBQueryExpression<LikeInfo> queryExpression = new DynamoDBQueryExpression<LikeInfo>()
                    .withIndexName(LikeInfo.Index.PRODUCTID_UPDATEDTIME)
                    .withHashKeyValues(info)
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeValues(filterKeyMap)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            try {
                List<LikeInfo> result = new ArrayList<>();
                result.addAll(mapper.query(LikeInfo.class, queryExpression));
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<LikeInfo> likeInfos) {
            if (likeInfos == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onLikeInfoListLoaded(likeInfos);
            }
        }
    }
}
