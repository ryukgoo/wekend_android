package com.entuition.wekend.view.main;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.SharedPreferencesWrapper;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2017. 9. 13..
 */

public class GuidePopupWindow extends PopupWindow implements View.OnClickListener {

    private Context context;
    private ViewPager viewPager;
    private Button closeButton;
    private CheckBox checkBox;
    private TextView noMoreShowText;

    public GuidePopupWindow(Context context, int width, int height) {
        super(width, height);
        init(context);
    }

    private void init(Context context) {

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.layout_popupwindow_guide, null);

        setContentView(contentView);

        viewPager = (ViewPager) getContentView().findViewById(R.id.id_pagerview_guide);
        viewPager.setAdapter(new GuidePagerAdapter());
        closeButton = (Button) getContentView().findViewById(R.id.id_button_close_guide);
        closeButton.setOnClickListener(this);
        checkBox = (CheckBox) getContentView().findViewById(R.id.id_checkbox_guide_hide);
        checkBox.setOnClickListener(this);
        noMoreShowText = (TextView) getContentView().findViewById(R.id.id_textview_guide_no_more_show);
        noMoreShowText.setOnClickListener(this);
    }

    public void show(boolean isShowCheckBox) {

        if (isShowCheckBox) {
            getContentView().findViewById(R.id.id_checkbox_layout_guide).setVisibility(View.VISIBLE);
        } else {
            getContentView().findViewById(R.id.id_checkbox_layout_guide).setVisibility(View.GONE);
        }

        setAnimationStyle(-1);
        showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_checkbox_guide_hide :
            case R.id.id_textview_guide_no_more_show:
                SharedPreferencesWrapper.setShowNoMoreGuide(PreferenceManager.getDefaultSharedPreferences(context), true);
                break;
            case R.id.id_button_close_guide:
                break;
            default:
                break;
        }

        dismiss();
    }

    private class GuidePagerAdapter extends PagerAdapter {

        private ArrayList<Integer> resources;

        public GuidePagerAdapter() {
            resources = new ArrayList<>();
            resources.add(R.drawable.help_00);
            resources.add(R.drawable.help_01);
            resources.add(R.drawable.help_02);
            resources.add(R.drawable.help_03);
            resources.add(R.drawable.help_04);
            resources.add(R.drawable.help_05);
        }

        @Override
        public int getCount() {
            return resources.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(resources.get(position));

            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
