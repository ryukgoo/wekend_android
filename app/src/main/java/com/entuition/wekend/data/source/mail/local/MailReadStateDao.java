package com.entuition.wekend.data.source.mail.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 29..
 */

@Dao
public interface MailReadStateDao {

    @Query("SELECT * FROM mailReadStates WHERE type = :type")
    List<MailReadState> getReadStates(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateReadState(MailReadState readState);

    @Query("DELETE FROM mailReadStates WHERE type = :type and userId = :userId and productId = :productId")
    int deleteReadState(String type, String userId, int productId);
}
