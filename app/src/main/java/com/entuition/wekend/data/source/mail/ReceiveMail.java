package com.entuition.wekend.data.source.mail;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ryukgoo on 2016. 9. 6..
 */
@DynamoDBTable(tableName = ReceiveMail.RECEIVE_MAIL_TABLE)
public class ReceiveMail implements IMail {

    static final String RECEIVE_MAIL_TABLE = "receive_mail_db";

    public static class Index {
        public static final String USERID_RESPONSE_TIME = "UserId-ResponseTime-index";
    }

    public static class Attribute {
        public static final String USER_ID = "UserId";
        public static final String UPDATED_TIME = "UpdatedTime";
        public static final String RESPONSE_TIME = "ResponseTime";
        public static final String PRODUCT_ID = "ProductId";
        public static final String PRODUCT_TITLE = "ProductTitle";
        public static final String SENDER_ID = "SenderId";
        public static final String SENDER_NICKNAME = "SenderNickname";
        public static final String RECEIVER_NICKNAME = "ReceiverNickname";
        public static final String PROPOSE_STATUS = "ProposeStatus";
        public static final String MESSAGE = "Message";
        public static final String IS_READ = "IsRead";
    }

    private String userId;
    private String updatedTime;
    private String responseTime;
    private int productId;
    private String productTitle;
    private String senderId;
    private String senderNickname;
    private String receiverNickname;
    private String status;
    private String message;
    private int isRead; // 0:read, 1:unread

    @Override
    @DynamoDBIgnore
    public MailType getMailType() {
        return MailType.receive;
    }

    @Override
    @DynamoDBIgnore
    public String getHighlightColor() {
        return "#e2154a";
    }

    @DynamoDBHashKey(attributeName = Attribute.USER_ID)
    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.USERID_RESPONSE_TIME)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = Attribute.UPDATED_TIME)
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.RESPONSE_TIME)
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Index.USERID_RESPONSE_TIME)
    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.PRODUCT_ID)
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

    @DynamoDBAttribute(attributeName = Attribute.SENDER_ID)
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    @DynamoDBIgnore
    public String getFriendId() {
        return senderId;
    }

    @Override
    @DynamoDBIgnore
    public String getFriendNickname() {
        return senderNickname;
    }

    @DynamoDBAttribute(attributeName = Attribute.SENDER_NICKNAME)
    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    @DynamoDBAttribute(attributeName = Attribute.RECEIVER_NICKNAME)
    public String getReceiverNickname() {
        return receiverNickname;
    }

    public void setReceiverNickname(String receiverNickname) {
        this.receiverNickname = receiverNickname;
    }

    @DynamoDBAttribute(attributeName = Attribute.PROPOSE_STATUS)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = Attribute.MESSAGE)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDBAttribute(attributeName = Attribute.IS_READ)
    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
