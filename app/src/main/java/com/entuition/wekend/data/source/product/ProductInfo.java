package com.entuition.wekend.data.source.product;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ryukgoo on 2016. 3. 10..
 */
@DynamoDBTable(tableName = ProductInfo.PRODUCT_TABLE)
public class ProductInfo {

    /**
     * Product Table Schema
     */
    static final String PRODUCT_TABLE = "product_db";

    static class Index {
        static final String UPDATED_TIME = "ProductStatus-UpdatedTime-index";
        static final String LIKE_COUNT = "ProductStatus-LikeCount-index";
        static final String MAIN_CATEGORY_UPDATED_TIME = "MainCategory-UpdatedTime-index";
    }

    static class Attribute {
        static final String PRODUCT_ID = "ProductId";
        static final String TITLE_KOR = "TitleKor";
        static final String TITLE_ENG = "TitleEng";
        static final String SUB_TITLE = "SubTitle";
        static final String PRODUCT_REGION = "ProductRegion";
        static final String MAIN_CATEGORY = "MainCategory";
        static final String SUB_CATEGORY = "SubCategory";
        static final String DESCRIPTION = "Description";
        static final String TELEPHONE = "Telephone";
        static final String ADDRESS = "Address";
        static final String SUB_ADDRESS = "SubAddress";
        static final String PRICE = "Price";
        static final String PARKING = "Parking";
        static final String OPERATION_TIME = "OperationTime";
        static final String FACEBOOK = "Facebook";
        static final String BLOG = "Blog";
        static final String INSTAGRAM = "Instagram";
        static final String HOMEPAGE = "Homepage";
        static final String ETC = "Etc";
        static final String IMAGE_COUNT = "ImageCount";
        static final String UPDATED_TIME = "UpdatedTime";
        static final String LIKECOUNT = "LikeCount";
        static final String PRODUCT_STATUS = "ProductStatus";
        static final String MALE_LIKE_TIME = "MaleLikeTime";
        static final String FEMALE_LIKE_TIME = "FemaleLikeTime";
    }

    public static final int LIKE_COUNT_DELIMETER = 1000000;
    public static final int ITEM_COUNT_PER_PAGE = 20;
    public static final int GOOGLE_MAP_ZOOM = 16;

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
    private String totalAddress;
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

    // -> Ignore?
    private String maleLikeTime;
    private String femaleLikeTime;

    private boolean isLike;

    @DynamoDBHashKey(attributeName = Attribute.PRODUCT_ID)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @DynamoDBAttribute(attributeName = Attribute.TITLE_KOR)
    public String getTitleKor() {
        return titleKor;
    }

    public void setTitleKor(String titleKor) {
        this.titleKor = titleKor;
    }

    @DynamoDBAttribute(attributeName = Attribute.TITLE_ENG)
    public String getTitleEng() {
        return titleEng;
    }

    public void setTitleEng(String titleEng) {
        this.titleEng = titleEng;
    }

    @DynamoDBAttribute(attributeName = Attribute.SUB_TITLE)
    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    @DynamoDBAttribute(attributeName = Attribute.PRODUCT_REGION)
    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = Index.MAIN_CATEGORY_UPDATED_TIME)
    @DynamoDBAttribute(attributeName = Attribute.MAIN_CATEGORY)
    public int getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(int mainCategory) {
        this.mainCategory = mainCategory;
    }

    @DynamoDBAttribute(attributeName = Attribute.SUB_CATEGORY)
    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    @DynamoDBAttribute(attributeName = Attribute.DESCRIPTION)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = Attribute.TELEPHONE)
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @DynamoDBAttribute(attributeName = Attribute.ADDRESS)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        setTotalAddress();
    }

    @DynamoDBAttribute(attributeName = Attribute.SUB_ADDRESS)
    public String getSubAddress() {
        return subAddress;
    }

    public void setSubAddress(String subAddress) {
        this.subAddress = subAddress;
        setTotalAddress();
    }

    @DynamoDBAttribute(attributeName = Attribute.PRICE)
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = Attribute.OPERATION_TIME)
    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.PARKING)
    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    @DynamoDBAttribute(attributeName = Attribute.FACEBOOK)
    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    @DynamoDBAttribute(attributeName = Attribute.BLOG)
    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    @DynamoDBAttribute(attributeName = Attribute.INSTAGRAM)
    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    @DynamoDBAttribute(attributeName = Attribute.HOMEPAGE)
    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = Index.LIKE_COUNT)
    @DynamoDBAttribute(attributeName = Attribute.LIKECOUNT)
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    @DynamoDBAttribute(attributeName = Attribute.ETC)
    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = {Index.MAIN_CATEGORY_UPDATED_TIME, Index.UPDATED_TIME})
    @DynamoDBAttribute(attributeName = Attribute.UPDATED_TIME)
    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.IMAGE_COUNT)
    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexNames = {Index.UPDATED_TIME, Index.LIKE_COUNT} )
    @DynamoDBAttribute(attributeName = Attribute.PRODUCT_STATUS)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = Attribute.MALE_LIKE_TIME)
    public String getMaleLikeTime() {
        return maleLikeTime;
    }

    public void setMaleLikeTime(String maleLikeTime) {
        this.maleLikeTime = maleLikeTime;
    }

    @DynamoDBAttribute(attributeName = Attribute.FEMALE_LIKE_TIME)
    public String getFemaleLikeTime() {
        return femaleLikeTime;
    }

    public void setFemaleLikeTime(String femaleLikeTime) {
        this.femaleLikeTime = femaleLikeTime;
    }

    @DynamoDBIgnore
    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    @DynamoDBIgnore
    public String getTotalAddress() {
        return totalAddress;
    }

    private void setTotalAddress() {
        if (address != null) {
            if (subAddress == null) {
                totalAddress = address;
            } else {
                totalAddress = address + " " + subAddress;
            }
        }
    }

    @DynamoDBIgnore
    public static void setLikeCountToInfo(ProductInfo info, int count) {
        info.setLikeCount(count * 1000000 + info.getId());
    }

}
