package com.entuition.wekend.data.source.noticeinfo;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public interface NoticeInfoDataSource {

    interface GetNoticeInfoCallback {
        void onNoticeInfoLoaded(List<NoticeInfo> list);
        void onDataNotAvailable();
    }

    void getNoticeInfoList(String type, @NonNull  GetNoticeInfoCallback callback);

}
