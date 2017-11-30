package com.entuition.wekend.data.source.product.enums;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum Category implements IProductEnum {

    NONE(100, 0, R.string.category_all),
    FOOD(101, 1, R.string.category_food),
    CONCERT(102, 2, R.string.category_concert),
    LEISURE(103, 3, R.string.category_leisure);

    private int identifier;
    private int index;
    private int resource;

    Category(int identifier, int index, int resource) {
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
        list.add(FOOD);
        list.add(CONCERT);
        list.add(LEISURE);
        return list;
    }
}
