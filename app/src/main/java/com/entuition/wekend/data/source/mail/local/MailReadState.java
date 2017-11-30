package com.entuition.wekend.data.source.mail.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.entuition.wekend.util.Utilities;

import java.util.UUID;

/**
 * Created by ryukgoo on 2017. 11. 29..
 */

@Entity(tableName = "mailReadStates")
public class MailReadState {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "productId")
    private int productId;

    @ColumnInfo(name = "readTime")
    private String readTime;

    @Ignore
    public MailReadState(String type, String userId, int productId) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.userId = userId;
        this.productId = productId;
        this.readTime = Utilities.getTimestamp();
    }

    public MailReadState(@NonNull String id, String type, String userId, int productId, String readTime) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.productId = productId;
        this.readTime = readTime;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    public String getReadTime() {
        return readTime;
    }
}
