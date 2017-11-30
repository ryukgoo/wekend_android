package com.entuition.wekend.data.source.product.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.product.ProductFilterOptions;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class ProductInfoLocalDataSource implements ProductInfoDataSource {

    public static final String TAG = ProductInfoLocalDataSource.class.getSimpleName();

    private static ProductInfoLocalDataSource INSTANCE = null;

    public static ProductInfoLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (ProductInfoLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProductInfoLocalDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private final Context context;

    private ProductInfoLocalDataSource(Context context) {
        this.context = context;
    }

    @Override
    public void clear() {

    }

    @Override
    public void refreshProductInfos() {

    }

    @Override
    public ProductInfo getProductInfo(int id) {
        return null;
    }

    @Override
    public void getProductInfo(int id, @NonNull GetProductCallback callback) {

    }

    @Override
    public void loadProductList(@NonNull LoadProductListCallback callback) {

    }

    @Override
    public void loadProductList(ProductFilterOptions options, @NonNull LoadProductListCallback callback) {

    }

    @Override
    public void loadProductList(String keyword, @NonNull LoadProductListCallback callback) {

    }

    @Override
    public void getPaginatedList(@NonNull LoadProductListCallback callback) {

    }

    @Override
    public void getProductReadStates(@NonNull LoadProductReadStatesCallback callback) {

    }

    @Override
    public void getProductLocation(@NonNull String address, @NonNull GetLocationCallback callback) {

    }

    @Override
    public ProductFilterOptions getFilterOptions() {
        return null;
    }

    @Override
    public boolean isReadLikeInfo(LikeInfo info) {
        return false;
    }
}
