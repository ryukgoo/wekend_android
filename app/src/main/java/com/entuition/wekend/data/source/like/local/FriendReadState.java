package com.entuition.wekend.data.source.like.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.util.Utilities;

/**
 * Created by ryukgoo on 2017. 11. 27..
 */

@Entity(tableName = "friendReadStates")
public class FriendReadState {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "productId")
    private int productId;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "readTime")
    private String readTime;

    @Ignore
    public FriendReadState(LikeInfo likeInfo) {
        this.id = likeInfo.getLikeId();
        this.productId = likeInfo.getProductId();
        this.userId = likeInfo.getUserId();
        this.readTime = Utilities.getTimestamp();
    }

    public FriendReadState(@NonNull String id, int productId, String userId, String readTime) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.readTime = readTime;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public String getReadTime() {
        return readTime;
    }
}
