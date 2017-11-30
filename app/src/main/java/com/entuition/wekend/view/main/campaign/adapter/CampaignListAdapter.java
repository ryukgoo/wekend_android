package com.entuition.wekend.view.main.campaign.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.entuition.wekend.data.source.product.ProductInfo;
import com.entuition.wekend.databinding.CampaignListItemBinding;
import com.entuition.wekend.view.main.campaign.viewmodel.CampaignListViewModel;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class CampaignListAdapter extends RecyclerView.Adapter<CampaignItemViewHolder> {

    public static final String TAG = CampaignListAdapter.class.getSimpleName();

    private final CampaignListViewModel model;
    private List<ProductInfo> productInfos;

    public CampaignListAdapter(CampaignListViewModel model, List<ProductInfo> infos) {
        this.model = model;
        setList(infos);
    }

    @Override
    public CampaignItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CampaignListItemBinding binding = CampaignListItemBinding.inflate(inflater, parent, false);
        return new CampaignItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CampaignItemViewHolder holder, int position) {
        ProductInfo productInfo = getItemForPosition(position);
        holder.bind(productInfo, model);
    }

    @Override
    public int getItemCount() {
        return (productInfos == null ? 0 : productInfos.size());
    }

    public void replaceData(List<ProductInfo> infos) {
        setList(infos);
    }

    private void setList(List<ProductInfo> infos) {
        productInfos = infos;
        notifyDataSetChanged();
    }

    private ProductInfo getItemForPosition(int position) {
        return productInfos.get(position);
    }
}
