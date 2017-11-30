package com.entuition.wekend.view.main.likelist.adapter;

import android.view.MotionEvent;
import android.view.View;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public class SingleSwipeListener extends SimpleSwipeListener implements View.OnTouchListener {

    private List<SwipeLayout> openedLayout;

    public SingleSwipeListener() {
        this.openedLayout = new ArrayList<>();
    }

    @Override
    public void onOpen(SwipeLayout layout) {
        openedLayout.add(layout);
    }

    @Override
    public void onClose(SwipeLayout layout) {
        openedLayout.remove(layout);
    }

    public void closeAll() {
        for (SwipeLayout layout : openedLayout) {
            layout.close();
        }

        openedLayout.clear();
        openedLayout = new ArrayList<>();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (openedLayout != null && openedLayout.size() > 0) {
                closeAll();
                return true;
            }
        }

        return false;
    }
}
