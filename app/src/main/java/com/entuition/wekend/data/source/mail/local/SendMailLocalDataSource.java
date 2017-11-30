package com.entuition.wekend.data.source.mail.local;

import android.content.Context;
import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.data.source.mail.MailDataSource;
import com.entuition.wekend.data.source.mail.MailType;
import com.entuition.wekend.util.AppExecutors;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public class SendMailLocalDataSource implements MailDataSource {

    public static final String TAG = SendMailLocalDataSource.class.getSimpleName();

    private static SendMailLocalDataSource INSTANCE = null;

    public static SendMailLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SendMailLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SendMailLocalDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private final MailReadStateDao readStateDao;
    private AppExecutors appExecutors;

    private SendMailLocalDataSource(Context context) {
        this.readStateDao = MailReadStateDatabase.getInstance(context).readStateDao();
        this.appExecutors = new AppExecutors();
    }

    @Override
    public void clear() {

    }

    @Override
    public void refreshMailBoxes() {

    }

    @Override
    public void getMail(String userId, String friendId, int productId, @NonNull GetMailCallback callback) {

    }

    @Override
    public void loadMailList(@NonNull LoadMailListCallback callback) {

    }

    @Override
    public void updateMail(IMail mail, @NonNull UpdateMailCallback callback) {

    }

    @Override
    public void deleteMail(IMail mail, @NonNull DeleteMailCallback callback) {

    }

    @Override
    public void readMail(IMail mail) {
        final MailReadState readState = new MailReadState(MailType.send.toString(), mail.getFriendId(), mail.getProductId());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                readStateDao.updateReadState(readState);
            }
        };
        appExecutors.diskIO().execute(runnable);
    }
}
