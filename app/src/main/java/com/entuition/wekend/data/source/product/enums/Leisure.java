package com.entuition.wekend.data.source.product.enums;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Leisure implements IProductEnum {

    NONE(100, 0, R.string.sub_category_all),
    LEISURE(3001, 1, R.string.sub_category_leisure_0),             // 레져
    SPORTS(3002, 2, R.string.sub_category_leisure_1);              // 스포츠;

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

    public static List<IProductEnum> asList() {
        List<IProductEnum> list = new ArrayList<>();
        list.add(NONE);
        list.add(LEISURE);
        list.add(SPORTS);
        return list;
    }
}
