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
public interface LikeReadStateDao {

    @Query("SELECT * FROM likeReadStates")
    List<LikeReadState> getReadStates();

    @Query("SELECT * FROM likeReadStates WHERE productId = :productId")
    LikeReadState getReadStateByProductId(int productId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateReadState(LikeReadState readState);

    @Query("DELETE FROM likeReadStates WHERE productId = :productId")
    int deleteReadState(int productId);
}
