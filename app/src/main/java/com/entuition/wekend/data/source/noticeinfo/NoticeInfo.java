package com.entuition.wekend.data.source.noticeinfo;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ryukgoo on 2017. 8. 3..
 */

@DynamoDBTable(tableName = NoticeInfo.NOTICE_INTO_DB)
public class NoticeInfo {

    static final String NOTICE_INTO_DB = "notice_db";

    public static class Index {
        public static final String TYPE_TIME = "noticeType-updatedTime-index";
    }

    static class Attribute {
        static final String NOTICE_INFO_ID = "noticeId";
        static final String NOTICE_INFO_TYPE = "noticeType";
        static final String NOTICE_INFO_TITLE = "title";
        static final String NOTICE_INFO_SUB_TITLE = "subTitle";
        static final String NOTICE_INFO_CONTENT = "content";
        static final String NOTICE_INFO_UPDATED_TIME = "updatedTime";
    }

    private String id;
    private String type;
    private String title;
    private String subTitle;
    private String content;
    private String updatedTime;

    /*
    public NoticeInfo(String id, String type, String title, String subTitle, String content, String updatedTime) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
        this.updatedTime = updatedTime;
    }
    */

    @DynamoDBHashKey(attributeName = Attribute.NOTICE_INFO_ID)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.TYPE_TIME)
    @DynamoDBAttribute(attributeName = Attribute.NOTICE_INFO_TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = Attribute.NOTICE_INFO_TITLE)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DynamoDBAttribute(attributeName = Attribute.NOTICE_INFO_SUB_TITLE)
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @DynamoDBAttribute(attributeName = Attribute.NOTICE_INFO_CONTENT)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Index.TYPE_TIME)
    @DynamoDBAttribute(attributeName = Attribute.NOTICE_INFO_UPDATED_TIME)
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
}
