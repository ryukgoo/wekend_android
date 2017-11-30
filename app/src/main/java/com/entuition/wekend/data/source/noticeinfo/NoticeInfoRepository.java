package com.entuition.wekend.data.source.noticeinfo;

import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.noticeinfo.remote.NoticeInfoRemoteDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class NoticeInfoRepository implements NoticeInfoDataSource {

    private static final String TAG = NoticeInfoRepository.class.getSimpleName();

    private static NoticeInfoRepository INSTANCE = null;

    private final NoticeInfoRemoteDataSource noticeInfoRemoteDataSource;

    public static NoticeInfoRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (NoticeInfoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NoticeInfoRepository();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    Map<String, NoticeInfo> cachedNoticeInfos;
    private boolean isCacheDirty = false;

    private NoticeInfoRepository() {
        this.noticeInfoRemoteDataSource = NoticeInfoRemoteDataSource.getInstance();
    }

    @Override
    public void getNoticeInfoList(String type, @NonNull GetNoticeInfoCallback callback) {

        if (cachedNoticeInfos != null && !isCacheDirty) {
            callback.onNoticeInfoLoaded(new ArrayList<NoticeInfo>(cachedNoticeInfos.values()));
            return;
        }

        getNoticeInfoFromRemoteDataSource(type, callback);
        /*
        if (isCacheDirty) {
            getNoticeInfoFromRemoteDataSource(type, callback);
        } else {
            // getNoticeInfos from LocalDataSource
            callback.onNoticeInfoLoaded(new ArrayList<NoticeInfo>(cachedNoticeInfos.values()));
        }
        */
    }

    private void getNoticeInfoFromRemoteDataSource(String type, @NonNull final GetNoticeInfoCallback callback) {
        noticeInfoRemoteDataSource.getNoticeInfoList(type, new GetNoticeInfoCallback() {
            @Override
            public void onNoticeInfoLoaded(List<NoticeInfo> list) {
                refreshCache(list);
                callback.onNoticeInfoLoaded(new ArrayList<NoticeInfo>(cachedNoticeInfos.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<NoticeInfo> noticeInfos) {
        if (cachedNoticeInfos == null) {
            cachedNoticeInfos = new LinkedHashMap<>();
        }
        cachedNoticeInfos.clear();

        for (NoticeInfo noticeInfo : noticeInfos) {
            cachedNoticeInfos.put(noticeInfo.getId(), noticeInfo);
        }
        isCacheDirty = false;
    }
}
