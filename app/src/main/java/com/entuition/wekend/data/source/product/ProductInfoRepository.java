package com.entuition.wekend.data.source.product;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.util.DateUtils;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.product.local.ProductInfoLocalDataSource;
import com.entuition.wekend.data.source.product.remote.ProductInfoRemoteDataSource;
import com.entuition.wekend.util.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class ProductInfoRepository implements ProductInfoDataSource {

    public static final String TAG = ProductInfoRepository.class.getSimpleName();

    private static ProductInfoRepository INSTANCE = null;

    public static ProductInfoRepository getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (ProductInfoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProductInfoRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private final Context context;
    private final ProductInfoLocalDataSource localDataSource;
    private final ProductInfoRemoteDataSource remoteDataSource;

    private Map<Integer, ProductInfo> cachedProductInfoMap;
    private List<ProductInfo> cachedProductInfoList;
    private ProductFilterOptions filterOptions;
    private Map<Integer, ProductReadState> cachedReadStates;

    private boolean isCacheDirty = false;

    private ProductInfoRepository(Context context) {
        this.context = context;
        this.localDataSource = ProductInfoLocalDataSource.getInstance(context);
        this.remoteDataSource = ProductInfoRemoteDataSource.getInstance(context);
        this.filterOptions = new ProductFilterOptions.Builder().build();
    }

    @Override
    public void clear() {
        localDataSource.clear();
        remoteDataSource.clear();
        clearProductInfos();
        clearProductInfoMap();
        clearReadStates();
        initQueryOptions();
    }

    @Override
    public void refreshProductInfos() {
        isCacheDirty = true;
    }

    @Override
    public ProductInfo getProductInfo(int id) {
        if (cachedProductInfoMap != null && cachedProductInfoMap.get(id) != null) {
            return cachedProductInfoMap.get(id);
        }
        return null;
    }

    @Override
    public void getProductInfo(final int id, @NonNull final GetProductCallback callback) {
        if (cachedProductInfoMap != null && cachedProductInfoMap.get(id) != null) {
            callback.onProductInfoLoaded(cachedProductInfoMap.get(id));
            return;
        }

        remoteDataSource.getProductInfo(id, new GetProductCallback() {
            @Override
            public void onProductInfoLoaded(ProductInfo info) {
                if (cachedProductInfoMap != null) {
                    cachedProductInfoMap.put(id, info);
                }
                callback.onProductInfoLoaded(info);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void loadProductList(@NonNull LoadProductListCallback callback) {
        loadProductListFromRemoteDataSource(filterOptions, callback);
    }

    @Override
    public void loadProductList(ProductFilterOptions options, @NonNull LoadProductListCallback callback) {
        filterOptions = new ProductFilterOptions.Builder().cloneFrom(options).build();
        loadProductListFromRemoteDataSource(filterOptions, callback);
    }

    @Override
    public void loadProductList(String keyword, @NonNull LoadProductListCallback callback) {
        filterOptions.setKeyword(keyword);
        loadProductListFromRemoteDataSource(filterOptions, callback);
    }

    @Override
    public void getPaginatedList(@NonNull final LoadProductListCallback callback) {
        remoteDataSource.getPaginatedList(new LoadProductListCallback() {
            @Override
            public void onProductListLoaded(List<ProductInfo> infos) {
                cachedProductInfoList.addAll(infos);
                putInfosToMap(infos);
                callback.onProductListLoaded(infos);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getProductReadStates(@NonNull final LoadProductReadStatesCallback callback) {
//        if (cachedReadStates != null && !isCacheDirty) {
//            callback.onProductReadStatesLoaded(cachedReadStates);
//            return;
//        }

        remoteDataSource.getProductReadStates(new LoadProductReadStatesCallback() {
            @Override
            public void onProductReadStatesLoaded(Map<Integer, ProductReadState> readStates) {
                clearReadStates();
                cachedReadStates.putAll(readStates);

                callback.onProductReadStatesLoaded(cachedReadStates);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getProductLocation(@NonNull String address, @NonNull GetLocationCallback callback) {
        remoteDataSource.getProductLocation(address, callback);
    }

    @Override
    public ProductFilterOptions getFilterOptions() {
        return filterOptions;
    }

    @Override
    public boolean isReadLikeInfo(LikeInfo info) {

        if (cachedReadStates == null) return false;

        ProductReadState readState = cachedReadStates.get(info.getProductId());
        if (readState == null) return false;

        if (info.getReadTime() != null) {

            Log.d(TAG, "title : " + info.getProductTitle());
            Log.d(TAG, "readTime : " + info.getReadTime());
            Log.d(TAG, "MailLikeTime : " + readState.getMaleLikeTime());
            Log.d(TAG, "FemaleLikeTime : " + readState.getFemaleLikeTime());

            Date readTime = DateUtils.parseISO8601Date(info.getReadTime());

            if (info.getGender().equals(Constants.GenderValue.male.toString())) {
                if (readState.getFemaleLikeTime() != null) {
                    Date likeTime = DateUtils.parseISO8601Date(readState.getFemaleLikeTime());
                    return (readTime.getTime() > likeTime.getTime());
                }
                return true;
            } else {
                if (readState.getMaleLikeTime() != null) {
                    Date likeTime = DateUtils.parseISO8601Date(readState.getMaleLikeTime());
                    return (readTime.getTime() > likeTime.getTime());
                }
                return true;
            }
        } else {
            if (info.getGender().equals(Constants.GenderValue.male.toString())) {
                return (readState.getFemaleLikeTime() == null);
            } else {
                return (readState.getMaleLikeTime() == null);
            }
        }
    }

    private void loadProductListFromRemoteDataSource(ProductFilterOptions options, final LoadProductListCallback callback) {
        remoteDataSource.loadProductList(options, new LoadProductListCallback() {
            @Override
            public void onProductListLoaded(List<ProductInfo> infos) {
                clearProductInfos();
                cachedProductInfoList.addAll(infos);
                clearProductInfoMap();
                putInfosToMap(infos);
                callback.onProductListLoaded(cachedProductInfoList);

                isCacheDirty = false;
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void clearProductInfos() {
        if (cachedProductInfoList == null) {
            cachedProductInfoList = new ArrayList<>();
        }
        cachedProductInfoList.clear();

    }

    private void clearProductInfoMap() {
        if (cachedProductInfoMap == null) {
            cachedProductInfoMap = new LinkedHashMap<>();
        }
        cachedProductInfoMap.clear();
    }

    private void clearReadStates() {
        if (cachedReadStates == null) {
            cachedReadStates = new LinkedHashMap<>();
        }
        cachedReadStates.clear();
    }

    private void putInfosToMap(List<ProductInfo> infos) {
        for (ProductInfo info : infos) {
            cachedProductInfoMap.put(info.getId(), info);
        }
    }

    private void initQueryOptions() {
        filterOptions = new ProductFilterOptions.Builder().build();
    }
}
