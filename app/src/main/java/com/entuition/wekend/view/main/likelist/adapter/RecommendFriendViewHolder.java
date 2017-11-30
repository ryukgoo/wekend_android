package com.entuition.wekend.view.main.likelist.adapter;

import android.support.v7.widget.RecyclerView;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.databinding.RecommendFriendItemBinding;
import com.entuition.wekend.view.main.likelist.viewmodel.RecommendFriendItemListener;
import com.entuition.wekend.view.main.likelist.viewmodel.RecommendFriendViewModel;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class RecommendFriendViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = RecommendFriendViewHolder.class.getSimpleName();

    private final RecommendFriendItemBinding binding;
    private final RecommendFriendViewModel model;

    public RecommendFriendViewHolder(RecommendFriendViewModel model, RecommendFriendItemBinding binding) {
        super(binding.getRoot());
        this.model = model;
        this.binding = binding;
    }

    public void bind(LikeInfo info) {
        binding.setInfo(info);
        binding.setListener(new RecommendFriendItemListener() {
            @Override
            public void onClickItem(LikeInfo info) {
                model.onClickItem(info);
            }
        });
        binding.executePendingBindings();
    }
}
