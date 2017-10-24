package com.entuition.wekend.model.data.user;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2017. 8. 3..
 */

@DynamoDBTable(tableName = Constants.NOTICE_INTO_DB)
public class NoticeInfo {

    private String id;
    private String type;
    private String title;
    private String subTitle;
    private String content;
    private String updatedTime;


    @DynamoDBHashKey(attributeName = Constants.NOTICE_INFO_ID)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Constants.NOTICE_INFO_INDEX_TYPE_TIME)
    @DynamoDBAttribute(attributeName = Constants.NOTICE_INFO_TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = Constants.NOTICE_INFO_TITLE)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = Constants.NOTICE_INFO_SUB_TITLE)
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @DynamoDBAttribute(attributeName = Constants.NOTICE_INFO_CONTENT)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Constants.NOTICE_INFO_INDEX_TYPE_TIME)
    @DynamoDBAttribute(attributeName = Constants.NOTICE_INFO_UPDATED_TIME)
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
}
