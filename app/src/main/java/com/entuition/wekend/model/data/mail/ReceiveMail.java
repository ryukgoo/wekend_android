package com.entuition.wekend.model.data.mail;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2016. 9. 6..
 */
@DynamoDBTable(tableName = Constants.RECEIVE_MAIL_TABLE)
public class ReceiveMail {

    private String userId;
    private String updatedTime;
    private String responseTime;
    private int productId;
    private String productTitle;
    private String senderId;
    private String senderNickname;
    private String receiverNickname;
    private String status;
    private int isRead; // 0:read, 1:unread

    @DynamoDBHashKey(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_USER_ID)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Constants.RECEIVE_MAIL_INDEX_USERID_RESPONSE_TIME)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_UPDATED_TIME)
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_RESPONSE_TIME)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Constants.RECEIVE_MAIL_INDEX_USERID_RESPONSE_TIME)
    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_PRODUCT_ID)
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_PRODUCT_TITLE)
    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_SENDER_ID)
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_SENDER_NICKNAME)
    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_RECEIVER_NICKNAME)
    public String getReceiverNickname() {
        return receiverNickname;
    }

    public void setReceiverNickname(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_STATUS)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_RECEIVE_MAIL_ISREAD)
    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
