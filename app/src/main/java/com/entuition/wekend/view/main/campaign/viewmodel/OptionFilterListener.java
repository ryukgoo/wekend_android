package com.entuition.wekend.view.main.campaign.viewmodel;

import com.entuition.wekend.data.source.product.ProductFilterOptions;

/**
 * Created by ryukgoo on 2017. 11. 10..
 */

public interface OptionFilterListener {
    void onClickFilter(ProductFilterOptions options);
    void onClickShowAll();
}
