package com.entuition.wekend.model.data.mail;

import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.entuition.wekend.controller.CognitoSyncClientManager;
import com.entuition.wekend.model.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public class ReceiveMailDaoImpl implements IReceiveMailDao {

    private final String TAG = getClass().getSimpleName();

    private static final String FILTER_KEY_STATUS = ":ProposeStatus";
    private static final String FILTER_KEY_SENDER_ID = ":SenderId";
    private static final String FILTER_KEY_PRODUCT_ID = ":ProductId";
    private static final String FILTER_KEY_ISREAD = ":IsRead";

    private static ReceiveMailDaoImpl sInstance = null;

    public static ReceiveMailDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (ReceiveMailDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new ReceiveMailDaoImpl();
                }
            }
        }
        return sInstance;
    }

    private DynamoDBMapper mapper;
    private List<ReceiveMail> receiveMailList;

    public ReceiveMailDaoImpl() {
        AmazonDynamoDBClient client = CognitoSyncClientManager.getDynamoDBClient();
        this.mapper = new DynamoDBMapper(client);

        receiveMailList = new ArrayList<ReceiveMail>();
    }

    public void clear() {
        receiveMailList = new ArrayList<ReceiveMail>();
    }

    @Override
    public List<ReceiveMail> getReceiveMailList() {
        return receiveMailList;
    }

    @Override
    public boolean loadReceiveMailList(String userId) {

        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(userId);

        String filterExpression = Constants.ATTRIBUTE_RECEIVE_MAIL_STATUS + " <> " + FILTER_KEY_STATUS;
        Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
        attributeValue.put(FILTER_KEY_STATUS, new AttributeValue().withS(Constants.PROPOSE_STATUS_DELETE));

        DynamoDBQueryExpression<ReceiveMail> queryExpression = new DynamoDBQueryExpression<ReceiveMail>()
                .withIndexName(Constants.RECEIVE_MAIL_INDEX_USERID_RESPONSE_TIME)
                .withHashKeyValues(mail)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(attributeValue)
                .withScanIndexForward(false)
                .withConsistentRead(false);

        try {
            PaginatedList<ReceiveMail> mails = mapper.query(ReceiveMail.class, queryExpression);

            receiveMailList = new ArrayList<ReceiveMail>();

            for (ReceiveMail item : mails) {
                receiveMailList.add(item);
            }

            return true;
        } catch (AmazonClientException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public ReceiveMail getReceiveMail(String userId, String senderId, int productId) {

        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(userId);

        String filterExpression = Constants.ATTRIBUTE_RECEIVE_MAIL_SENDER_ID + " = " + FILTER_KEY_SENDER_ID
                + " and " + Constants.ATTRIBUTE_RECEIVE_MAIL_PRODUCT_ID + " = " + FILTER_KEY_PRODUCT_ID;
        Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
        attributeValue.put(FILTER_KEY_SENDER_ID, new AttributeValue().withS(senderId));
        attributeValue.put(FILTER_KEY_PRODUCT_ID, new AttributeValue().withN(String.valueOf(productId)));

        DynamoDBQueryExpression<ReceiveMail> queryExpression = new DynamoDBQueryExpression<ReceiveMail>()
                .withHashKeyValues(mail)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(attributeValue)
                .withScanIndexForward(false)
                .withConsistentRead(false);

        try {
            PaginatedList<ReceiveMail> results = mapper.query(ReceiveMail.class, queryExpression);
            if (results != null) {
                return results.get(0);
            }
        } catch (AmazonClientException e) {
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    /**
     *
     * @param mail ReceiveMail
     * @return success return true, otherwise return false;
     */
    @Override
    public boolean updateReceiveMail(ReceiveMail mail) {
        try {
            mapper.save(mail);
            return true;
        } catch (AmazonClientException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteReceiveMail(ReceiveMail mail) {

        try {
            mapper.delete(mail);
            receiveMailList.remove(mail);
            return true;
        } catch (AmazonClientException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public int getNewMail(String userId) {

        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(userId);

        String filterExpression = Constants.ATTRIBUTE_RECEIVE_MAIL_ISREAD + " = " + FILTER_KEY_ISREAD;
        Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
        attributeValue.put(FILTER_KEY_ISREAD, new AttributeValue().withN(String.valueOf(Constants.MAIL_STATUS_UNREAD)));

        DynamoDBQueryExpression<ReceiveMail> queryExpression = new DynamoDBQueryExpression<ReceiveMail>()
                .withHashKeyValues(mail)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(attributeValue)
                .withConsistentRead(false);

        try {
            return mapper.count(ReceiveMail.class, queryExpression);
        } catch (AmazonClientException e) {
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return 0;
    }
}
