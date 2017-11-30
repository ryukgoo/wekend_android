package com.entuition.wekend.view.common.adapter;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public abstract class SingleLayoutAdapter extends BaseBindingAdapter {

    private final int layoutId;

    public SingleLayoutAdapter(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return layoutId;
    }
}
