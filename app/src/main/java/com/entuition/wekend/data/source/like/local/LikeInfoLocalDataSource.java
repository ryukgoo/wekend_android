package com.entuition.wekend.data.source.like.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.like.LikeInfoDataSource;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.util.AppExecutors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class LikeInfoLocalDataSource implements LikeInfoDataSource {

    public static final String TAG = LikeInfoLocalDataSource.class.getSimpleName();

    private static LikeInfoLocalDataSource INSTANCE = null;

    public static LikeInfoLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LikeInfoLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LikeInfoLocalDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private final FriendReadStateDao friendReadStateDao;
    private final LikeReadStateDao likeReadStateDao;
    private AppExecutors appExecutors;

    private Map<Integer, String> likeReadStateMap;

    private LikeInfoLocalDataSource(Context context) {
        this.friendReadStateDao = FriendReadStateDatabase.getInstance(context).readStateDao();
        this.likeReadStateDao = LikeReadStateDatabase.getInstance(context).readStateDao();
        this.appExecutors = new AppExecutors();
    }

    @Override
    public void clear() {
        clearCachedReadState();
    }

    @Override
    public void refreshLikeInfos() {}

    @Override
    public void getLikeInfo(String userId, int productId, @NonNull GetLikeInfoCallback callback) {

    }

    @Override
    public void loadLikeInfos(LoadLikeInfoListCallback callback) {}

    @Override
    public void loadLikeInfos(int productId, String gender, @NonNull LoadLikeInfoListCallback callback) {}

    @Override
    public void readLike(final LikeInfo info) {
        int productId = info.getProductId();
        String readTime = info.getReadTime();
        final LikeReadState readState = new LikeReadState(productId, readTime);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                likeReadStateDao.updateReadState(readState);
                if (likeReadStateMap == null) clearCachedReadState();
                likeReadStateMap.put(readState.getProductId(), readState.getReadTime());
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void addLike(UserInfo userInfo, ProductInfo productInfo, @NonNull AddLikeCallback callback) {

    }

    @Override
    public void deleteLike(LikeInfo info, DeleteLikeCallback callback) {
        final int productId = info.getProductId();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                likeReadStateDao.deleteReadState(productId);
                friendReadStateDao.deleteReadState(productId);
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getLikeReadStates(final GetLikeReadStatesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                refreshCachedMap(likeReadStateDao.getReadStates());
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onReadStateLoaded(likeReadStateMap);
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void readFriend(final LikeInfo info) {
        final FriendReadState readState = new FriendReadState(info);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                friendReadStateDao.updateReadState(readState);
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getFriendReadState(final GetFriendReadStatesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<FriendReadState> readStates = friendReadStateDao.getReadStates();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (readStates != null) {
                            Map<String, FriendReadState> readStateMap = new LinkedHashMap<>();
                            for (FriendReadState readState : readStates) {
                                Log.d(TAG, "readState > id : " + readState.getId());

                                readStateMap.put(readState.getId(), readState);
                            }

                            callback.onReadStateLoaded(readStateMap);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getLikeCount(int productId, @NonNull GetLikeCountCallback callback) {

    }

    @Override
    public void getLikeCount(int productId, String gender, @NonNull GetLikeCountCallback callback) {

    }

    @Override
    public boolean containsProductKey(int productId) {
        return false;
    }

    private void clearCachedReadState() {
        if (likeReadStateMap == null) {
            likeReadStateMap = new LinkedHashMap<>();
        }
        likeReadStateMap.clear();
    }

    private void refreshCachedMap(List<LikeReadState> readStates) {
        clearCachedReadState();
        for (LikeReadState readState : readStates) {
            likeReadStateMap.put(readState.getProductId(), readState.getReadTime());
        }
    }
}
