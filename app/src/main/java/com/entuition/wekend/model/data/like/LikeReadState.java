package com.entuition.wekend.model.data.like;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2017. 6. 27..
 */

@DynamoDBTable(tableName = Constants.LIKE_READ_STATE_TALBE)
public class LikeReadState {

    private String likeId;
    private String userId;
    private int productId;
    private String likeUserId;
    private String readTime;

    @DynamoDBHashKey(attributeName = Constants.ATTRIBUTE_LIKE_READ_STATE_LIKE_ID)
    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    @DynamoDBRangeKey(attributeName = Constants.ATTRIBUTE_LIKE_READ_STATE_USERID)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Constants.INDEX_READSTATE_PRODUCTID_USERID)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_READ_STATE_PRODUCTID)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Constants.INDEX_READSTATE_PRODUCTID_USERID)
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_READ_STATE_LIKEUSERID)
    public String getLikeUserId() {
        return likeUserId;
    }

    public void setLikeUserId(String likeUserId) {
        this.likeUserId = likeUserId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_READ_STATE_READTIME)
    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }
}
