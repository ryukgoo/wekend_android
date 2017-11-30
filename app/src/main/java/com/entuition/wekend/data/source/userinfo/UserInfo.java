package com.entuition.wekend.data.source.userinfo;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAutoGeneratedKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Set;

/**
 * Created by ryukgoo on 2015. 12. 16..
 */
@DynamoDBTable(tableName = UserInfo.USER_TABLE)
public class UserInfo {

    public static final String USER_TABLE = "picnic_users";

    public static class Index {
        public static final String USERNAME = "username-index";
        public static final String NICKNAME = "nickname-index";
    }

    static class Attribute {
        static final String USERNAME = "username";
        static final String HASHED_PASSWORD = "hashed_password";
        static final String STATUS = "status";
        static final String USERID = "userid";
        static final String REGISTERED_TIME = "registered_time";
        static final String NICKNAME = "nickname";
        static final String GENDER = "gender";
        static final String BIRTH = "birth";
        static final String PHONE = "phone";
        static final String PHOTOS = "photos";
        static final String BALLOON = "balloon";
        static final String ENDPOINT_ARN = "EndpointARN";
        static final String NEW_LIKE_COUNT = "NewLikeCount";
        static final String NEW_SEND_COUNT = "NewSendCount";
        static final String NEW_RECEIVE_COUNT = "NewReceiveCount";
    }

    public static final int DEFAULT_BIRTH_VALUE = 1900;

    private String userId;
    private String username;
    private String hashedPassword;
    private String status;
    private String registeredTime;
    private String nickname;
    private String gender;
    private int birth;
    private String phone;
    private Set<String> photos;
    private int balloon;
    private String endpointARN;
    private int newLikeCount;
    private int newSendCount;
    private int newReceiveCount;

    public UserInfo() {}

    public UserInfo(UserInfo origin) {
        this.userId = origin.userId;
        this.username = origin.username;
        this.hashedPassword = origin.hashedPassword;
        this.status = origin.status;
        this.registeredTime = origin.registeredTime;
        this.nickname = origin.nickname;
        this.gender = origin.gender;
        this.birth = origin.birth;
        this.phone = origin.phone;
        this.photos = origin.photos;
        this.balloon = origin.balloon;
        this.endpointARN = origin.endpointARN;
        this.newLikeCount = origin.newLikeCount;
        this.newSendCount = origin.newSendCount;
        this.newReceiveCount = origin.newReceiveCount;
    }

    @DynamoDBAutoGeneratedKey
    @DynamoDBHashKey(attributeName = Attribute.USERID)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userid) {
        this.userId = userid;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.USERNAME)
    @DynamoDBAttribute(attributeName = Attribute.USERNAME)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = Attribute.HASHED_PASSWORD)
    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @DynamoDBAttribute(attributeName = Attribute.STATUS)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = Attribute.REGISTERED_TIME)
    public String getRegisteredTime() {
        return registeredTime;
    }

    public void setRegisteredTime(String registeredTime) {
        this.registeredTime = registeredTime;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.NICKNAME)
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

    @DynamoDBAttribute(attributeName = Attribute.BIRTH)
    public int getBirth() {
        return birth;
    }

    public void setBirth(int birth) {
        this.birth = birth;
    }

    @DynamoDBAttribute(attributeName = Attribute.PHONE)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @DynamoDBAttribute(attributeName = Attribute.PHOTOS)
    public Set<String> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<String> photos) {
        this.photos = photos;
    }

    @DynamoDBAttribute(attributeName = Attribute.BALLOON)
    public int getBalloon() {
        return balloon;
    }

    public void setBalloon(int balloon) {
        this.balloon = balloon;
    }

    @DynamoDBAttribute(attributeName = Attribute.ENDPOINT_ARN)
    public String getEndpointARN() {
        return endpointARN;
    }

    public void setEndpointARN(String endpointARN) {
        this.endpointARN = endpointARN;
    }

    @DynamoDBAttribute(attributeName = Attribute.NEW_LIKE_COUNT)
    public int getNewLikeCount() {
        return newLikeCount;
    }

    public void setNewLikeCount(int newLikeCount) {
        this.newLikeCount = newLikeCount;
    }

    @DynamoDBAttribute(attributeName = Attribute.NEW_SEND_COUNT)
    public int getNewSendCount() {
        return newSendCount;
    }

    public void setNewSendCount(int newSendCount) {
        this.newSendCount = newSendCount;
    }

    @DynamoDBAttribute(attributeName = Attribute.NEW_RECEIVE_COUNT)
    public int getNewReceiveCount() {
        return newReceiveCount;
    }

    public void setNewReceiveCount(int newReceiveCount) {
        this.newReceiveCount = newReceiveCount;
    }
}