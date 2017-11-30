package com.entuition.wekend.view.common;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2017. 11. 1..
 */

public class PagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private final Context context;
    private int pageCount;
    private ImageView[] dots;

    public PagerIndicator(Context context) {
        super(context);
        this.context = context;
    }

    public PagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setPageCount(int count) {
        this.pageCount = count;

        if (getChildCount() > 0) {
            removeAllViews();
        }

        createDots();
    }

    private void createDots() {
        dots = new ImageView[pageCount];
        for (int i = 0 ; i < pageCount ; i ++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.drawable_indicator_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 0, 10, 0);

            addView(dots[i], params);
        }

        if (dots.length > 0 && dots[0] != null) {
            dots[0].setSelected(true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        for (int i = 0 ; i < pageCount ; i ++) {
            dots[i].setSelected(false);
        }

        dots[position].setSelected(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
