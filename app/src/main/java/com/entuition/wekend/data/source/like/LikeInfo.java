package com.entuition.wekend.data.source.like;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ryukgoo on 2016. 4. 8..
 */
@DynamoDBTable(tableName = LikeInfo.LIKE_TABLE)
public class LikeInfo {

    static final String LIKE_TABLE = "picnic_like_db";

    public static class Index {
        public static final String PRODUCTID_UPDATEDTIME = "ProductId-UpdatedTime-index";
        public static final String USERID_UPDATEDTIME = "UserId-UpdatedTime-index";
    }

    public static class Attribute {
        public static final String USER_ID = "UserId";
        public static final String GENDER = "Gender";
        public static final String NICKNAME = "Nickname";
        public static final String PRODUCT_ID = "ProductId";
        public static final String PRODUCT_TITLE = "ProductTitle";
        public static final String PRODUCT_DESC = "ProductDesc";
        public static final String UPDATED_TIME = "UpdatedTime";
        public static final String READ_TIME = "ReadTime";
        public static final String LIKE_ID = "LikeId";
    }

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

    @DynamoDBHashKey(attributeName = Attribute.USER_ID)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.USERID_UPDATEDTIME)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBAttribute(attributeName = Attribute.NICKNAME)
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @DynamoDBAttribute(attributeName = Attribute.GENDER)
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.PRODUCTID_UPDATEDTIME)
    @DynamoDBRangeKey(attributeName = Attribute.PRODUCT_ID)
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @DynamoDBAttribute(attributeName = Attribute.PRODUCT_TITLE)
    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    @DynamoDBAttribute(attributeName = Attribute.PRODUCT_DESC)
    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    @DynamoDBAttribute(attributeName = Attribute.UPDATED_TIME)
    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = {Index.PRODUCTID_UPDATEDTIME, Index.USERID_UPDATEDTIME})
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.READ_TIME)
    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.LIKE_ID)
    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    @DynamoDBIgnore
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @DynamoDBIgnore
    public String getProductLikedTime() {
        return productLikedTime;
    }

    public void setProductLikedTime(String productLikedTime) {
        this.productLikedTime = productLikedTime;
    }
}
