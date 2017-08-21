package com.entuition.wekend.model.data.product;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 7. 26..
 */
public class CampaignQueryPage {

    private Iterator<ProductInfo> iterator;
    private int count;

    public CampaignQueryPage(PaginatedQueryList<ProductInfo> results, int count) {
        this.count = count;
        this.iterator = results.iterator();
    }

    public List<ProductInfo> getSubList() {

        List<ProductInfo> list = new ArrayList<ProductInfo>();

        for (int i = 0 ; iterator.hasNext() && i < count ; i ++) {
            ProductInfo info = iterator.next();
            list.add(info);
        }
        return list;
    }
}
