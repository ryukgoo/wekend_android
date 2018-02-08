package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import com.daimajia.swipe.SwipeLayout;
import com.entuition.wekend.R;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.data.source.mail.observable.ReceiveMailObservable;
import com.entuition.wekend.data.source.mail.observable.SendMailObservable;
import com.entuition.wekend.util.Constants;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class MailBoxListViewModel extends AbstractViewModel {

    public static final String TAG = MailBoxListViewModel.class.getSimpleName();

    public final ObservableBoolean isLoading = new ObservableBoolean();
    public final ObservableBoolean isRefreshing = new ObservableBoolean();
    public final ObservableBoolean isNoData = new ObservableBoolean();
    public final ObservableField<String> noDataMessage = new ObservableField<>();

    public final ObservableArrayList<IMail> mailList = new ObservableArrayList<>();

    private final MailType mailType;
    private final MailDataSource mailDataSource;
    private final WeakReference<MailBoxNavigator> navigator;

    public MailBoxListViewModel(Context context, MailBoxNavigator navigator, MailType mailType,
                                @NonNull MailDataSource mailDataSource) {
        super(context);

        this.navigator = new WeakReference<MailBoxNavigator>(navigator);
        this.mailDataSource = mailDataSource;
        this.mailType = mailType;

        isLoading.set(false);
        isRefreshing.set(false);
        isNoData.set(false);

        switch (mailType) {
            case receive:
                noDataMessage.set(getApplication().getString(R.string.mailbox_receive_no_result));
                break;
            case send:
                noDataMessage.set(getApplication().getString(R.string.mailbox_send_no_result));
                break;
        }

    }

    @Override
    public void onCreate() {
        SendMailObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Log.d(TAG, "SendMailObservable > update");
                mailDataSource.refreshMailBoxes();
                loadMails();
            }
        });

        ReceiveMailObservable.getInstance().addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Log.d(TAG, "ReceiveMailObservable > update");
                mailDataSource.refreshMailBoxes();
                loadMails();
            }
        });

        isLoading.set(true);
    }

    @Override
    public void onResume() {
        loadMails();
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onRefresh() {
        isRefreshing.set(true);
        mailDataSource.refreshMailBoxes();
        loadMails();
    }

    public void onClickItem(IMail mail) {
        if (mail.getIsRead() == Constants.ReadState.unread.ordinal()) {
            mailDataSource.readMail(mail);
        }

        if (navigator.get() != null) {
            navigator.get().gotoMailProfileView(mail);
        }
    }

    public void onClickDeleteItem(IMail mail, final int position, final SwipeLayout layout) {
        Log.d(TAG, "onClickDeleteItem > position : " + position);
        mailDataSource.deleteMail(mail, new MailDataSource.DeleteMailCallback() {
            @Override
            public void onCompleteDeleteMail(IMail mail, int remain) {
                if (navigator.get() != null) {
                    navigator.get().onCompleteDeleteMail(position, layout);
                }
                if (remain == 0) isNoData.set(true);
            }

            @Override
            public void onFailedDeleteMail() {
                if (navigator.get() != null) {
                    navigator.get().onFailedDeleteMail();
                }
            }
        });
    }

    private void loadMails() {
        mailDataSource.loadMailList(new MailDataSource.LoadMailListCallback() {
            @Override
            public void onMailListLoaded(List<IMail> mails) {
                isLoading.set(false);
                isRefreshing.set(false);

                mailList.clear();
                mailList.addAll(mails);
                validateNoData();
            }

            @Override
            public void onDataNotAvailable() {
                isLoading.set(false);
                isRefreshing.set(false);
                validateNoData();
            }
        });
    }

    private void validateNoData() {
        if (mailList.size() == 0) {
            isNoData.set(true);
        } else {
            isNoData.set(false);
        }
    }
}
