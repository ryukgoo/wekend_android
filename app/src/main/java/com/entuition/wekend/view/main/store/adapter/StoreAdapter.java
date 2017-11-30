package com.entuition.wekend.view.main.store.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.entuition.wekend.data.google.billing.SkuDetails;
import com.entuition.wekend.databinding.StoreItemBinding;
import com.entuition.wekend.view.main.store.viewmodel.StoreViewModel;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 8. 31..
 */
public class StoreAdapter extends RecyclerView.Adapter<StoreViewHolder> {

    public static final String TAG = StoreAdapter.class.getSimpleName();

    private List<SkuDetails> skuDetails;
    private final StoreViewModel model;

    public StoreAdapter(StoreViewModel model, List<SkuDetails> skuDetails) {
        this.model = model;
        setList(skuDetails);
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        StoreItemBinding binding = StoreItemBinding.inflate(inflater, parent, false);
        return new StoreViewHolder(binding, model);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        SkuDetails skuDetail = getItemForPosition(position);
        holder.bind(skuDetail);
    }

    @Override
    public int getItemCount() {
        return skuDetails == null ? 0 : skuDetails.size();
    }

    public void replaceData(List<SkuDetails> skuDetails) {
        setList(skuDetails);
    }

    private SkuDetails getItemForPosition(int position) {
        return skuDetails.get(position);
    }

    private void setList(List<SkuDetails> skuDetails) {
        this.skuDetails = skuDetails;
        notifyDataSetChanged();
    }
}
