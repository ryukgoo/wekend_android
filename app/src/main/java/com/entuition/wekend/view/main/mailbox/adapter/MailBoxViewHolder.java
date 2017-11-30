package com.entuition.wekend.view.main.mailbox.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.databinding.MailboxItemBinding;
import com.entuition.wekend.view.main.likelist.adapter.SingleSwipeListener;
import com.entuition.wekend.view.main.mailbox.viewmodel.MailBoxListViewModel;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class MailBoxViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = MailBoxViewHolder.class.getSimpleName();

    private final MailBoxListViewModel model;
    private final MailboxItemBinding binding;
    private final SingleSwipeListener swipeListener;

    public MailBoxViewHolder(MailBoxListViewModel model, MailboxItemBinding binding, SingleSwipeListener swipeListener) {
        super(binding.getRoot());
        this.model = model;
        this.binding = binding;
        this.swipeListener = swipeListener;
    }

    public void bind(IMail mail) {
        binding.setMail(mail);

        MailBoxItemListener listener = new MailBoxItemListener() {
            @Override
            public void onClickItem(IMail mail) {
                model.onClickItem(mail);
            }

            @Override
            public void onClickDeleteItem(IMail mail) {
                model.onClickDeleteItem(mail, getAdapterPosition(), binding.swipeLayout);
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

        binding.executePendingBindings();
    }
}
