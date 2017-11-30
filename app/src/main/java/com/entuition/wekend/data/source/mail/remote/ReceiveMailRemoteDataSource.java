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
import com.entuition.wekend.data.source.mail.ReceiveMail;
import com.entuition.wekend.data.source.userinfo.UserInfoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public class ReceiveMailRemoteDataSource implements MailDataSource {

    public static final String TAG = ReceiveMailRemoteDataSource.class.getSimpleName();

    private static ReceiveMailRemoteDataSource INSTANCE = null;

    public static ReceiveMailRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ReceiveMailRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReceiveMailRemoteDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static final String FILTER_KEY_STATUS = ":ProposeStatus";
    private static final String FILTER_KEY_SENDER_ID = ":SenderId";
    private static final String FILTER_KEY_PRODUCT_ID = ":ProductId";
    private static final String FILTER_KEY_ISREAD = ":IsRead";

    private final Context context;
    private final DynamoDBMapper mapper;

    private ReceiveMailRemoteDataSource(Context context) {
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
        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(userId);
        mail.setSenderId(friendId);
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
        new UpdateMailTask(mapper, callback).execute((ReceiveMail) mail);
    }

    @Override
    public void deleteMail(IMail mail, @NonNull DeleteMailCallback callback) {
        new DeleteMailTask(mapper, callback).execute((ReceiveMail) mail);
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
        }).execute((ReceiveMail) mail);
    }

    private static class GetMailTask extends AsyncTask<ReceiveMail, Void, IMail> {

        private final DynamoDBMapper mapper;
        private final GetMailCallback callback;

        public GetMailTask(DynamoDBMapper mapper, GetMailCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected IMail doInBackground(ReceiveMail... receiveMails) {

            String userId = receiveMails[0].getUserId();
            String senderId = receiveMails[0].getSenderId();
            int productId = receiveMails[0].getProductId();

            ReceiveMail mail = new ReceiveMail();
            mail.setUserId(userId);

            String filterExpression = ReceiveMail.Attribute.SENDER_ID + " = " + FILTER_KEY_SENDER_ID
                    + " and " + ReceiveMail.Attribute.PRODUCT_ID + " = " + FILTER_KEY_PRODUCT_ID;
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
                PaginatedQueryList<ReceiveMail> result = mapper.query(ReceiveMail.class, queryExpression);
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

        private LoadMailListTask(DynamoDBMapper mapper, LoadMailListCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected List<IMail> doInBackground(String... strings) {

            ReceiveMail mail = new ReceiveMail();
            mail.setUserId(strings[0]);

            String filterExpression = ReceiveMail.Attribute.PROPOSE_STATUS + " <> " + FILTER_KEY_STATUS;
            Map<String, AttributeValue> attributeValue = new HashMap<String, AttributeValue>();
            attributeValue.put(FILTER_KEY_STATUS, new AttributeValue().withS(ProposeStatus.delete.toString()));

            DynamoDBQueryExpression<ReceiveMail> queryExpression = new DynamoDBQueryExpression<ReceiveMail>()
                    .withIndexName(ReceiveMail.Index.USERID_RESPONSE_TIME)
                    .withHashKeyValues(mail)
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeValues(attributeValue)
                    .withScanIndexForward(false)
                    .withConsistentRead(false);

            try {
                PaginatedQueryList<ReceiveMail> receiveMails = mapper.query(ReceiveMail.class, queryExpression);
                List<IMail> results = new ArrayList<>();
                results.addAll(receiveMails);
                return results;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
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

    private static class UpdateMailTask extends AsyncTask<ReceiveMail, Void, IMail> {

        private final DynamoDBMapper mapper;
        private final UpdateMailCallback callback;

        public UpdateMailTask(DynamoDBMapper mapper, UpdateMailCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected IMail doInBackground(ReceiveMail... receiveMails) {

            try {
                mapper.save(receiveMails[0]);
                return receiveMails[0];
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

    private static class DeleteMailTask extends AsyncTask<ReceiveMail, Void, IMail> {

        private final DynamoDBMapper mapper;
        private final DeleteMailCallback callback;

        public DeleteMailTask(DynamoDBMapper mapper, DeleteMailCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected IMail doInBackground(ReceiveMail... receiveMails) {

            try {
                mapper.delete(receiveMails[0]);
                return receiveMails[0];
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
