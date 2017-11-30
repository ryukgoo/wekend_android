package com.entuition.wekend.data.source.like.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by ryukgoo on 2017. 11. 27..
 */

@Entity(tableName = "likeReadStates")
public class LikeReadState {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "productId")
    private int productId;

    @ColumnInfo(name = "readTime")
    private String readTime;

    public LikeReadState(@NonNull int productId, String readTime) {
        this.productId = productId;
        this.readTime = readTime;
    }

    @NonNull
    public int getProductId() {
        return productId;
    }

    public String getReadTime() {
        return readTime;
    }
}
