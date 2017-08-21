package com.entuition.wekend.model.data.product;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.entuition.wekend.model.Constants;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

@DynamoDBTable(tableName = Constants.PRODUCT_READ_STATE_TABLE)
public class ProductReadState {

    private int productId;
    private String maleLikeTime;
    private String femaleLikeTime;

    @DynamoDBHashKey(attributeName = Constants.ATTRIBUTE_PRODUCT_READ_STATE_PRODUCT_ID)
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_PRODUCT_READ_STATE_MALE_LIKE_TIME)
    public String getMaleLikeTime() {
        return maleLikeTime;
    }

    public void setMaleLikeTime(String maleLikeTime) {
        this.maleLikeTime = maleLikeTime;
    }

    @DynamoDBAttribute(attributeName = Constants.ATTRIBUTE_PRODUCT_READ_STATE_FEMALE_LIKE_TIME)
    public String getFemaleLikeTime() {
        return femaleLikeTime;
    }

    public void setFemaleLikeTime(String femaleLikeTime) {
        this.femaleLikeTime = femaleLikeTime;
    }
}
