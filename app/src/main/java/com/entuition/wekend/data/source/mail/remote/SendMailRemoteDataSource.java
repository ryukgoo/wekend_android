package com.entuition.wekend.data.source.mail.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.entuition.wekend.data.CognitoSyncClientManager;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.mail.ProposeStatus;
import com.entuition.wekend.data.source.mail.SendMail;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public class SendMailRemoteDataSource implements MailDataSource {

    public static final String TAG = SendMailRemoteDataSource.class.getSimpleName();

    private static SendMailRemoteDataSource INSTANCE = null;

    public static SendMailRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SendMailRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SendMailRemoteDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final String FILTER_KEY_STATUS = ":ProposeStatus";
    private static final String FILTER_KEY_RECEIVER_ID = ":ReceiverId";
    private static final String FILTER_KEY_PRODUCT_ID = ":ProductId";
    private static final String FILTER_KEY_ISREAD = ":IsRead";

    private final Context context;
    private final DynamoDBMapper mapper;

    private SendMailRemoteDataSource(Context context) {
        this.context = context;
        this.mapper = CognitoSyncClientManager.getDynamoDBMapper();
    }

    @Override
    public void clear() {

    }

    @Override
    public void refreshMailBoxes() {

    }

    @Override
    public void getMail(String userId, String friendId, int productId, @NonNull GetMailCallback callback) {
        SendMail mail = new SendMail();
        mail.setUserId(userId);
        mail.setReceiverId(friendId);
        mail.setProductId(productId);
        new GetMailTask(mapper, callback).execute(mail);
    }

    @Override
    public void loadMailList(@NonNull LoadMailListCallback callback) {
        String userId = UserInfoRepository.getInstance(context).getUserId();
        new LoadMailListTask(mapper, callback).execute(userId);
    }

    @Override
    public void updateMail(IMail mail, @NonNull UpdateMailCallback callback) {
        new UpdateMailTask(mapper, callback).execute((SendMail) mail);
    }

    @Override
    public void deleteMail(IMail mail, @NonNull DeleteMailCallback callback) {
        new DeleteMailTask(mapper, callback).execute((SendMail) mail);
    }

    @Override
    public void readMail(final IMail mail) {
        new UpdateMailTask(mapper, new UpdateMailCallback() {
            @Override
            public void onCompleteUpdateMail(IMail result) {
                Log.d(TAG, "onSuccessReadMail > mail : " + result.getProductTitle());
            }

            @Override
            public void onFailedUpdateMail() {
                Log.e(TAG, "onFailedReadMail > mail : " + mail.getProductTitle());
            }
        }).execute((SendMail) mail);
    }

    private static class GetMailTask extends AsyncTask<SendMail, Void, IMail> {

        private final DynamoDBMapper mapper;
        private final GetMailCallback callback;

        public GetMailTask(DynamoDBMapper mapper, GetMailCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected IMail doInBackground(SendMail... sendMails) {

            String userId = sendMails[0].getUserId();
            String receiverId = sendMails[0].getReceiverId();
            int productId = sendMails[0].getProductId();

            SendMail mail = new SendMail();
            mail.setUserId(userId);

            String filterExpression = SendMail.Attribute.RECEIVER_ID + " = " + FILTER_KEY_RECEIVER_ID
                    + " and " + SendMail.Attribute.PRODUCT_ID + " = " + FILTER_KEY_PRODUCT_ID;
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
                PaginatedQueryList<SendMail> result = mapper.query(SendMail.class, queryExpression);
                return result.get(0);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(IMail mail) {
            if (mail == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onMailLoaded(mail);
            }
        }
    }

    private static class LoadMailListTask extends AsyncTask<String, Void, List<IMail>> {

        private final DynamoDBMapper mapper;
        private final LoadMailListCallback callback;

        LoadMailListTask(DynamoDBMapper mapper, LoadMailListCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected List<IMail> doInBackground(String... strings) {

            SendMail mail = new SendMail();
            mail.setUserId(strings[0]);

            String filterExpression = SendMail.Attribute.PROPOSE_STATUS + " <> " + FILTER_KEY_STATUS;
            Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
            attributeValue.put(FILTER_KEY_STATUS, new AttributeValue().withS(ProposeStatus.delete.toString()));

            DynamoDBQueryExpression<SendMail> queryExpression = new DynamoDBQueryExpression<SendMail>()
                    .withIndexName(SendMail.Index.USERID_RESPONSETIME)
                    .withHashKeyValues(mail)
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeValues(attributeValue)
                    .withScanIndexForward(false)
                    .withConsistentRead(false);

            try {
                PaginatedQueryList<SendMail> sendMails = mapper.query(SendMail.class, queryExpression);
                return new ArrayList<IMail>(sendMails);
            } catch (Exception e) {
                Log.e(TAG, "LoadMailListTask > " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<IMail> iMails) {
            if (iMails == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onMailListLoaded(iMails);
            }
        }
    }

    private static class UpdateMailTask extends AsyncTask<SendMail, Void, IMail> {

        private final DynamoDBMapper mapper;
        private final UpdateMailCallback callback;

        UpdateMailTask(DynamoDBMapper mapper, UpdateMailCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected IMail doInBackground(SendMail... sendMails) {

            try {
                mapper.save(sendMails[0]);
                return sendMails[0];
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(IMail mail) {
            if (mail == null) {
                callback.onFailedUpdateMail();
            } else {
                callback.onCompleteUpdateMail(mail);
            }
        }
    }

    private static class DeleteMailTask extends AsyncTask<SendMail, Void, IMail> {

        private final DynamoDBMapper mapper;
        private final DeleteMailCallback callback;

        DeleteMailTask(DynamoDBMapper mapper, DeleteMailCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected IMail doInBackground(SendMail... sendMails) {

            try {
                mapper.delete(sendMails[0]);
                return sendMails[0];
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(IMail mail) {
            if (mail == null) {
                callback.onFailedDeleteMail();
            } else {
                callback.onCompleteDeleteMail(mail, 0);
            }
        }
    }
}
