package com.entuition.wekend.model.data.product.enums;

import android.content.Context;

import com.entuition.wekend.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ryukgoo on 2016. 3. 29..
 */
public enum ProductRegion implements IProductEnum {

    NONE(100, 0, R.string.campaign_filter_region_all),
    REGION_SEOUL1(4001, 1, R.string.campaign_filter_region_0),        // 압구정/청담/신사
    REGION_SEOUL2(4002, 2, R.string.campaign_filter_region_1),        // 서래마을/서초/방배
    REGION_SEOUL3(4012, 3, R.string.campaign_filter_region_2),        // 도곡/대치/양재
    REGION_SEOUL4(4011, 4, R.string.campaign_filter_region_3),        // 역삼/논현/삼성
    REGION_SEOUL5(4003, 5, R.string.campaign_filter_region_4),        // 이태원/한남
    REGION_SEOUL6(4007, 6, R.string.campaign_filter_region_5),        // 삼청(북촌)/효자(서촌)/인사
    REGION_SEOUL7(4008, 7, R.string.campaign_filter_region_6),        // 평창/부암
    REGION_SEOUL8(4014, 8, R.string.campaign_filter_region_7),        // 종로/광화문
    REGION_SEOUL9(4015, 9, R.string.campaign_filter_region_8),        // 성북/정동
    REGION_SEOUL10(4004, 10, R.string.campaign_filter_region_9),      // 홍대/합정
    REGION_SEOUL11(4005, 11, R.string.campaign_filter_region_10),      // 연남/연희
    REGION_SEOUL12(4006, 12, R.string.campaign_filter_region_11),      // 이촌/용산
    REGION_SEOUL13(4010, 13, R.string.campaign_filter_region_12),      // 잠실/송파/강동
    REGION_SEOUL14(4013, 14, R.string.campaign_filter_region_13),      // 광진/성수
    REGION_SEOUL15(4009, 15, R.string.campaign_filter_region_14);      // 장충/혜화/기타

    private int identifier;
    private int index;
    private int resource;

    ProductRegion(int identifier, int index, int resource) {
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
        IProductEnum[] array = { NONE, REGION_SEOUL1, REGION_SEOUL2, REGION_SEOUL3,  REGION_SEOUL4,
                REGION_SEOUL5, REGION_SEOUL6, REGION_SEOUL7, REGION_SEOUL8, REGION_SEOUL9, REGION_SEOUL10,
                REGION_SEOUL11, REGION_SEOUL12, REGION_SEOUL13, REGION_SEOUL14, REGION_SEOUL15 };
        return array;
    }

    public static Map<Integer, Integer> getStringResourceMap() {
        Map<Integer, Integer> stringMap = new HashMap<Integer, Integer>();
        for (IProductEnum region : asArray()) {
            stringMap.put(region.getIdentifier(), region.getResource());
        }
        return stringMap;
    }

    public static List<String> asStringList(Context context) {
        List<String> stringList = new ArrayList<String>();

        for (IProductEnum category : asArray()) {
            stringList.add(context.getString(category.getResource()));
        }
        return stringList;
    }
}
