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
 * Created by ryukgoo on 2016. 9. 6..
 */
public class SendMailDaoImpl implements ISendMailDao {

    private final String TAG = getClass().getSimpleName();

    private static final String FILTER_KEY_STATUS = ":ProposeStatus";
    private static final String FILTER_KEY_RECEIVER_ID = ":ReceiverId";
    private static final String FILTER_KEY_PRODUCT_ID = ":ProductId";
    private static final String FILTER_KEY_ISREAD = ":IsRead";

    private static SendMailDaoImpl sInstance = null;

    public static SendMailDaoImpl getInstance() {
        if (sInstance == null) {
            synchronized (SendMailDaoImpl.class) {
                if (sInstance == null) {
                    sInstance = new SendMailDaoImpl();
                }
            }
        }

        return sInstance;
    }

    private DynamoDBMapper mapper;
    private List<SendMail> sendMailList;

    public SendMailDaoImpl() {
        AmazonDynamoDBClient client = CognitoSyncClientManager.getDynamoDBClient();
        mapper = new DynamoDBMapper(client);

        sendMailList = new ArrayList<SendMail>();
    }

    public void clear() {
        sendMailList = new ArrayList<SendMail>();
    }

    @Override
    public List<SendMail> getSendMailList() {
        return sendMailList;
    }

    @Override
    public boolean loadSendMailList(String userId) {

        SendMail sendMail = new SendMail();
        sendMail.setUserId(userId);

        String filterExpression = Constants.ATTRIBUTE_SEND_MAIL_STATUS + " <> " + FILTER_KEY_STATUS;
        Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
        attributeValue.put(FILTER_KEY_STATUS, new AttributeValue().withS(Constants.PROPOSE_STATUS_DELETE));

        DynamoDBQueryExpression<SendMail> queryExpression = new DynamoDBQueryExpression<SendMail>()
                .withIndexName(Constants.SEND_MAIL_INDEX_USERID_RESPONSETIME)
                .withHashKeyValues(sendMail)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(attributeValue)
                .withScanIndexForward(false)
                .withConsistentRead(false);

        try {
            PaginatedList<SendMail> mails = mapper.query(SendMail.class, queryExpression);
            sendMailList = new ArrayList<SendMail>();

            for (SendMail item : mails) {
                sendMailList.add(item);
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
    public SendMail getSendMail(String userId, String receiverId, int productId) {

        SendMail mail = new SendMail();
        mail.setUserId(userId);

        String filterExpression = Constants.ATTRIBUTE_SEND_MAIL_RECEIVER_ID + " = " + FILTER_KEY_RECEIVER_ID
                + " and " + Constants.ATTRIBUTE_SEND_MAIL_PRODUCT_ID + " = " + FILTER_KEY_PRODUCT_ID;
        Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
        attributeValue.put(FILTER_KEY_RECEIVER_ID, new AttributeValue().withS(receiverId));
        attributeValue.put(FILTER_KEY_PRODUCT_ID, new AttributeValue().withN(String.valueOf(productId)));

        DynamoDBQueryExpression<SendMail> queryExpression = new DynamoDBQueryExpression<SendMail>()
                .withHashKeyValues(mail)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(attributeValue)
                .withScanIndexForward(false)
                .withConsistentRead(false);

        try {
            PaginatedList<SendMail> results = mapper.query(SendMail.class, queryExpression);
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

    @Override
    public boolean updateSendMail(SendMail sendMail) {
        try {
            mapper.save(sendMail);
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
    public boolean deleteSendMail(SendMail mail) {

        try {
            mapper.delete(mail);
            sendMailList.remove(mail);
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

        SendMail mail = new SendMail();
        mail.setUserId(userId);

        String filterExpression = Constants.ATTRIBUTE_SEND_MAIL_ISREAD + " = " + FILTER_KEY_ISREAD;
        Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
        attributeValue.put(FILTER_KEY_ISREAD, new AttributeValue().withN(String.valueOf(Constants.MAIL_STATUS_UNREAD)));

        DynamoDBQueryExpression<SendMail> queryExpression = new DynamoDBQueryExpression<SendMail>()
                .withHashKeyValues(mail)
                .withFilterExpression(filterExpression)
                .withExpressionAttributeValues(attributeValue)
                .withConsistentRead(false);

        try {
            return mapper.count(SendMail.class, queryExpression);
        } catch (AmazonClientException e) {
            Log.e(TAG, e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return 0;
    }
}
