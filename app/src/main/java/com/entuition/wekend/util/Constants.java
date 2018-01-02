package com.entuition.wekend.util;

import com.amazonaws.regions.Regions;

/**
 * Created by ryukgoo on 2016. 1. 2..
 */
public class Constants {

    public static final String APP_PACKAGE_NAME = "com.entuition.wekend";

    public static final String ACCOUNT_ID = "039291388167";

    public static final String IDENTITY_POOL_ID = "ap-northeast-1:7fd2e15f-b246-4086-a019-dc6d446bdd99";

    public static final String DEVELOPER_PROVIDER_NAME = "login.entuition.picnic";

    public static final String PLATFORM = "GCM";

    public static final Regions REGIONS = Regions.AP_NORTHEAST_1;

    public static final String SESSION_DURATION = "900";

    public static final int    SESSION_DURATION_INT = Integer.parseInt(SESSION_DURATION);

    public static final String APP_NAME = getAppName();

    public static final String START_ACTIVITY_POSITION = "start_activity_position";

    private static String getAppName() {
        return System.getProperty("APP_NAME", "Wekend").toLowerCase();
    }

    public static class ExtraKeys {
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String NICKNAME = "nickname";
        public static final String GENDER = "gender";
        public static final String BIRTH = "birth";
        public static final String PRODUCT_ID = "product_id";
        public static final String PRODUCT_TITLE = "product_title";
        public static final String USER_ID = "userId";
        public static final String FRIEND_ID = "friendId";
        public static final String MAIL_TYPE = "mailType";
        public static final String MAP_ADDRESS = "map_address";
        public static final String CELL_INDEX = "cell_index";
    }

    public enum GenderValue {
        male, female;
    }

    public enum UserStatus {
        enable, verified, not_verified, not_enough_info;
    }

    public enum ProductTable {
        ProductId;
    }

    public enum ProductStatus {
        Enabled, Disabled, Expired, Deleted;
    }

    public enum ReadState {
        unread, read;
    }

    public enum NotificationType {
        like, receiveMail, sendMail;
    }
}
