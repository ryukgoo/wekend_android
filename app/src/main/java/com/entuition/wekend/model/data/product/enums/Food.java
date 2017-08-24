package com.entuition.wekend.model.data.product.enums;

import android.content.Context;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Food implements IProductEnum {

    NONE(100, 0, R.string.campaign_filter_subcategory_all),
    WESTERN_FOOD(1001, 1, R.string.campaign_filter_subcategory_food_0),
    JAPANESE_FOOD(1002, 2, R.string.campaign_filter_subcategory_food_1),
    CHINESE_FOOD(1003, 3, R.string.campaign_filter_subcategory_food_2),
    KOREAN_FOOD(1004, 4, R.string.campaign_filter_subcategory_food_3),
    ASIAN_FOOD(1005, 5, R.string.campaign_filter_subcategory_food_4),
    DESSERT(1006, 6, R.string.campaign_filter_subcategory_food_5),
    HOTEL(1007, 7, R.string.campaign_filter_subcategory_food_6);

    private int identifier;
    private int index;
    private int resource;

    Food(int identifier, int index, int resource) {
        this.identifier = identifier;
        this.index = index;
        this.resource = resource;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getResource() {
        return resource;
    }

    public static IProductEnum[] asArray() {
        IProductEnum[] array = { NONE, WESTERN_FOOD, JAPANESE_FOOD, CHINESE_FOOD, KOREAN_FOOD, ASIAN_FOOD, DESSERT, HOTEL };
        return array;
    }

    public static List<String> asStringList(Context context) {
        List<String> stringList = new ArrayList<String>();

        for (IProductEnum category : asArray()) {
            stringList.add(context.getString(category.getResource()));
        }
        return stringList;
    }
}
