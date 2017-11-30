package com.entuition.wekend.data.source.mail.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by ryukgoo on 2017. 11. 29..
 */

@Database(entities = {MailReadState.class}, version = 1, exportSchema = false)
public abstract class MailReadStateDatabase extends RoomDatabase {

    private static MailReadStateDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static MailReadStateDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MailReadStateDatabase.class, "MailReadStates.db").build();
            }
        }
        return INSTANCE;
    }

    public abstract MailReadStateDao readStateDao();

}
