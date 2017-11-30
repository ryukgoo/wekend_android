package com.entuition.wekend.data.source.noticeinfo.remote;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.entuition.wekend.data.CognitoSyncClientManager;
import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.data.source.noticeinfo.NoticeInfoDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class NoticeInfoRemoteDataSource implements NoticeInfoDataSource {

    private static final String TAG = NoticeInfoRemoteDataSource.class.getSimpleName();

    private static NoticeInfoRemoteDataSource INSTANCE = null;

    public static NoticeInfoRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoticeInfoRemoteDataSource();
        }
        return INSTANCE;
    }

    private DynamoDBMapper mapper;

    private NoticeInfoRemoteDataSource() {
        AmazonDynamoDBClient client = CognitoSyncClientManager.getDynamoDBClient();
        mapper = new DynamoDBMapper(client);
    }

    @Override
    public void getNoticeInfoList(String type, @NonNull GetNoticeInfoCallback callback) {
        Log.d(TAG, "getNoticeInfoList > type : " + type);
        new GetNoticeInfoTask(mapper, callback).execute(type);
    }

    private static class GetNoticeInfoTask extends AsyncTask<String, Void, Void> {

        private final DynamoDBMapper mapper;
        private final GetNoticeInfoCallback callback;
        private List<NoticeInfo> noticeInfos = null;

        GetNoticeInfoTask(DynamoDBMapper mapper, GetNoticeInfoCallback callback) {
            this.mapper = mapper;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... params) {

            NoticeInfo noticeInfo = new NoticeInfo();
            noticeInfo.setType(params[0]);

            DynamoDBQueryExpression<NoticeInfo> queryExpression = new DynamoDBQueryExpression<NoticeInfo>()
                    .withIndexName(NoticeInfo.Index.TYPE_TIME)
                    .withHashKeyValues(noticeInfo)
                    .withConsistentRead(false)
                    .withScanIndexForward(true);

            try {
                PaginatedList<NoticeInfo> result = mapper.query(NoticeInfo.class, queryExpression);
                noticeInfos = new ArrayList<>(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (noticeInfos == null) {
                callback.onDataNotAvailable();
            } else {
                callback.onNoticeInfoLoaded(noticeInfos);
            }
        }
    }
}
