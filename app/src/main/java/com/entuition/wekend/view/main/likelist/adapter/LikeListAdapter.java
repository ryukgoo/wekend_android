package com.entuition.wekend.view.main.likelist.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.entuition.wekend.R;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.databinding.LikeListItemBinding;
import com.entuition.wekend.view.main.likelist.viewmodel.LikeListViewModel;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 6. 27..
 */
public class LikeListAdapter extends RecyclerSwipeAdapter<LikeListItemViewHolder> {

    public static final String TAG = LikeListAdapter.class.getSimpleName();

    private final LikeListViewModel model;
    private List<LikeInfo> likeInfos;
    private SingleSwipeListener swipeListener;

    public LikeListAdapter(LikeListViewModel model, List<LikeInfo> infos) {
        this.model = model;
        this.swipeListener = new SingleSwipeListener();
        setList(infos);
    }

    @Override
    public LikeListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LikeListItemBinding binding = LikeListItemBinding.inflate(inflater, parent, false);
        return new LikeListItemViewHolder(model, binding, swipeListener);
    }

    @Override
    public void onBindViewHolder(LikeListItemViewHolder viewHolder, int position) {
        LikeInfo likeInfo = getItemForPosition(position);
        viewHolder.bind(likeInfo);
    }

    @Override
    public int getItemCount() {
        return likeInfos == null ? 0 : likeInfos.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    public void replaceData(List<LikeInfo> datas) {
        setList(datas);
    }

    public void insertItem(int position, LikeInfo info) {
        likeInfos.add(position, info);
        notifyItemInserted(position);
    }

    public void deleteItem(int position) {
        likeInfos.remove(position);
        notifyItemRemoved(position);
    }

    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    private void setList(List<LikeInfo> infos) {
        likeInfos = infos;
        notifyDataSetChanged();
        closeAllItems();
    }

    private LikeInfo getItemForPosition(int position) {
        return likeInfos.get(position);
    }
}
