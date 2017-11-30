package com.entuition.wekend.view.main.campaign.adapter;

import android.app.Service;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.databinding.CampaignPagerItemBinding;

/**
 * Created by ryukgoo on 2017. 11. 12..
 */

public class CampaignDetailAdapter extends PagerAdapter {

    public static final String TAG = CampaignDetailAdapter.class.getSimpleName();

    private ProductInfo productInfo;

    public CampaignDetailAdapter(ProductInfo info) {
        this.productInfo = info;
    }

    public void replaceData(ProductInfo info) {
        this.productInfo = info;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return productInfo == null ? 0 : productInfo.getImageCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        CampaignPagerItemBinding binding = CampaignPagerItemBinding.inflate(inflater, container, false);
        binding.getRoot().setTag(position);
        container.addView(binding.getRoot());

        binding.setProductInfo(productInfo);
        binding.setPosition(position);
        binding.executePendingBindings();

        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
