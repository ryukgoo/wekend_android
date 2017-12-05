package com.entuition.wekend.data.source.mail;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public interface IMail {

    MailType getMailType();
    String getHighlightColor();
    String getUserId();
    void setUserId(String userId);
    String getUpdatedTime();
    void setUpdatedTime(String updatedTime);
    String getResponseTime();
    void setResponseTime(String responseTime);
    int getProductId();
    void setProductId(int productId);
    String getProductTitle();
    void setProductTitle(String productTitle);
    String getFriendId();
    String getFriendNickname();
    String getSenderNickname();
    void setSenderNickname(String senderNickname);
    String getReceiverNickname();
    void setReceiverNickname(String receiverNickname);
    String getStatus();
    void setStatus(String status);
    String getMessage();
    void setMessage(String message);
    int getIsRead();
    void setIsRead(int isRead);

}
