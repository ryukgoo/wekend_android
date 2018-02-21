package com.entuition.wekend.data.source.product.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.entuition.wekend.data.CognitoSyncClientManager;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.data.source.product.ProductFilterOptions;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.ProductInfoDataSource;
import com.entuition.wekend.data.source.product.ProductReadState;
import com.entuition.wekend.util.Constants.ProductStatus;
import com.entuition.wekend.util.Utilities;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class ProductInfoRemoteDataSource implements ProductInfoDataSource {

    public static final String TAG = ProductInfoRemoteDataSource.class.getSimpleName();

    private static ProductInfoRemoteDataSource INSTANCE = null;

    public static ProductInfoRemoteDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (ProductInfoRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProductInfoRemoteDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private final Context context;
    private final DynamoDBMapper mapper;
    private Iterator<ProductInfo> iterator;

    private ProductInfoRemoteDataSource(Context context) {
        this.context = context;
        this.mapper = CognitoSyncClientManager.getDynamoDBMapper();
    }

    @Override
    public void clear() {
        iterator = null;
    }

    @Override
    public void refreshProductInfos() {
        iterator = null;
    }

    @Override
    public ProductInfo getProductInfo(int id) {
        return null;
    }

    @Override
    public void getProductInfo(int id, @NonNull GetProductCallback callback) {
        new GetProductTask(mapper, callback).execute(id);
    }

    @Override
    public void loadProductList(@NonNull LoadProductListCallback callback) {
        ProductFilterOptions options = new ProductFilterOptions.Builder().build();
        loadProductList(options, callback);
    }

    @Override
    public void loadProductList(ProductFilterOptions options, @NonNull final LoadProductListCallback callback) {

        new LoadProductListTask(mapper, options, new LoadProductListCallback() {
            @Override
            public void onProductListLoaded(List<ProductInfo> infos) {
                if (PaginatedQueryList.class.isInstance(infos)) {
                    iterator = infos.iterator();
                    getSubList(callback);
                }
            }

            @Override
            public void onDataNotAvailable() {
                iterator = null;
                callback.onDataNotAvailable();
            }
        }).execute();
    }

    @Override
    public void loadProductList(String keyword, @NonNull LoadProductListCallback callback) {
        ProductFilterOptions options = new ProductFilterOptions.Builder().build();
        options.setKeyword(keyword);
        loadProductList(options, callback);
    }

    @Override
    public void getPaginatedList(@NonNull LoadProductListCallback callback) {
        getSubList(callback);
    }

    @Override
    public void getProductReadStates(@NonNull LoadProductReadStatesCallback callback) {
        new LoadReadStatesTask(mapper, callback).execute();
    }

    @Override
    public void getProductLocation(@NonNull String address, @NonNull GetLocationCallback callback) {
        new GetLocationTask(context, callback).execute(address);
    }

    @Override
    public ProductFilterOptions getFilterOptions() {
        return null;
    }

    @Override
    public boolean isReadLikeInfo(LikeInfo info) {
        return false;
    }

    private void getSubList(LoadProductListCallback callback) {
        new GetSubListTask(iterator, callback).execute();
    }

    private static class GetProductTask extends AsyncTask<Integer, Void, ProductInfo> {

        private final DynamoDBMapper mapper;
        private final GetProductCallback callback;

        GetProductTask(DynamoDBMapper mapper, GetProductCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected ProductInfo doInBackground(Integer... ids) {
            return mapper.load(ProductInfo.class, ids[0]);
        }

        @Override
        protected void onPostExecute(ProductInfo productInfo) {
            if (productInfo == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onProductInfoLoaded(productInfo);
            }
        }
    }

    private static class LoadProductListTask extends AsyncTask<Void, Void, List<ProductInfo>> {

        private final DynamoDBMapper mapper;
        private final ProductFilterOptions options;
        private final LoadProductListCallback callback;

        LoadProductListTask(@NonNull DynamoDBMapper mapper,
                                   @NonNull ProductFilterOptions options,
                                   @NonNull LoadProductListCallback callback) {
            this.mapper = mapper;
            this.options = new ProductFilterOptions.Builder().cloneFrom(options).build();
            this.callback = callback;
        }

        @Override
        protected List<ProductInfo> doInBackground(Void... voids) {

            Log.d(TAG, "LoadProductListTask > keyword : " + options.getKeyword());

            ProductInfo keyValue = new ProductInfo();
            keyValue.setStatus(ProductStatus.Enabled.toString());

            DynamoDBQueryExpression<ProductInfo> queryExpression = new DynamoDBQueryExpression<ProductInfo>()
                    .withIndexName(options.getIndexName())
                    .withHashKeyValues(keyValue)
                    .withConsistentRead(false)
                    .withScanIndexForward(false)
                    .withLimit(ProductInfo.ITEM_COUNT_PER_PAGE);

            String filterExpression = options.getFilterExpress();
            Map<String, AttributeValue> attributeValueMap = options.getAttributeValueMap();

            if (filterExpression != null && !attributeValueMap.isEmpty()) {
                queryExpression.withFilterExpression(filterExpression)
                        .withExpressionAttributeValues(attributeValueMap);
            }

            try {
                return mapper.query(ProductInfo.class, queryExpression);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ProductInfo> results) {
            if (results != null) {
                callback.onProductListLoaded(results);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    private static class GetSubListTask extends AsyncTask<Void, Void, List<ProductInfo>> {

        private final Iterator<ProductInfo> infoIterator;
        private final LoadProductListCallback callback;

        private GetSubListTask(Iterator<ProductInfo> infoIterator, LoadProductListCallback callback) {
            this.infoIterator = infoIterator;
            this.callback = callback;
        }

        @Override
        protected List<ProductInfo> doInBackground(Void... voids) {

            List<ProductInfo> items = new ArrayList<>();

            for (int i = 0; infoIterator.hasNext() && i < ProductInfo.ITEM_COUNT_PER_PAGE ; i ++) {
                ProductInfo info = infoIterator.next();
                items.add(info);
            }

            return items;
        }

        @Override
        protected void onPostExecute(List<ProductInfo> list) {
            if (list != null) {
                callback.onProductListLoaded(list);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    private static class LoadReadStatesTask extends AsyncTask<Void, Void, Map<Integer, ProductReadState>> {

        private final DynamoDBMapper mapper;
        private final LoadProductReadStatesCallback callback;

        LoadReadStatesTask(DynamoDBMapper mapper, LoadProductReadStatesCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected Map<Integer, ProductReadState> doInBackground(Void... voids) {

            try {
                PaginatedScanList<ProductReadState> results = mapper.scan(ProductReadState.class, new DynamoDBScanExpression());

                if (results != null) {
                    Map<Integer, ProductReadState> readStateMap = new LinkedHashMap<>();
                    for (ProductReadState readState : results) {
                        readStateMap.put(readState.getProductId(), readState);
                    }
                    return readStateMap;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<Integer, ProductReadState> map) {
            if (map != null) {
                callback.onProductReadStatesLoaded(map);
            } else {
                callback.onDataNotAvailable();
            }
        }
    }

    private static class GetLocationTask extends AsyncTask<String, Void, LatLng> {

        private final Context context;
        private final GetLocationCallback callback;

        private GetLocationTask(Context context, GetLocationCallback callback) {
            this.context = context.getApplicationContext();
            this.callback = callback;
        }

        @Override
        protected LatLng doInBackground(String... strings) {
            return Utilities.getLocationFromAddress(context, strings[0]);
        }

        @Override
        protected void onPostExecute(LatLng location) {
            if (location == null) {
                callback.onAddressNotAvailable();
            } else {
                callback.onProductLocationLoaded(location);
            }
        }
    }
}
