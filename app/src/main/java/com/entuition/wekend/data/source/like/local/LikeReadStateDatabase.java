package com.entuition.wekend.data.source.like.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by ryukgoo on 2017. 11. 27..
 */

@Database(entities = {LikeReadState.class}, version = 1, exportSchema = false)
public abstract class LikeReadStateDatabase extends RoomDatabase {

    private static LikeReadStateDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static LikeReadStateDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        LikeReadStateDatabase.class, "LikeReadStates.db").build();
            }
        }
        return INSTANCE;
    }

    public abstract LikeReadStateDao readStateDao();
}
