package com.entuition.wekend.view.main.store.adapter;

import android.support.v7.widget.RecyclerView;

import com.entuition.wekend.data.google.billing.SkuDetails;
import com.entuition.wekend.databinding.StoreItemBinding;
import com.entuition.wekend.view.main.store.viewmodel.StoreViewModel;

/**
 * Created by ryukgoo on 2017. 11. 17..
 */
public class StoreViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = StoreViewHolder.class.getSimpleName();

    private final StoreItemBinding binding;
    private final StoreViewModel model;

    StoreViewHolder(StoreItemBinding binding, StoreViewModel model) {
        super(binding.getRoot());
        this.binding = binding;
        this.model = model;
    }

    public void bind(SkuDetails skuDetail) {
        binding.setSkuDetail(skuDetail);
        binding.setBonus(model.getBonusForSku(skuDetail.getSku()));
        binding.setListener(new StoreItemListener() {
            @Override
            public void onClickItem() {
                model.onClickItem(getAdapterPosition());
            }
        });
        binding.executePendingBindings();
    }
}
