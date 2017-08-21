package com.entuition.wekend.model.data.like;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2016. 4. 8..
 */
@DynamoDBTable(tableName = Constants.LIKE_TABLE)
public class LikeDBItem {

    private String userId;
    private String nickname;
    private String gender;
    private int productId;
    private String productTitle;
    private String productDesc;
    private String updatedTime;
    private String readTime;
    private String likeId;
    private boolean isRead;

    private String productLikedTime;

    @DynamoDBHashKey(attributeName = Constants.ATTRIBUTE_LIKE_USER_ID)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Constants.INDEX_LIKE_USERID_UPDATEDTIME)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_NICKNAME)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_GENDER)
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Constants.INDEX_LIKE_PRODUCTID_UPDATEDTIME)
    @DynamoDBRangeKey(attributeName = Constants.ATTRIBUTE_LIKE_PRODUCT_ID)
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_PRODUCT_TITLE)
    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_PRODUCT_DESC)
    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_UPDATED_TIME)
    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = {Constants.INDEX_LIKE_PRODUCTID_UPDATEDTIME, Constants.INDEX_LIKE_USERID_UPDATEDTIME})
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_READ_TIME)
    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKE_ID)
    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getProductLikedTime() {
        return productLikedTime;
    }

    public void setProductLikedTime(String productLikedTime) {
        this.productLikedTime = productLikedTime;
    }
}
