package com.entuition.wekend.data.source.mail;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public interface MailDataSource {

    interface GetMailCallback {
        void onMailLoaded(IMail mail);
        void onDataNotAvailable();
    }

    interface LoadMailListCallback {
        void onMailListLoaded(List<IMail> mails);
        void onDataNotAvailable();
    }

    interface UpdateMailCallback {
        void onCompleteUpdateMail(IMail mail);
        void onFailedUpdateMail();
    }

    interface DeleteMailCallback {
        void onCompleteDeleteMail(IMail mail, int remain);
        void onFailedDeleteMail();
    }

    void clear();

    void refreshMailBoxes();

    void getMail(String userId, String friendId, int productId, @NonNull GetMailCallback callback);

    void loadMailList(@NonNull LoadMailListCallback callback);

    void updateMail(IMail mail, @NonNull UpdateMailCallback callback);

    void deleteMail(IMail mail, @NonNull DeleteMailCallback callback);

    void readMail(IMail mail);
}
