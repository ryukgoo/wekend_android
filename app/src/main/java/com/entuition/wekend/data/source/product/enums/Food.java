package com.entuition.wekend.data.source.product.enums;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Food implements IProductEnum {

    NONE(100, 0, R.string.sub_category_all),
    WESTERN_FOOD(1001, 1, R.string.sub_category_food_0),
    JAPANESE_FOOD(1002, 2, R.string.sub_category_food_1),
    CHINESE_FOOD(1003, 3, R.string.sub_category_food_2),
    KOREAN_FOOD(1004, 4, R.string.sub_category_food_3),
    ASIAN_FOOD(1005, 5, R.string.sub_category_food_4),
    DESSERT(1006, 6, R.string.sub_category_food_5),
    HOTEL(1007, 7, R.string.sub_category_food_6);

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

    public static List<IProductEnum> asList() {
        List<IProductEnum> list = new ArrayList<>();
        list.add(NONE);
        list.add(WESTERN_FOOD);
        list.add(JAPANESE_FOOD);
        list.add(CHINESE_FOOD);
        list.add(KOREAN_FOOD);
        list.add(ASIAN_FOOD);
        list.add(DESSERT);
        list.add(HOTEL);
        return list;
    }
}
