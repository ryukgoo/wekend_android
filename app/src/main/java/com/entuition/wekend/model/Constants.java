package com.entuition.wekend.model;

import com.amazonaws.regions.Regions;

/**
 * Created by ryukgoo on 2016. 1. 2..
 */
public class Constants {

    public static final String APP_PACKAGE_NAME = "com.entuition.wekend";

    public static final String ACCOUNT_ID = "039291388167";

    public static final String IDENTITY_POOL_ID = "ap-northeast-1:7fd2e15f-b246-4086-a019-dc6d446bdd99";

    public static final String DEVELOPER_PROVIDER_NAME = "login.entuition.picnic";

    public static final String REGION = getReion();

    public static final Regions REGIONS = Regions.AP_NORTHEAST_1;

    public static final String SESSION_DERATION = "900";

    public static final String APP_NAME = getAppName();

    public static final String DEVICE_TABLE = "picnic_devies";

    public static final String CAMPAIGN_TABLE = getCampaignTable();

    public static final String START_ACTIVITY_POSITION = "start_activity_position";

    // Is it OK?

    /**
     * User Table Schema
     */
    public static final String USER_TABLE = "picnic_users";
    public static final String INDEX_USERNAME = "username-index";
    public static final String INDEX_NICKNAME = "nickname-index";

    public static final String ATTRIBUTE_USERNAME = "username";
    public static final String ATTRIBUTE_HASHED_PASSWORD = "hashed_password";
    public static final String ATTRIBUTE_STATUS = "status";
    public static final String ATTRIBUTE_USERID = "userid";
    public static final String ATTRIBUTE_REGISTERED_TIME = "registered_time";
    public static final String ATTRIBUTE_NICKNAME = "nickname";
    public static final String ATTRIBUTE_GENDER = "gender";
    public static final String ATTRIBUTE_BIRTH = "birth";
    public static final String ATTRIBUTE_PHONE = "phone";
    public static final String ATTRIBUTE_PHOTOS = "photos";
    public static final String ATTRIBUTE_BALLOON = "balloon";
    public static final String ATTRIBUTE_ENDPOINT_ARN = "EndpointARN";
    public static final String ATTRIBUTE_NEW_LIKE_COUNT = "NewLikeCount";
    public static final String ATTRIBUTE_NEW_SEND_COUNT = "NewSendCount";
    public static final String ATTRIBUTE_NEW_RECEIVE_COUNT = "NewReceiveCount";

    // value male
    public static final String VALUE_GENDER_MALE = "male";
    // value female
    public static final String VALUE_GENDER_FEMALE = "female";

    // UserInfo Status
    public static final String USER_STATUS_ENABLED = "status_enable";
    public static final String USER_STATUS_VERIFIED = "status_verified";
    public static final String USER_STATUS_NOT_VERIFIED = "status_not_verified";
    public static final String USER_STATUS_NOT_INPUT_INFO = "status_not_input_info";

    public static final int DEFAULT_BIRTH_VALUE = 1900;

    /**
     * Product Table Schema
     */
    public static final String PRODUCT_TABLE = "product_db";
    public static final String INDEX_STATUS_UPDATED_TIME = "ProductStatus-UpdatedTime-index";
    public static final String INDEX_STATUS_LIKECOUNT = "ProductStatus-LikeCount-index";
    public static final String INDEX_MAIN_CATEGORY = "MainCategory-UpdatedTime-index";

    public static final String ATTRIBUTE_PRODUCT_ID = "ProductId";
    public static final String ATTRIBUTE_TITLE_KOR = "TitleKor";
    public static final String ATTRIBUTE_TITLE_ENG = "TitleEng";
    public static final String ATTRIBUTE_SUB_TITLE = "SubTitle";
    public static final String ATTRIBUTE_PRODUCT_REGION = "ProductRegion";
    public static final String ATTRIBUTE_MAIN_CATEGORY = "MainCategory";
    public static final String ATTRIBUTE_SUB_CATEGORY = "SubCategory";
    public static final String ATTRIBUTE_DESCRIPTION = "Description";
    public static final String ATTRIBUTE_TELEPHONE = "Telephone";
    public static final String ATTRIBUTE_ADDRESS = "Address";
    public static final String ATTRIBUTE_SUB_ADDRESS = "SubAddress";
    public static final String ATTRIBUTE_PRICE = "Price";
    public static final String ATTRIBUTE_PARKING = "Parking";
    public static final String ATTRIBUTE_OPERATION_TIME = "OperationTime";
    public static final String ATTRIBUTE_FACEBOOK = "Facebook";
    public static final String ATTRIBUTE_BLOG = "Blog";
    public static final String ATTRIBUTE_INSTAGRAM = "Instagram";
    public static final String ATTRIBUTE_HOMEPAGE = "Homepage";
    public static final String ATTRIBUTE_ETC = "Etc";
    public static final String ATTRIBUTE_IMAGE_COUNT = "ImageCount";
    public static final String ATTRIBUTE_UPDATED_TIME = "UpdatedTime";
    public static final String ATTRIBUTE_LIKECOUNT = "LikeCount";
    public static final String ATTRIBUTE_PRODUCT_STATUS = "ProductStatus";
    public static final String ATTRIBUTE_MALE_LIKE_TIME = "MaleLikeTime";
    public static final String ATTRIBUTE_FEMALE_LIKE_TIME = "FemaleLikeTime";

