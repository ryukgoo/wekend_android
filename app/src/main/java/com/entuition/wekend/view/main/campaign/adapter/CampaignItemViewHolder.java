package com.entuition.wekend.view.main.campaign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.data.source.product.enums.ProductRegion;
import com.entuition.wekend.databinding.CampaignListItemBinding;
import com.entuition.wekend.view.main.campaign.viewmodel.CampaignListViewModel;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class CampaignItemViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = CampaignItemViewHolder.class.getSimpleName();

    private final Context context;
    private final CampaignListItemBinding binding;

    public CampaignItemViewHolder(CampaignListItemBinding binding) {
        super(binding.getRoot());
        this.context = binding.getRoot().getContext();
        this.binding = binding;
    }

    public void bind(ProductInfo productInfo, final CampaignListViewModel model) {
        binding.setProduct(productInfo);
        binding.setRegion(ProductRegion.getRegionString(context, productInfo.getRegion()));
        binding.setIsLiked(model.isLikedCampaign(productInfo.getId()));
        binding.setListener(new CampaignItemListener() {
            @Override
            public void onClickCampaignItem(ProductInfo info) {
                model.onClickCampaignItem(info);
            }

            @Override
            public void onClickLikeCampaign(ProductInfo info) {
                model.onClickLikeCampaign(info, getAdapterPosition());
            }
        });
        binding.executePendingBindings();
    }
}
