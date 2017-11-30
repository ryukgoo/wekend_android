package com.entuition.wekend.data.source.product;

import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public interface ProductInfoDataSource {

    interface GetProductCallback {
        void onProductInfoLoaded(ProductInfo info);
        void onDataNotAvailable();
    }

    interface LoadProductListCallback {
        void onProductListLoaded(List<ProductInfo> infos);
        void onDataNotAvailable();
    }

    interface LoadProductReadStatesCallback {
        void onProductReadStatesLoaded(Map<Integer, ProductReadState> readStates);
        void onDataNotAvailable();
    }

    interface GetLocationCallback {
        void onProductLocationLoaded(LatLng location);
        void onAddressNotAvailable();
    }

    void clear();

    void refreshProductInfos();

    ProductInfo getProductInfo(int id);

    void getProductInfo(int id, @NonNull GetProductCallback callback);

    void loadProductList(@NonNull LoadProductListCallback callback);

    void loadProductList(ProductFilterOptions options, @NonNull LoadProductListCallback callback);

    void loadProductList(String keyword, @NonNull LoadProductListCallback callback);

    void getPaginatedList(@NonNull LoadProductListCallback callback);

    void getProductReadStates(@NonNull LoadProductReadStatesCallback callback);

    void getProductLocation(@NonNull String address, @NonNull GetLocationCallback callback);

    ProductFilterOptions getFilterOptions();

    boolean isReadLikeInfo(LikeInfo info);
}
