package com.entuition.wekend.view.main.fragment.campaign;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by ryukgoo on 2016. 8. 8..
 */
public class CampaignLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public CampaignLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean enabled) {
        this.isScrollEnabled = enabled;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }


}
