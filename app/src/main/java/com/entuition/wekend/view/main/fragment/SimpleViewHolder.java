package com.entuition.wekend.view.main.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2016. 8. 10..
 */
public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {

    private final String TAG = getClass().getSimpleName();

    public LinearLayout container;
    public SwipeLayout swipeLayout;
    public ImageView imageView;
    public TextView title;
    public TextView subTitle;
    public LinearLayout deleteLayout;
    public ImageView newIcon;
    public TextView newText;
    public IViewHolderListener listener;

    public SimpleViewHolder(View itemView, IViewHolderListener listener) {
        super(itemView);

        this.listener = listener;
        this.container = (LinearLayout) itemView.findViewById(R.id.id_recyclerview_item_container);
        this.swipeLayout = (SwipeLayout) itemView.findViewById(R.id.id_recyclerview_swipelayout);
        this.imageView = (ImageView) itemView.findViewById(R.id.id_recyclerview_item_imageview);
        this.title = (TextView) itemView.findViewById(R.id.id_recyclerview_item_title);
        this.subTitle = (TextView) itemView.findViewById(R.id.id_recyclerview_item_subtitle);
        this.deleteLayout = (LinearLayout) itemView.findViewById(R.id.id_recyclerview_item_delete);
        this.newIcon = (ImageView) itemView.findViewById(R.id.id_recyclerview_item_newicon);
        this.newText = (TextView) itemView.findViewById(R.id.id_recyclerview_item_newtext);

        container.setOnTouchListener(this);
        container.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick!!@!@!@");

        final int position = getAdapterPosition();

        if (position == RecyclerView.NO_POSITION) return;

        if (v.getId() == R.id.id_recyclerview_item_delete) {
            listener.onClickDeleteButton(position, swipeLayout);
        } else {
            listener.onClickItem(position);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return listener.onTouchItem(v, event);
    }

    public interface IViewHolderListener {
        void onClickItem(int position);
        void onClickDeleteButton(int position, SwipeLayout layout);
        boolean onTouchItem(View v, MotionEvent event);
    }
}
