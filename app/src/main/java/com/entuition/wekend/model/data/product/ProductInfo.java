package com.entuition.wekend.model.data.product;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2016. 3. 10..
 */
@DynamoDBTable(tableName = Constants.PRODUCT_TABLE)
public class ProductInfo {

    private int id;
    private String titleKor;
    private String titleEng;
    private String subTitle;
    private int region;
    private int mainCategory;
    private int subCategory;
    private String description;
    private String telephone;
    private String address;
    private String subAddress;
    private String price;
    private String operationTime;
    private String parking;
    private String facebook;
    private String blog;
    private String instagram;
    private String homepage;
    private String etc;
    private int imageCount;
    private int likeCount;
    private String updatedTime;
    private String status;

    private String maleLikeTime;
    private String femaleLikeTime;

    private boolean isLike;

    @DynamoDBHashKey(attributeName = Constants.ATTRIBUTE_PRODUCT_ID)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_TITLE_KOR)
    public String getTitleKor() {
        return titleKor;
    }

    public void setTitleKor(String titleKor) {
        this.titleKor = titleKor;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_TITLE_ENG)
    public String getTitleEng() {
        return titleEng;
    }

    public void setTitleEng(String titleEng) {
        this.titleEng = titleEng;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_SUB_TITLE)
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_PRODUCT_REGION)
    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Constants.INDEX_MAIN_CATEGORY)
    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_MAIN_CATEGORY)
    public int getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(int mainCategory) {
        this.mainCategory = mainCategory;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_SUB_CATEGORY)
    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_TELEPHONE)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_ADDRESS)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_SUB_ADDRESS)
    public String getSubAddress() {
        return subAddress;
    }

    public void setSubAddress(String subAddress) {
        this.subAddress = subAddress;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_PRICE)
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_OPERATION_TIME)
    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_PARKING)
    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_FACEBOOK)
    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_BLOG)
    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_INSTAGRAM)
    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_HOMEPAGE)
    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Constants.INDEX_STATUS_LIKECOUNT)
    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_LIKECOUNT)
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_ETC)
    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = {Constants.INDEX_MAIN_CATEGORY, Constants.INDEX_STATUS_UPDATED_TIME})
    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_UPDATED_TIME)
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_IMAGE_COUNT)
    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {Constants.INDEX_STATUS_UPDATED_TIME, Constants.INDEX_STATUS_LIKECOUNT} )
    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_PRODUCT_STATUS)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_MALE_LIKE_TIME)
    public String getMaleLikeTime() {
        return maleLikeTime;
    }

    public void setMaleLikeTime(String maleLikeTime) {
        this.maleLikeTime = maleLikeTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_FEMALE_LIKE_TIME)
    public String getFemaleLikeTime() {
        return femaleLikeTime;
    }

    public void setFemaleLikeTime(String femaleLikeTime) {
        this.femaleLikeTime = femaleLikeTime;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}
