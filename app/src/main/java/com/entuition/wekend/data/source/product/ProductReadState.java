package com.entuition.wekend.data.source.product;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

@DynamoDBTable(tableName = ProductReadState.PRODUCT_READ_STATE_TABLE)
public class ProductReadState {

    /**
     * Product Read State
     */
    public static final String PRODUCT_READ_STATE_TABLE = "product_like_state";

    private class Attribute {
        private static final String PRODUCT_ID = "ProductId";
        private static final String MALE_LIKE_TIME = "MaleLikeTime";
        private static final String FEMALE_LIKE_TIME = "FemaleLikeTime";
    }

    private int productId;
    private String maleLikeTime;
    private String femaleLikeTime;

    @DynamoDBHashKey(attributeName = Attribute.PRODUCT_ID)
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
}
