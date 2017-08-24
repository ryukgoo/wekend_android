package com.entuition.wekend.model.data.product.enums;

import android.content.Context;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Concert implements IProductEnum {

    NONE(100, 0, R.string.campaign_filter_subcategory_all),
    EXHIBITION(2001, 1, R.string.campaign_filter_subcategory_concert_0),          // 전시회
    CLASSIC_OPERA(2002, 2, R.string.campaign_filter_subcategory_concert_1),       // 클래식/오페라
    MUSICAL_PLAYS(2003, 3, R.string.campaign_filter_subcategory_concert_2),       // 뮤지컬/연극
    SHOW(2004, 4, R.string.campaign_filter_subcategory_concert_3);                // 공연

    private int identifier;
    private int index;
    private int resource;

    Concert(int identifier, int index, int resource) {
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
        IProductEnum[] array = { NONE, EXHIBITION, CLASSIC_OPERA, MUSICAL_PLAYS, SHOW };
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
