package com.entuition.wekend.view.main.container;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.entuition.wekend.R;
import com.entuition.wekend.databinding.GuidePopupLayoutBinding;
import com.entuition.wekend.view.main.container.viewmodel.GuidePopupNavigator;
import com.entuition.wekend.view.main.container.viewmodel.GuidePopupViewModel;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2017. 9. 13..
 */

public class GuidePopupWindow extends PopupWindow implements GuidePopupNavigator {

    public static final String TAG = GuidePopupWindow.class.getSimpleName();

    private GuidePopupViewModel model;
    private GuidePagerAdapter adapter;

    public GuidePopupWindow(Context context, int width, int height) {
        super(width, height);
        init(context);
    }

    private void init(Context context) {

        model = new GuidePopupViewModel(context, this);
        GuidePopupLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.guide_popup_layout, null, false);

        setContentView(binding.getRoot());
        binding.setModel(model);

        model.onCreate();

        adapter = new GuidePagerAdapter(context);
        binding.guidePager.setAdapter(adapter);
        binding.guidePagerIndicator.setPageCount(adapter.getCount());
        binding.guidePager.addOnPageChangeListener(binding.guidePagerIndicator);
    }

    public void show(boolean isShowCheckBox) {
        model.isShowCheckbox.set(isShowCheckBox);
        setAnimationStyle(-1);
        showAtLocation(getContentView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void dismissPopup() {
        dismiss();
    }

    private static class GuidePagerAdapter extends PagerAdapter {

        private final Context context;
        private final ArrayList<Integer> resources;

        public GuidePagerAdapter(Context context) {
            this.context = context;

            resources = new ArrayList<>();
            resources.add(R.drawable.help_01);
            resources.add(R.drawable.help_00);
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
