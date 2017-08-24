package com.entuition.wekend.model.data.product.enums;

import android.content.Context;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Leisure implements IProductEnum {

    NONE(100, 0, R.string.campaign_filter_subcategory_all),
    LEISURE(3001, 1, R.string.campaign_filter_subcategory_leisure_0),             // 레져
    SPORTS(3002, 2, R.string.campaign_filter_subcategory_leisure_1);              // 스포츠;

    private int identifier;
    private int index;
    private int resource;

    Leisure(int identifier, int index, int resource) {
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
        IProductEnum[] array = { NONE, LEISURE, SPORTS };
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