    public static final int ITEM_COUNT_PER_PAGE = 20;
    public static final int GOOGLE_MAP_ZOOM = 16;

    /**
     * Product Read State
     */
    public static final String PRODUCT_READ_STATE_TABLE = "product_like_state";
    public static final String ATTRIBUTE_PRODUCT_READ_STATE_PRODUCT_ID = "ProductId";
    public static final String ATTRIBUTE_PRODUCT_READ_STATE_MALE_LIKE_TIME = "MaleLikeTime";
    public static final String ATTRIBUTE_PRODUCT_READ_STATE_FEMALE_LIKE_TIME = "FemaleLikeTime";

    /**
     * LikeDBItem Table Schema
     */
    public static final String LIKE_TABLE = "picnic_like_db";
    public static final String INDEX_LIKE_PRODUCTID_UPDATEDTIME = "ProductId-UpdatedTime-index";
    public static final String INDEX_LIKE_USERID_UPDATEDTIME = "UserId-UpdatedTime-index";

    public static final String ATTRIBUTE_LIKE_USER_ID = "UserId";
    public static final String ATTRIBUTE_LIKE_GENDER = "Gender";
    public static final String ATTRIBUTE_LIKE_NICKNAME = "Nickname";
    public static final String ATTRIBUTE_LIKE_PRODUCT_ID = "ProductId";
    public static final String ATTRIBUTE_LIKE_PRODUCT_TITLE = "ProductTitle";
    public static final String ATTRIBUTE_LIKE_PRODUCT_DESC = "ProductDesc";
    public static final String ATTRIBUTE_LIKE_UPDATED_TIME = "UpdatedTime";
    public static final String ATTRIBUTE_READ_TIME = "ReadTime";
    public static final String ATTRIBUTE_LIKE_ID = "LikeId";
    public static final String ATTRIBUTE_LIKE_HAS_NEW = "HasNew";

    public static final int LIKE_COUNT_DELIMETER = 1000000;

    /**
     * LikeReadState
     */

    public static final String LIKE_READ_STATE_TALBE = "like_read_state";
    public static final String INDEX_READSTATE_PRODUCTID_USERID = "ProductId-UserId-index";

    public static final String ATTRIBUTE_LIKE_READ_STATE_LIKE_ID = "LikeId";
    public static final String ATTRIBUTE_LIKE_READ_STATE_USERID = "UserId";
    public static final String ATTRIBUTE_LIKE_READ_STATE_PRODUCTID = "ProductId";
    public static final String ATTRIBUTE_LIKE_READ_STATE_LIKEUSERID = "LikeUserId";
    public static final String ATTRIBUTE_LIKE_READ_STATE_READTIME = "ReadTime";

    /**
     * Send Mail Box
     */
    public static final String SEND_MAIL_TABLE = "send_mail_db";
    public static final String SEND_MAIL_INDEX_USERID_RESPONSETIME = "UserId-ResponseTime-index";
    public static final String ATTRIBUTE_SEND_MAIL_USER_ID = "UserId";
    public static final String ATTRIBUTE_SEND_MAIL_UPDATED_TIME = "UpdatedTime";
    public static final String ATTRIBUTE_SEND_MAIL_RESPONSE_TIME = "ResponseTime";
    public static final String ATTRIBUTE_SEND_MAIL_PRODUCT_ID = "ProductId";
    public static final String ATTRIBUTE_SEND_MAIL_PRODUCT_TITLE = "ProductTitle";
    public static final String ATTRIBUTE_SEND_MAIL_RECEIVER_ID = "ReceiverId";
    public static final String ATTRIBUTE_SEND_MAIL_SENDER_NICKNAME = "SenderNickname";
    public static final String ATTRIBUTE_SEND_MAIL_RECEIVER_NICKNAME = "ReceiverNickname";
    public static final String ATTRIBUTE_SEND_MAIL_STATUS = "ProposeStatus";
    public static final String ATTRIBUTE_SEND_MAIL_ISREAD = "IsRead";

