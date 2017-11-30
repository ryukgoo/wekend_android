package com.entuition.wekend.view.main.campaign.viewmodel;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.enums.IProductEnum;
import com.entuition.wekend.util.ImageOptions;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.common.AnimateFirstDisplayListener;
import com.entuition.wekend.view.common.PagerIndicator;
import com.entuition.wekend.view.main.campaign.adapter.CampaignDetailAdapter;
import com.entuition.wekend.view.main.campaign.adapter.CampaignLayoutManager;
import com.entuition.wekend.view.main.campaign.adapter.CampaignListAdapter;
import com.entuition.wekend.view.main.campaign.adapter.FilterSpinnerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class CampaignBindingAdapters {

    public static final String TAG = CampaignBindingAdapters.class.getSimpleName();

    @BindingAdapter("refreshProducts")
    public static void refreshProductInfos(RecyclerView recyclerView, List<ProductInfo> items) {
        CampaignListAdapter adapter = (CampaignListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }

    @BindingAdapter("campaignListImage")
    public static void setCampaignListImage(ImageView view, int id) {
        String imageUrl = ImageUtils.getCampaignImageUrl(id, 0);
        ImageLoader.getInstance().displayImage(imageUrl, view, ImageOptions.CAMPAIGN_LIST_ITEM, new AnimateFirstDisplayListener());
    }

    @BindingAdapter("campaignListLike")
    public static void setCampaignListLike(TextView view, int count) {
        int likeCount = count / ProductInfo.LIKE_COUNT_DELIMETER;
        view.setText(String.valueOf(likeCount));
    }

    @BindingAdapter("recyclerEnable")
    public static void setRecyclerViewEnable(RecyclerView view, boolean isEnable) {
        CampaignLayoutManager layoutManager = (CampaignLayoutManager) view.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.setScrollEnabled(isEnable);
        }
    }

    @BindingAdapter("spinnerDatas")
    public static void setSpinnerDatas(AppCompatSpinner spinner, List<IProductEnum> datas) {

        FilterSpinnerAdapter adapter = (FilterSpinnerAdapter) spinner.getAdapter();
        Log.d(TAG, "adapter : " + adapter);
        if (adapter != null) {
            adapter.clear();
            adapter.setList(datas);
            adapter.notifyDataSetChanged();
            spinner.setSelection(0);
        }
    }

    @BindingAdapter("optionFilterOpen")
    public static void openOptionFilter(SlidingDrawer drawer, boolean isOpen) {
        if (drawer.isOpened() && !isOpen) {
            drawer.animateClose();
        } else if (!drawer.isOpened() && isOpen) {
            drawer.animateOpen();
        }
    }

    @BindingAdapter("campaignDetailDesc")
    public static void setCampaignDetailDescription(TextView view, ProductInfo info) {

        if (info == null) return;

        Context context = view.getContext();
        String newDescription = info.getDescription();

        if (info.getPrice() != null) {
            newDescription = newDescription + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE;
            newDescription = newDescription + context.getString(R.string.campaign_price, info.getPrice());
        }

        if (info.getParking() != null) {
            newDescription = newDescription + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE;
            newDescription = newDescription + context.getString(R.string.campaign_parking, info.getParking());
        }

        if (info.getOperationTime() != null) {
            newDescription = newDescription + Utilities.HTML_NEW_LINE + Utilities.HTML_NEW_LINE;
            newDescription = newDescription + context.getString(R.string.campaign_operation_time, info.getOperationTime());
        }

        view.setText(Html.fromHtml(newDescription));
    }

    @BindingAdapter("productViewPager")
    public static void setProductImages(ViewPager viewPager, ProductInfo info) {
        CampaignDetailAdapter adapter = (CampaignDetailAdapter) viewPager.getAdapter();
        if (adapter != null) {
            adapter.replaceData(info);
        }
    }

    @BindingAdapter("productPagerIndicator")
    public static void setIndicator(PagerIndicator indicator, int imageCount) {
        indicator.setPageCount(imageCount);
    }

    @BindingAdapter({"productImageDefault", "productImagePosition"})
    public static void loadImage(ImageView view, ProductInfo info, int position) {
        String imageUrl = ImageUtils.getCampaignImageUrl(info.getId(), position);
        ImageLoader.getInstance().displayImage(imageUrl, view, ImageOptions.CAMPAIGN_LIST_ITEM, new AnimateFirstDisplayListener());
    }
}
