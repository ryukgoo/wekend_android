package com.entuition.wekend.data.source.like;

import android.content.Context;
import android.support.annotation.NonNull;

import com.amazonaws.util.DateUtils;
import com.entuition.wekend.data.source.like.local.FriendReadState;
import com.entuition.wekend.data.source.like.local.LikeInfoLocalDataSource;
import com.entuition.wekend.data.source.like.observable.AddLikeObservable;
import com.entuition.wekend.data.source.like.observable.DeleteLikeObservable;
import com.entuition.wekend.data.source.like.remote.LikeInfoRemoteDataSource;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductReadState;
import com.entuition.wekend.data.source.userinfo.UserInfo;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.util.Utilities;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class LikeInfoRepository implements LikeInfoDataSource {

    public static final String TAG = LikeInfoRepository.class.getSimpleName();

    private static LikeInfoRepository INSTANCE = null;

    public static LikeInfoRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LikeInfoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LikeInfoRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        LikeInfoLocalDataSource.destroyInstance();
        LikeInfoRemoteDataSource.destroyInstance();
        INSTANCE = null;
    }

    private final Context context;
    private final LikeInfoRemoteDataSource remoteDataSource;
    private final LikeInfoLocalDataSource localDataSource;

//    List<LikeInfo> cachedLikeInfos;
    Map<Integer, LikeInfo> cachedLikeInfoMap;

    private boolean isCacheDirty = false;

    private LikeInfoRepository(Context context) {
        this.context = context;
        this.remoteDataSource = LikeInfoRemoteDataSource.getInstance(context);
        this.localDataSource = LikeInfoLocalDataSource.getInstance(context);
    }

    @Override
    public void clear() {
        remoteDataSource.clear();
        localDataSource.clear();
        clearCachedLikeInfoMap();
    }

    @Override
    public void refreshLikeInfos() {
        isCacheDirty = true;
    }

    @Override
    public void getLikeInfo(String userId, int productId, @NonNull final GetLikeInfoCallback callback) {
        if (cachedLikeInfoMap != null && cachedLikeInfoMap.get(productId) != null) {
            callback.onLikeInfoLoaded(cachedLikeInfoMap.get(productId));
        }

        remoteDataSource.getLikeInfo(userId, productId, new GetLikeInfoCallback() {
            @Override
            public void onLikeInfoLoaded(LikeInfo info) {
                cachedLikeInfoMap.put(info.getProductId(), info);
                callback.onLikeInfoLoaded(info);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void loadLikeInfos(final LoadLikeInfoListCallback callback) {

        if (cachedLikeInfoMap != null && !isCacheDirty) {
            List<LikeInfo> likeInfoList = new ArrayList<>(cachedLikeInfoMap.values());
            callback.onLikeInfoListLoaded(likeInfoList);
            return;
        }

        remoteDataSource.loadLikeInfos(new LoadLikeInfoListCallback() {
            @Override
            public void onLikeInfoListLoaded(final List<LikeInfo> likeInfos) {
                refreshCacheMap(likeInfos);

                localDataSource.getLikeReadStates(new GetLikeReadStatesCallback() {
                    @Override
                    public void onReadStateLoaded(Map<Integer, String> readStates) {

                        // ??
                        callback.onLikeInfoListLoaded(likeInfos);
                        isCacheDirty = false;
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onLikeInfoListLoaded(likeInfos);
                        isCacheDirty = false;
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void loadLikeInfos(int productId, String gender, @NonNull final LoadLikeInfoListCallback callback) {

        remoteDataSource.loadLikeInfos(productId, gender, new LoadLikeInfoListCallback() {
            @Override
            public void onLikeInfoListLoaded(final List<LikeInfo> likeInfos) {

                localDataSource.getFriendReadState(new GetFriendReadStatesCallback() {
                    @Override
                    public void onReadStateLoaded(Map<String, FriendReadState> readStateMap) {

                        for (LikeInfo likeInfo : likeInfos) {

                            FriendReadState readState = readStateMap.get(likeInfo.getLikeId());
                            likeInfo.setRead(readState != null);
                        }

                        callback.onLikeInfoListLoaded(likeInfos);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onLikeInfoListLoaded(likeInfos);
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void readLike(LikeInfo info) {
        info.setReadTime(Utilities.getTimestamp());
        remoteDataSource.readLike(info);
        localDataSource.readLike(info);
    }

    @Override
    public void addLike(UserInfo userInfo, ProductInfo productInfo, @NonNull final AddLikeCallback callback) {
        remoteDataSource.addLike(userInfo, productInfo, new AddLikeCallback() {
            @Override
            public void onCompleteAddLike(LikeInfo info) {
//                cachedLikeInfos.add(0, info);
                cachedLikeInfoMap.put(info.getProductId(), info);

                callback.onCompleteAddLike(info);
                AddLikeObservable.getInstance().addLike(info);
            }

            @Override
            public void onFailedAddLike() {
                callback.onFailedAddLike();
            }
        });
    }

    @Override
    public void deleteLike(LikeInfo info, final DeleteLikeCallback callback) {
        remoteDataSource.deleteLike(info, new DeleteLikeCallback() {
            @Override
            public void onCompleteDeleteLike(LikeInfo info, int remainCount) {
                cachedLikeInfoMap.remove(info.getProductId());

                callback.onCompleteDeleteLike(info, cachedLikeInfoMap.size());
                DeleteLikeObservable.getInstance().deleteLike(info);
            }

            @Override
            public void onFailedDeleteLike() {
                callback.onFailedDeleteLike();
            }
        });
    }

    @Override
    public void getLikeReadStates(GetLikeReadStatesCallback callback) {

    }

    @Override
    public void readFriend(LikeInfo info) {
        localDataSource.readFriend(info);
    }

    @Override
    public void getFriendReadState(GetFriendReadStatesCallback callback) {}

    @Override
    public void getLikeCount(int productId, @NonNull GetLikeCountCallback callback) {
        remoteDataSource.getLikeCount(productId, callback);
    }

    @Override
    public void getLikeCount(int productId, String gender, @NonNull GetLikeCountCallback callback) {
        remoteDataSource.getLikeCount(productId, gender, callback);
    }

    @Override
    public boolean containsProductKey(int productId) {
        return cachedLikeInfoMap != null && cachedLikeInfoMap.containsKey(productId);
    }

    private void clearCachedLikeInfoMap() {
        if (cachedLikeInfoMap == null) {
            cachedLikeInfoMap = new LinkedHashMap<>();
        }
        cachedLikeInfoMap.clear();
    }

    private void refreshCacheMap(List<LikeInfo> items) {
        clearCachedLikeInfoMap();
        for (LikeInfo info : items) {
            cachedLikeInfoMap.put(info.getProductId(), info);
        }

        isCacheDirty = false;
    }

    private final static Comparator<LikeInfo> comparator = new Comparator<LikeInfo>() {
        @Override
        public int compare(LikeInfo o1, LikeInfo o2) {
            final Collator collator = Collator.getInstance();

            String o1Time = o1.getProductLikedTime();
            String o2Time = o2.getProductLikedTime();

            return collator.compare(o2Time, o1Time);
        }
    };

    public static List<LikeInfo> getSortedList(List<LikeInfo> likeInfos, Map<Integer, ProductReadState> readStateMap) {
        List<LikeInfo> results = new ArrayList<>();

        for (LikeInfo info : likeInfos) {
            if (info.getGender().equals(Constants.GenderValue.male.toString())) {
                ProductReadState readState = readStateMap.get(info.getProductId());
                info.setProductLikedTime(readState.getFemaleLikeTime());
            } else {
                ProductReadState readState = readStateMap.get(info.getProductId());
                info.setProductLikedTime(readState.getMaleLikeTime());
            }

            Date updatedTime = DateUtils.parseISO8601Date(info.getUpdatedTime());
            Date likedTime = DateUtils.parseISO8601Date(info.getProductLikedTime());

            if (updatedTime.getTime() > likedTime.getTime()) {
                info.setProductLikedTime(info.getUpdatedTime());
            }

            results.add(info);
        }

        Collections.sort(results, comparator);

        return results;
    }
}
