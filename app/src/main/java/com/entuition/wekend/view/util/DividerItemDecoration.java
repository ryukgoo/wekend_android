package com.entuition.wekend.view.util;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ryukgoo on 2016. 6. 27..
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;
    private boolean isShowFirstDivider = false;
    private boolean isShowLastDivider = false;

    public DividerItemDecoration(Context context, AttributeSet attrs) {
        final TypedArray arr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
        divider = arr.getDrawable(0);
        arr.recycle();
    }

    public DividerItemDecoration(Context context, AttributeSet attrs, boolean isShowFirstDivider, boolean isShowLastDivider) {
        this(context, attrs);
        this.isShowFirstDivider = isShowFirstDivider;
        this.isShowLastDivider = isShowLastDivider;
    }

    public DividerItemDecoration(Drawable divider) {
        this.divider = divider;
    }

    public DividerItemDecoration(Drawable divider, boolean isShowFirstDivider, boolean isShowLastDivider) {
        this(divider);
        this.isShowFirstDivider = isShowFirstDivider;
        this.isShowLastDivider = isShowLastDivider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (divider == null) {
            return;
        }
        if (parent.getChildPosition(view) < 1) {
            return;
        }

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.top = divider.getIntrinsicHeight();
        } else {
            outRect.left = divider.getIntrinsicWidth();
        }
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (divider == null) {
            super.onDrawOver(canvas, parent, state);
            return;
        }

        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        int size;
        int orientation = getOrientation(parent);
        int childCount = parent.getChildCount();

        if (orientation == LinearLayoutManager.VERTICAL) {
            size = divider.getIntrinsicHeight();
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
        } else {
            size = divider.getIntrinsicWidth();
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
        }

        for (int i = isShowFirstDivider ? 0 : 1 ; i < childCount ; i ++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.getTop() - params.topMargin;
                bottom = top + size;
            } else {
                left = child.getLeft() - params.leftMargin;
                right = left + size;
            }
            divider.setBounds(left, top, right, bottom);
            divider.draw(canvas);
        }
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
            return layoutManager.getOrientation();
        } else {
            throw new IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}
