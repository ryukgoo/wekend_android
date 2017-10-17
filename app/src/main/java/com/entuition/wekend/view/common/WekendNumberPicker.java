package com.entuition.wekend.view.common;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by ryukgoo on 2016. 11. 7..
 */

public class WekendNumberPicker extends NumberPicker {

    public WekendNumberPicker(Context context) {
        super(context);
    }

    public WekendNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WekendNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateViews(child);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateViews(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateViews(child);
    }

    private void updateViews(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextSize(20);
            ((EditText) view).setTextColor(Color.WHITE);
        }
    }
}
