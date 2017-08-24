package com.entuition.wekend.model.data.like;

import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.DateUtils;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductReadState;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ryukgoo on 2016. 4. 9..
 */
public class LikeDBDaoImpl implements ILikeDBDao {

    private static final String TAG = "LikeDBDaoImpl";

    private static final String FILTER_KEY_LIKE_GENDER = ":gender";

    private static LikeDBDaoImpl sInstance;

    public static LikeDBDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (LikeDBDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new LikeDBDaoImpl();
                }
            }
        }
        return sInstance;
    }

    private DynamoDBMapper mapper;
    private List<LikeDBItem> likeList;
//    private PaginatedList<LikeReadState> readStates;
    private List<LikeReadState> readStates;

    private LikeDBDaoImpl() {

        AmazonDynamoDBClient dynamoDBClient = CognitoSyncClientManager.getDynamoDBClient();
        mapper = new DynamoDBMapper(dynamoDBClient);
        likeList = new ArrayList<LikeDBItem>();
    }

    public void clear() {
        likeList = new ArrayList<LikeDBItem>();
        readStates = null;
    }

    public List<LikeDBItem> getList() {
        return likeList;
    }

    @Override
    public LikeDBItem getLikeItem(String userId, int productId) {
        try {
            return mapper.load(LikeDBItem.class, userId, productId);
        } catch (Exception e) {
            Log.e(TAG, "getLikeItem > " + e.getMessage());
            return null;
        }
    }

    @Override
    public void addLike(String userId, int productId, String nickname, String gender, String productTitle, String productDesc) throws Exception {

        try {
            LikeDBItem likeItem = new LikeDBItem();
            likeItem.setUserId(userId);
            likeItem.setProductId(productId);
            likeItem.setNickname(nickname);
            likeItem.setGender(gender);
            likeItem.setProductTitle(productTitle);
            likeItem.setProductDesc(productDesc);
            likeItem.setUpdatedTime(Utilities.getTimestamp());
            likeItem.setLikeId(UUID.randomUUID().toString());

            mapper.save(likeItem);
            likeList.add(0, likeItem);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteLike(LikeDBItem likeDBItem) throws Exception {
        try {
            mapper.delete(likeDBItem);
//            likeList.remove(likeDBItem);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<LikeDBItem> getLikedProductList(String userId) {
        if (likeList != null && likeList.size() > 0) return likeList;

        loadLikeProductList(userId);
        return likeList;
    }

    @Override
    public List<LikeDBItem> getLikedFriendList(int productId, String gender) {

        LikeDBItem likeDBItem = new LikeDBItem();
        likeDBItem.setProductId(productId);

        String filterExpression = Constants.ATTRIBUTE_LIKE_GENDER + " <> " + FILTER_KEY_LIKE_GENDER;
        Map<String, AttributeValue> filterKeyMap = new HashMap<String, AttributeValue>();
        filterKeyMap.put(FILTER_KEY_LIKE_GENDER, new AttributeValue().withS(gender));

        try {
            DynamoDBQueryExpression<LikeDBItem> queryExpression = new DynamoDBQueryExpression<LikeDBItem>()
                    .withIndexName(Constants.INDEX_LIKE_PRODUCTID_UPDATEDTIME)
                    .withHashKeyValues(likeDBItem)
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeValues(filterKeyMap)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            PaginatedList<LikeDBItem> results = mapper.query(LikeDBItem.class, queryExpression);
            List<LikeDBItem> friendsList = new ArrayList<LikeDBItem>();
            for (LikeDBItem item : results) {
                friendsList.add(item);
            }

            return friendsList;

        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.getMessage());
        }

        return null;
    }

    @Override
    public int getLikedFriendCount(int productId, String gender) {
        LikeDBItem likeDBItem = new LikeDBItem();
        likeDBItem.setProductId(productId);

        String filterExpression = Constants.ATTRIBUTE_LIKE_GENDER + " <> " + FILTER_KEY_LIKE_GENDER;
        Map<String, AttributeValue> filterKeyMap = new HashMap<String, AttributeValue>();
        filterKeyMap.put(FILTER_KEY_LIKE_GENDER, new AttributeValue().withS(gender));

        try {
            DynamoDBQueryExpression<LikeDBItem> queryExpression = new DynamoDBQueryExpression<LikeDBItem>()
                    .withIndexName(Constants.INDEX_LIKE_PRODUCTID_UPDATEDTIME)
                    .withHashKeyValues(likeDBItem)
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeValues(filterKeyMap)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            return mapper.count(LikeDBItem.class, queryExpression);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return 0;
    }

    @Override
    public int getLikedTotalCount(int productId) {
        LikeDBItem likeDBItem = new LikeDBItem();
        likeDBItem.setProductId(productId);

        try {
            DynamoDBQueryExpression<LikeDBItem> queryExpression = new DynamoDBQueryExpression<LikeDBItem>()
                    .withIndexName(Constants.INDEX_LIKE_PRODUCTID_UPDATEDTIME)
                    .withHashKeyValues(likeDBItem)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            int count = mapper.count(LikeDBItem.class, queryExpression);

            return count * Constants.LIKE_COUNT_DELIMETER + productId;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return 0;
    }

    @Override
    public void clearLikeData() {
        likeList = new ArrayList<LikeDBItem>();
    }

    @Override
    public void loadLikeProductList(String userId) {

        Log.d(TAG, "LikeListLoad > loadLikeProductList start");
        Log.d(TAG, "loadLikeProductList > userId : " + userId);

        if (likeList != null) {
            likeList = null;
        }

        LikeDBItem likeDBItem = new LikeDBItem();
        likeDBItem.setUserId(userId);

        try {
            DynamoDBQueryExpression<LikeDBItem> queryExpression = new DynamoDBQueryExpression<LikeDBItem>()
                    .withIndexName(Constants.INDEX_LIKE_USERID_UPDATEDTIME)
                    .withHashKeyValues(likeDBItem)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            PaginatedList<LikeDBItem> results = mapper.query(LikeDBItem.class, queryExpression);
            likeList = new ArrayList<LikeDBItem>();
            for (LikeDBItem item : results) {
                ProductReadState productReadState = ProductDaoImpl.getInstance().getLikeStates().get(item.getProductId());

                if (productReadState != null) {
                    if (item.getGender().equals(Constants.VALUE_GENDER_MALE) &&
                            productReadState.getFemaleLikeTime() != null) {
                        item.setProductLikedTime(productReadState.getFemaleLikeTime());
                    } else if (item.getGender().equals(Constants.VALUE_GENDER_FEMALE) &&
                            productReadState.getMaleLikeTime() != null) {
                        item.setProductLikedTime(productReadState.getMaleLikeTime());
                    }
                }

                likeList.add(item);
            }

            Collections.sort(likeList, comparator);

        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
        }

        Log.d(TAG, "LikeListLoad > loadLikeProductList finish");
    }

    @Override
    public List<LikeDBItem> getFriendSubList(int page) {
        //
        return null;
    }

    public int getPositionByProductId(int productId) {
        for (int i = 0 ; i < likeList.size() ; i ++) {
            LikeDBItem likeItem = likeList.get(i);
            if (likeItem.getProductId() == productId) {
                return i;
            }
        }
        return -1;
    }

    public LikeDBItem getItemByProductId(int productId) {
        int position = getPositionByProductId(productId);
        if (position >= 0) return likeList.get(position);
        else return null;
    }

    public boolean hasLikeProduct(int productId) {
        if (likeList == null) return false;
        for (LikeDBItem item : likeList) {
            if (item.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }

    public boolean isReadLikeItem(LikeDBItem item) {
        ProductReadState likeState = ProductDaoImpl.getInstance().getLikeStates().get(item.getProductId());
        if (likeState == null) {
            return false;
        } else {
            if (item.getReadTime() != null) {
                Date readTime = DateUtils.parseISO8601Date(item.getReadTime());
                if (item.getGender().equals("male")) {
                    if (likeState.getFemaleLikeTime() != null) {
                        Date likeTime = DateUtils.parseISO8601Date(likeState.getFemaleLikeTime());
                        return (readTime.getTime() > likeTime.getTime());
                    }
                    return true;
                } else {
                    if (likeState.getMaleLikeTime() != null) {
                        Date likeTime = DateUtils.parseISO8601Date(likeState.getMaleLikeTime());
                        return (readTime.getTime() > likeTime.getTime());
                    }
                    return true;
                }
            } else {
                if (item.getGender().equals("male")) {
                    if (likeState.getFemaleLikeTime() != null) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    if (likeState.getMaleLikeTime() != null) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    public void updateLikeReadState(LikeDBItem item) {
        item.setReadTime(Utilities.getTimestamp());
        mapper.save(item);
    }

    public void comeNewLikeNotification(int productId) {

        LikeDBItem newComeItem = getItemByProductId(productId);
        int removeIndex = likeList.indexOf(newComeItem);
        likeList.remove(removeIndex);
        newComeItem.setReadTime(null);
        likeList.add(0, newComeItem);

        AddLikeObservable.getInstance().addLike(productId);
    }

    /**
     * update Like Read State
     */
    public void updateProfileReadState(String userId, int productId, String likeUserId) {

        LikeReadState readState = new LikeReadState();
        readState.setUserId(userId);
        readState.setProductId(productId);
        readState.setLikeUserId(likeUserId);
        readState.setReadTime(Utilities.getTimestamp());

        try {
            mapper.save(readState);
        } catch (Exception e) {

        }

    }

    public void updateProfileReadState(LikeReadState readState) {
        try {

            if (!readStates.contains(readState)) {
                readState.setReadTime(Utilities.getTimestamp());
                readStates.add(readState);
            } else {
                readState.setReadTime(Utilities.getTimestamp());
            }

            mapper.save(readState);

        } catch (Exception e) {
        }
    }

    public List<LikeReadState> getLikeReadState(int productId, String userId) {

        LikeReadState readState = new LikeReadState();
        readState.setProductId(productId);
        readState.setUserId(userId);

        try {

            DynamoDBQueryExpression<LikeReadState> queryExpression = new DynamoDBQueryExpression<LikeReadState>()
                    .withIndexName(Constants.INDEX_READSTATE_PRODUCTID_USERID)
                    .withHashKeyValues(readState)
                    .withConsistentRead(false)
                    .withScanIndexForward(false);

            PaginatedList<LikeReadState> results = mapper.query(LikeReadState.class, queryExpression);
            readStates = new ArrayList<LikeReadState>();
            for (LikeReadState state : results) {
                readStates.add(state);
            }
            return readStates;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    private final static Comparator<LikeDBItem> comparator = new Comparator<LikeDBItem>() {
        @Override
        public int compare(LikeDBItem o1, LikeDBItem o2) {

            final Collator collator = Collator.getInstance();

            String o1Time = o1.getProductLikedTime();
            String o2Time = o2.getProductLikedTime();

            return collator.compare(o2Time, o1Time);
        }
    };
}
