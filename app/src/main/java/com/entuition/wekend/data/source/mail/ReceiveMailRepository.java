package com.entuition.wekend.data.source.mail;

import android.content.Context;
import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.mail.local.ReceiveMailLocalDataSource;
import com.entuition.wekend.data.source.mail.observable.ReceiveMailObservable;
import com.entuition.wekend.data.source.mail.remote.ReceiveMailRemoteDataSource;
import com.entuition.wekend.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public class ReceiveMailRepository implements MailDataSource {

    public static final String TAG = ReceiveMailRepository.class.getSimpleName();

    private static ReceiveMailRepository INSTANCE = null;

    public static ReceiveMailRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ReceiveMailRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReceiveMailRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private final ReceiveMailRemoteDataSource remoteDataSource;
    private ReceiveMailLocalDataSource localDataSource;

    private boolean isCacheDirty = false;

    List<IMail> cachedMails;

    private ReceiveMailRepository(Context context) {
        this.remoteDataSource = ReceiveMailRemoteDataSource.getInstance(context);
        this.localDataSource = ReceiveMailLocalDataSource.getInstance(context);
    }

    @Override
    public void clear() {
        clearCachedMails();
    }

    @Override
    public void refreshMailBoxes() {
        isCacheDirty = true;
    }

    @Override
    public void getMail(String userId, String friendId, int productId, @NonNull GetMailCallback callback) {
        remoteDataSource.getMail(userId, friendId, productId, callback);
    }

    @Override
    public void loadMailList(@NonNull final LoadMailListCallback callback) {
        if (cachedMails != null && !isCacheDirty) {
            callback.onMailListLoaded(cachedMails);
            return;
        }

        remoteDataSource.loadMailList(new LoadMailListCallback() {
            @Override
            public void onMailListLoaded(List<IMail> mails) {
                clearCachedMails();

                cachedMails.addAll(mails);
                callback.onMailListLoaded(cachedMails);
                isCacheDirty = false;
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void updateMail(IMail mail, @NonNull final UpdateMailCallback callback) {
        remoteDataSource.updateMail(mail, new UpdateMailCallback() {
            @Override
            public void onCompleteUpdateMail(IMail mail) {
                if (cachedMails == null) clearCachedMails();
                if (!cachedMails.contains(mail)) {
                    cachedMails.add(0, mail);
                }

                callback.onCompleteUpdateMail(mail);

                ReceiveMailObservable.getInstance().change();
            }

            @Override
            public void onFailedUpdateMail() {
                callback.onFailedUpdateMail();
            }
        });
    }

    @Override
    public void deleteMail(IMail mail, @NonNull final DeleteMailCallback callback) {
        remoteDataSource.deleteMail(mail, new DeleteMailCallback() {
            @Override
            public void onCompleteDeleteMail(IMail mail, int remain) {
                cachedMails.remove(mail);

                callback.onCompleteDeleteMail(mail, cachedMails.size());
            }

            @Override
            public void onFailedDeleteMail() {
                callback.onFailedDeleteMail();
            }
        });
    }

    @Override
    public void readMail(IMail mail) {
        mail.setIsRead(Constants.ReadState.read.ordinal());
        remoteDataSource.readMail(mail);
        localDataSource.readMail(mail);
    }

    private void clearCachedMails() {
        if (cachedMails == null) {
            cachedMails = new ArrayList<>();
        }
        cachedMails.clear();
    }
}
