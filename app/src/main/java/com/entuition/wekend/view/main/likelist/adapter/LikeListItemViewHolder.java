package com.entuition.wekend.view.main.likelist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.databinding.LikeListItemBinding;
import com.entuition.wekend.view.main.likelist.viewmodel.LikeListViewModel;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public class LikeListItemViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = LikeListItemViewHolder.class.getSimpleName();

    private final LikeListItemBinding binding;
    private final LikeListViewModel model;
    private final SingleSwipeListener swipeListener;

    LikeListItemViewHolder(LikeListViewModel model, LikeListItemBinding binding, SingleSwipeListener swipeListener) {
        super(binding.getRoot());
        this.model = model;
        this.binding = binding;
        this.swipeListener = swipeListener;
    }

    public void bind(LikeInfo likeInfo) {
        binding.setLikeInfo(likeInfo);

        LikeListItemListener listener = new LikeListItemListener() {
            @Override
            public void onClickItem(LikeInfo info) {
                model.onClickItem(info);
            }

            @Override
            public void onClickDeleteItem(LikeInfo info) {
                model.onClickDeleteItem(info, getAdapterPosition(), binding.swipeLayout);
            }
        };
        binding.setListener(listener);

        binding.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                swipeListener.onOpen(layout);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                swipeListener.onClose(layout);
            }
        });

        binding.itemContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return swipeListener.onTouch(v, event);
            }
        });

        binding.setIsNew(model.isNewLike(likeInfo));

        binding.executePendingBindings();
    }
}
