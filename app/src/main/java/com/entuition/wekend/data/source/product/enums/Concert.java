package com.entuition.wekend.data.source.product.enums;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Concert implements IProductEnum {

    NONE(100, 0, R.string.sub_category_all),
    EXHIBITION(2001, 1, R.string.sub_category_concert_0),          // 전시회
    CLASSIC_OPERA(2002, 2, R.string.sub_category_concert_1),       // 클래식/오페라
    MUSICAL_PLAYS(2003, 3, R.string.sub_category_concert_2),       // 뮤지컬/연극
    SHOW(2004, 4, R.string.sub_category_concert_3);                // 공연

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

    public static List<IProductEnum> asList() {
        List<IProductEnum> list = new ArrayList<>();
        list.add(NONE);
        list.add(EXHIBITION);
        list.add(CLASSIC_OPERA);
        list.add(MUSICAL_PLAYS);
        list.add(SHOW);
        return list;
    }
}