    public static final int MAIL_STATUS_UNREAD = 0;
    public static final int MAIL_STATUS_READ = 1;

    public static final String PROPOSE_STATUS_NOT_MADE = "notMade";
    public static final String PROPOSE_STATUS_MADE = "Made";
    public static final String PROPOSE_STATUS_REJECT = "reject";
    public static final String PROPOSE_STATUS_DELETE = "delete";
    public static final String PROPOSE_STATUS_ALREADY_MADE = "alreadyMade";

    /**
     * Receive Mail Box
     */
    public static final String RECEIVE_MAIL_TABLE = "receive_mail_db";
    public static final String RECEIVE_MAIL_INDEX_USERID_RESPONSE_TIME = "UserId-ResponseTime-index";
    public static final String ATTRIBUTE_RECEIVE_MAIL_USER_ID = "UserId";
    public static final String ATTRIBUTE_RECEIVE_MAIL_UPDATED_TIME = "UpdatedTime";
    public static final String ATTRIBUTE_RECEIVE_MAIL_RESPONSE_TIME = "ResponseTime";
    public static final String ATTRIBUTE_RECEIVE_MAIL_PRODUCT_ID = "ProductId";
    public static final String ATTRIBUTE_RECEIVE_MAIL_PRODUCT_TITLE = "ProductTitle";
    public static final String ATTRIBUTE_RECEIVE_MAIL_SENDER_ID = "SenderId";
    public static final String ATTRIBUTE_RECEIVE_MAIL_SENDER_NICKNAME = "SenderNickname";
    public static final String ATTRIBUTE_RECEIVE_MAIL_RECEIVER_NICKNAME = "ReceiverNickname";
    public static final String ATTRIBUTE_RECEIVE_MAIL_STATUS = "ProposeStatus";
    public static final String ATTRIBUTE_RECEIVE_MAIL_ISREAD = "IsRead";

    public static final String TYPE_NOTIFICATION_LIKE = "like";
    public static final String TYPE_NOTIFICATION_RECEIVE_MAIL = "receiveMail";
    public static final String TYPE_NOTIFICATION_SEND_MAIL = "sendMail";

    /**
     * Notice And Help Info DB
     */

    public static final String NOTICE_INTO_DB = "notice_db";
    public static final String NOTICE_INFO_INDEX_TYPE_TIME = "noticeType-updatedTime-index";
    public static final String NOTICE_INFO_ID = "noticeId";
    public static final String NOTICE_INFO_TYPE = "noticeType";
    public static final String NOTICE_INFO_TITLE = "title";
    public static final String NOTICE_INFO_SUB_TITLE = "subTitle";
    public static final String NOTICE_INFO_CONTENT = "content";
    public static final String NOTICE_INFO_UPDATED_TIME = "updatedTime";

    // Intent parameters
    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String PARAMETER_NICKNAME = "nickname";
    public static final String PARAMETER_GENDER = "gender";
    public static final String PARAMETER_BIRTH = "birth";

    public static final String PARAMETER_POSITION = "list_position";
    public static final String PARAMETER_PRODUCT_ID = "product_id";
    public static final String PARAMETER_PRODUCT_TITLE = "product_title";

    public static final String PARAMETER_FRIEND_ID = "friendId";
    public static final String PARAMETER_PROPOSER_ID = "senderId";
    public static final String PARAMETER_PROPOSEE_ID = "receiverId";
    public static final String PARAMETER_PROPOSE_UPDATEDTIME = "proposeUpdatedTime";

    public static final String PARAMETER_MAP_ADDRESS = "map_address";

    private static String getAppName() {
        return System.getProperty("APP_NAME", "Wekend").toLowerCase();
    }

    private static String getCampaignTable() {
        return APP_NAME + "_campaigns";
    }

    private static String getReion() {
        return System.getProperty("REGION", "ap-northeast-1");
    }

    /**
     * For S3
     */
    public static final String AMAZON_S3_ADDRESS = "s3-ap-northeast-1.amazonaws.com";
    public static final String JPG_FILE_FORMAT = ".jpg";

    public static final String PROFILE_IMAGE_BUCKET_NAME = "entuition-user-profile";
    public static final String PROFILE_THUMB_BUCKET_NAME = "entuition-user-profile-thumb";
    public static final String PROFILE_IMAGE_NAME_PREFIX = "profile_image";

    public static final String PRODUCT_IMAGE_BUCKET_NAME = "entuition-product-images";
    public static final String PRODUCT_THUMB_BUCKET_NAME = "entuition-product-images-thumb";
    public static final String PRODUCT_IMAGE_NAME_PREFIX = "product_image";
}
