package com.entuition.wekend.view.main.likelist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.databinding.RecommendFriendItemBinding;
import com.entuition.wekend.view.main.likelist.viewmodel.RecommendFriendViewModel;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

public class RecommendFriendViewAdapter extends RecyclerView.Adapter<RecommendFriendViewHolder> {

    private final RecommendFriendViewModel model;
    private List<LikeInfo> friendInfos;

    public RecommendFriendViewAdapter(RecommendFriendViewModel model, List<LikeInfo> friendInfos) {
        this.model = model;
        setList(friendInfos);
    }

    @Override
    public RecommendFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecommendFriendItemBinding binding = RecommendFriendItemBinding.inflate(inflater, parent, false);
        return new RecommendFriendViewHolder(model, binding);
    }

    @Override
    public void onBindViewHolder(RecommendFriendViewHolder holder, int position) {
        holder.bind(getItemForPosition(position));
    }

    @Override
    public int getItemCount() {
        return friendInfos == null ? 0 : friendInfos.size();
    }

    public void replaceData(List<LikeInfo> infos) {
        setList(infos);
    }

    private void setList(List<LikeInfo> infos) {
        friendInfos = infos;
        notifyDataSetChanged();
    }

    private LikeInfo getItemForPosition(int position) {
        return friendInfos.get(position);
    }
}



