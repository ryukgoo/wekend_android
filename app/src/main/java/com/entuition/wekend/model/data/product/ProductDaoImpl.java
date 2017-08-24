package com.entuition.wekend.model.data.product;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.product.enums.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2016. 3. 10..
 */
public class ProductDaoImpl implements IProductInfoDao {

    private final String TAG = getClass().getSimpleName();

    private static final String FILTER_KEY_MAIN_CATEGORY = ":category";
    private static final String FILTER_KEY_SUB_CATEGORY = ":subCategory";
    private static final String FILTER_KEY_PRODUCT_REGION = ":productRegion";
    private static final String FILTER_KEY_SEARCH_KEYWORD = ":keyword";

    private static final String PRODUCT_STATUS_ENABLED = "Enabled";

    private static ProductDaoImpl sInstance = null;

    public static ProductDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (ProductDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new ProductDaoImpl();
                }
            }
        }
        return sInstance;
    }

    private DynamoDBMapper mapper;
    private CampaignQueryPage queryPage;
    private List<ProductInfo> productInfoList;
    private Map<Integer, ProductReadState> likeStates;

    public ProductDaoImpl() {
        productInfoList = new ArrayList<ProductInfo>();
        likeStates = new HashMap<Integer, ProductReadState>();
        AmazonDynamoDBClient client = CognitoSyncClientManager.getDynamoDBClient();
        mapper = new DynamoDBMapper(client);
    }

    public void clear() {
        productInfoList = new ArrayList<ProductInfo>();
        likeStates = new HashMap<Integer, ProductReadState>();
        queryPage = null;
    }

    @Override
    public ProductInfo getProductInfo(int id) {
        try {
            ProductInfo productInfo = mapper.load(ProductInfo.class, id);
            return productInfo;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    @Override
    public List<ProductInfo> loadProductList(ProductQueryOptions options, List<LikeDBItem> likeList) {

        if (productInfoList != null) {
            productInfoList.clear();
            productInfoList = null;
        }

        productInfoList = new ArrayList<ProductInfo>();
        DynamoDBQueryExpression<ProductInfo> queryExpression = getQueryExpression(options);

        try {
            PaginatedQueryList<ProductInfo> queryList = mapper.query(ProductInfo.class, queryExpression);
            queryPage = new CampaignQueryPage(queryList, Constants.ITEM_COUNT_PER_PAGE);
            return queryList;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    @Override
    public List<ProductInfo> getProductSubList() {
        if (queryPage != null) {

            List<ProductInfo> subList = queryPage.getSubList();
            productInfoList.addAll(subList);

            return subList;
        }
        return null;
    }

    @Override
    public List<ProductInfo> getProductList() {
        return productInfoList;
    }

    @Override
    public List<ProductInfo> searchProducts(String keyword, List<LikeDBItem> likeList) {

        ProductInfo searchInfo = new ProductInfo();
        searchInfo.setStatus(PRODUCT_STATUS_ENABLED);

        String filterExpression = "contains(" + Constants.ATTRIBUTE_TITLE_KOR + ", " + FILTER_KEY_SEARCH_KEYWORD + ")"
                + " or " + "contains(" + Constants.ATTRIBUTE_ADDRESS + ", " + FILTER_KEY_SEARCH_KEYWORD + ")"
                + " or " + "contains(" + Constants.ATTRIBUTE_DESCRIPTION + ", " + FILTER_KEY_SEARCH_KEYWORD + ")";

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(FILTER_KEY_SEARCH_KEYWORD, new AttributeValue().withS(keyword));

        DynamoDBQueryExpression<ProductInfo> queryExpression = new DynamoDBQueryExpression<ProductInfo>()
                .withIndexName(Constants.INDEX_STATUS_UPDATED_TIME)
                .withHashKeyValues(searchInfo)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false)
                .withScanIndexForward(false);

        try {
            PaginatedQueryList<ProductInfo> queryList = mapper.query(ProductInfo.class, queryExpression);

            if (queryList != null && productInfoList != null) {
                productInfoList.clear();
                productInfoList = null;

                productInfoList = new ArrayList<ProductInfo>();
            }

            queryPage = new CampaignQueryPage(queryList, Constants.ITEM_COUNT_PER_PAGE);

            return queryList;
        } catch (Exception e) {
            Log.e(TAG, "searchProducts > error : " + e.getMessage());
        }

        return null;
    }

    public int getPositionById(int productId) {

        if (productInfoList != null) {
            for (int i = 0 ; i < productInfoList.size() ; i ++) {
                ProductInfo productInfo = productInfoList.get(i);
                if (productInfo.getId() == productId) {
                    return i;
                }
            }
        }

        return -1;
    }

    public void addLikeByProductId(int productId, int likedCount, int friendCount) {
        for (ProductInfo productInfo : productInfoList) {
            if (productInfo.getId() == productId) {
                productInfo.setLikeCount(likedCount);
                productInfo.setLike(true);
                return;
            }
        }
    }

    public void deleteLikeByProductId(int productId, int likedCount) {
        for (ProductInfo productInfo : productInfoList) {
            if (productId == productInfo.getId()) {
                productInfo.setLikeCount(likedCount);
                productInfo.setLike(false);
                return;
            }
        }
    }

    public Map<Integer, ProductReadState> getLikeStates() {
        return likeStates;
    }

    public void getLikedTime() {

        Log.d(TAG, "LikeListLoad > getLIkedTime");

        likeStates = new HashMap<Integer, ProductReadState>();
        try {
            PaginatedList<ProductReadState> results = mapper.scan(ProductReadState.class, new DynamoDBScanExpression());

            Log.d(TAG, "LikeListLoad > getLIkedTime > scan time");

            if (results != null) {
                for (ProductReadState readState : results) {
                    likeStates.put(readState.getProductId(), readState);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "LikeListLoad > exception : " + e.toString());
        }

        Log.d(TAG, "LikeListLoad > getLIkedTime > finish");
    }

    private DynamoDBQueryExpression<ProductInfo> getQueryExpression(ProductQueryOptions options) {
        ProductInfo productKey = new ProductInfo();
        productKey.setStatus(PRODUCT_STATUS_ENABLED);
        String filterExpression = getFilterExpression(options);

        Map<String, AttributeValue> expressionAttributeValues = getExpressionAttributeValue(options);

        String indexName;
        if (options.getOrderType() == ProductQueryOptions.ORDER_BY_DATE) {
            indexName = Constants.INDEX_STATUS_UPDATED_TIME;
        } else if (options.getOrderType() == ProductQueryOptions.ORDER_BY_LIKE) {
            indexName = Constants.INDEX_STATUS_LIKECOUNT;
        } else {
            indexName = Constants.INDEX_STATUS_UPDATED_TIME;
        }

        DynamoDBQueryExpression<ProductInfo> queryExpression = new DynamoDBQueryExpression<ProductInfo>()
                .withIndexName(indexName)
                .withHashKeyValues(productKey)
                .withConsistentRead(false)
                .withScanIndexForward(false)
                .withLimit(Constants.ITEM_COUNT_PER_PAGE);

        if (filterExpression != null && !expressionAttributeValues.isEmpty()) {
            queryExpression.withFilterExpression(filterExpression)
                    .withExpressionAttributeValues(expressionAttributeValues);
        }

        return queryExpression;
    }

    private String getFilterExpression(ProductQueryOptions options) {
        String filterExpression = null;

        if (options.getMainCategory().getIdentifier() != Category.NONE.getIdentifier()) {
            filterExpression = Constants.ATTRIBUTE_MAIN_CATEGORY + " = " + FILTER_KEY_MAIN_CATEGORY;
        }

        if (options.getSubCategory().getIdentifier() != Category.NONE.getIdentifier()) {
            if (filterExpression != null) {
                filterExpression = filterExpression + " and " + Constants.ATTRIBUTE_SUB_CATEGORY + " = " + FILTER_KEY_SUB_CATEGORY;
            } else {
                filterExpression = Constants.ATTRIBUTE_SUB_CATEGORY + " = " + FILTER_KEY_SUB_CATEGORY;
            }
        }

        if (options.getProductRegion().getIdentifier() != Category.NONE.getIdentifier()) {
            if (filterExpression != null) {
                filterExpression = filterExpression + " and " + Constants.ATTRIBUTE_PRODUCT_REGION + " = " + FILTER_KEY_PRODUCT_REGION;
            } else {
                filterExpression = Constants.ATTRIBUTE_PRODUCT_REGION + " = " + FILTER_KEY_PRODUCT_REGION;
            }
        }

        return filterExpression;
    }

    private Map<String, AttributeValue> getExpressionAttributeValue(ProductQueryOptions options) {
        Map<String, AttributeValue> valueMap = new HashMap<String, AttributeValue>();

        if (options.getMainCategory().getIdentifier() != Category.NONE.getIdentifier()) {
            String mainCategoryValue = String.valueOf(options.getMainCategory().getIdentifier());
            valueMap.put(FILTER_KEY_MAIN_CATEGORY, new AttributeValue().withN(mainCategoryValue));
        }

        if (options.getSubCategory().getIdentifier() != Category.NONE.getIdentifier()) {
            String subCategoryValue = String.valueOf(options.getSubCategory().getIdentifier());
            valueMap.put(FILTER_KEY_SUB_CATEGORY, new AttributeValue().withN(subCategoryValue));
        }

        if (options.getProductRegion().getIdentifier() != Category.NONE.getIdentifier()) {
            String productRegionValue = String.valueOf(options.getProductRegion().getIdentifier());
            valueMap.put(FILTER_KEY_PRODUCT_REGION, new AttributeValue().withN(productRegionValue));
        }

        return valueMap;
    }

    public static String getProductImageName(int productId, int position) {
        return productId + "/" + Constants.PRODUCT_IMAGE_NAME_PREFIX + "_" + position + Constants.JPG_FILE_FORMAT;
    }
}
