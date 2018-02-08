package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.util.Log;

import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.data.source.noticeinfo.NoticeInfoDataSource;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 10. 25..
 */

public class SettingNoticeViewModel extends AbstractViewModel implements SettingNoticeItemListener {

    public static final String TAG = SettingNoticeViewModel.class.getSimpleName();

    private final NoticeInfoDataSource noticeInfoDataSource;
    private final WeakReference<SettingNoticeNavigator> navigator;

    public final ObservableArrayList<NoticeInfo> items = new ObservableArrayList<>();

    public SettingNoticeViewModel(Context context, SettingNoticeNavigator navigator, NoticeInfoDataSource noticeInfoDataSource) {
        super(context);
        this.navigator = new WeakReference<SettingNoticeNavigator>(navigator);
        this.noticeInfoDataSource = noticeInfoDataSource;
    }

    @Override
    public void onCreate() {
        noticeInfoDataSource.getNoticeInfoList("Help", new NoticeInfoDataSource.GetNoticeInfoCallback() {
            @Override
            public void onNoticeInfoLoaded(List<NoticeInfo> list) {
                Log.d(TAG, "onNoticeInfoLoaded > list : " + list.size());
                items.addAll(list);
            }

            @Override
            public void onDataNotAvailable() {}
        });
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onClickNoticeItem(NoticeInfo info) {
        if (navigator.get() != null) navigator.get().showDetail(info);
    }
}
