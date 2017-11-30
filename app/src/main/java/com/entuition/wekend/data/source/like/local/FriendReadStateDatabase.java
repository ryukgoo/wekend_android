package com.entuition.wekend.data.source.like.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by ryukgoo on 2017. 11. 27..
 */

@Database(entities = {FriendReadState.class}, version = 1, exportSchema = false)
public abstract class FriendReadStateDatabase extends RoomDatabase {

    private static FriendReadStateDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static FriendReadStateDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        FriendReadStateDatabase.class, "FriendReadStates.db").build();
            }
        }
        return INSTANCE;
    }

    public abstract FriendReadStateDao readStateDao();

}
