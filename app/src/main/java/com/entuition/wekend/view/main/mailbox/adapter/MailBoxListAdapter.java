package com.entuition.wekend.view.main.mailbox.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.entuition.wekend.R;
import com.entuition.wekend.data.source.mail.IMail;
import com.entuition.wekend.databinding.MailboxItemBinding;
import com.entuition.wekend.view.main.likelist.adapter.SingleSwipeListener;
import com.entuition.wekend.view.main.mailbox.viewmodel.MailBoxListViewModel;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public class MailBoxListAdapter extends RecyclerSwipeAdapter<MailBoxViewHolder> {

    public static final String TAG = MailBoxListAdapter.class.getSimpleName();

    private final MailBoxListViewModel model;
    private List<IMail> mails;
    private SingleSwipeListener swipeListener;

    public MailBoxListAdapter(MailBoxListViewModel model, List<IMail> mails) {
        this.model = model;
        this.swipeListener = new SingleSwipeListener();
        setList(mails);
    }

    @Override
    public MailBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MailboxItemBinding binding = MailboxItemBinding.inflate(inflater, parent, false);
        return new MailBoxViewHolder(model, binding, swipeListener);
    }

    @Override
    public void onBindViewHolder(MailBoxViewHolder viewHolder, int position) {
        viewHolder.bind(getItemForPosition(position));
    }

    @Override
    public int getItemCount() {
        return mails == null ? 0 : mails.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    public void replaceData(List<IMail> items) {
        setList(items);
    }

    public void insertItem(int position, IMail mail) {
        mails.add(position, mail);
        notifyItemInserted(position);
    }

    public void deleteItem(int position, SwipeLayout layout) {
        mails.remove(position);
        mItemManger.removeShownLayouts(layout);
        notifyItemRemoved(position);
    }

    private void setList(List<IMail> items) {
        this.mails = items;
        notifyDataSetChanged();
        closeAllItems();
    }

    private IMail getItemForPosition(int position) {
        return mails.get(position);
    }
}
