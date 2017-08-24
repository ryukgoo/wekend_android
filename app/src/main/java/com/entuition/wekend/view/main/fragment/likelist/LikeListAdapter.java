package com.entuition.wekend.view.main.fragment.likelist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.main.fragment.SimpleViewHolder;
import com.entuition.wekend.view.util.AnimateFirstDisplayListener;
import com.entuition.wekend.view.util.MaskBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 6. 27..
 */
public class LikeListAdapter extends RecyclerSwipeAdapter<SimpleViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private DisplayImageOptions displayImageOptions;
    private SimpleViewHolder.IViewHolderListener listener;
    private List<SwipeLayout> openLayouts;

    public LikeListAdapter(Context context) {

        openLayouts = new ArrayList<SwipeLayout>();

        displayImageOptions = new DisplayImageOptions.Builder()
                .displayer(new MaskBitmapDisplayer(context.getApplicationContext(), MaskBitmapDisplayer.MASK_SMALL))
                .showImageOnLoading(R.drawable.img_bg_thumb_s_logo)
                .showImageForEmptyUri(R.drawable.img_bg_thumb_s_logo)
                .showImageOnFail(R.drawable.img_bg_thumb_s_logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    public void setOnItemClickListener(SimpleViewHolder.IViewHolderListener listener) {
        this.listener = listener;
    }

    public boolean isOpenLayout() {
        return (openLayouts != null && openLayouts.size() > 0);
    }

    public void closeAllOpenedLayout() {
        for (SwipeLayout layout : openLayouts) {
            layout.close();
        }

        openLayouts.clear();
        openLayouts = new ArrayList<SwipeLayout>();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {

        Log.d(TAG, "onCreateViewHolder > ViewType : " + ViewType);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        SimpleViewHolder simpleViewHolder = new SimpleViewHolder(view, listener);
        simpleViewHolder.swipeLayout.close();
        return simpleViewHolder;
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {

        Log.d(TAG, "onBindViewHolder > position: " + position);

        int safePosition = viewHolder.getAdapterPosition();
        LikeDBItem likeItem = LikeDBDaoImpl.getInstance().getList().get(safePosition);

        Log.d(TAG, "onBindViewHolder > productLikeTime : " + likeItem.getProductLikedTime());

        viewHolder.swipeLayout.close();
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {

            @Override
            public void onOpen(SwipeLayout layout) {
                openLayouts.add(layout);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                openLayouts.remove(layout);
            }
        });

        String imageName = likeItem.getProductId() + "/product_image_0.jpg";
        String imageUrl = S3Utils.getS3Url(Constants.PRODUCT_THUMB_BUCKET_NAME, imageName);

        ImageLoader.getInstance().displayImage(imageUrl, viewHolder.imageView, displayImageOptions, new AnimateFirstDisplayListener());

        viewHolder.title.setText(String.valueOf(likeItem.getProductTitle()));
        viewHolder.subTitle.setText(likeItem.getProductDesc());

        if (LikeDBDaoImpl.getInstance().isReadLikeItem(likeItem)) {
            viewHolder.newText.setVisibility(View.GONE);
        } else {
            viewHolder.newText.setVisibility(View.VISIBLE);
        }

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return (LikeDBDaoImpl.getInstance().getList() == null) ? 0 : LikeDBDaoImpl.getInstance().getList().size();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.id_recyclerview_swipelayout;
    }

}
