package com.entuition.wekend.data.source.like.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 27..
 */

@Dao
public interface FriendReadStateDao {

    @Query("SELECT * FROM friendReadStates")
    List<FriendReadState> getReadStates();

    @Query("SELECT * FROM friendReadStates WHERE productId = :productId and userId = :userId")
    FriendReadState getReadState(int productId, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateReadState(FriendReadState readState);

    @Query("DELETE FROM friendReadStates WHERE productId = :productId")
    int deleteReadState(int productId);

}
