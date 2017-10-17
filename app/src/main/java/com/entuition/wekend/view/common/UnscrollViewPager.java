package com.entuition.wekend.view.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ryukgoo on 2016. 7. 4..
 */
public class UnscrollViewPager extends ViewPager {

    private boolean enabled;

    public UnscrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (enabled) {
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
