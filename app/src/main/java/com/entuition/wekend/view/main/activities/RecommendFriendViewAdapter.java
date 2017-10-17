package com.entuition.wekend.view.main.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.LikeDBItem;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.common.AnimateFirstDisplayListener;
import com.entuition.wekend.view.common.MaskBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

public class RecommendFriendViewAdapter extends RecyclerView.Adapter<RecommendFriendViewAdapter.RecommendFriendViewHolder> {

    private LayoutInflater layoutInflater;
    private ItemClickListener listener;
    private List<LikeDBItem> datas;

    private DisplayImageOptions options;

    public RecommendFriendViewAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.datas = new ArrayList<LikeDBItem>();
        this.options = new DisplayImageOptions.Builder()
                .displayer(new MaskBitmapDisplayer(context, MaskBitmapDisplayer.MASK_BIG))
                .showImageOnLoading(R.drawable.img_bg_thumb_male)
                .showImageForEmptyUri(R.drawable.img_bg_thumb_male)
                .showImageOnFail(R.drawable.img_bg_thumb_male)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .build();
    }

    public void setData(List<LikeDBItem> datas) {
        this.datas = datas;
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecommendFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.gridlist_item_recommend_friend, parent, false);
        RecommendFriendViewHolder viewHolder = new RecommendFriendViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecommendFriendViewHolder holder, int position) {

        LikeDBItem item = datas.get(position);

        holder.nameTag.setText(item.getNickname());

        if (item.isRead()) {
            holder.newIcon.setVisibility(View.GONE);
        } else {
            holder.newIcon.setVisibility(View.VISIBLE);
        }

        String photoFileName = UserInfoDaoImpl.getUploadedPhotoFileName(item.getUserId(), 0);
        String photoUrl = S3Utils.getS3Url(Constants.PROFILE_THUMB_BUCKET_NAME, photoFileName);
        ImageLoader.getInstance().displayImage(photoUrl, holder.imageView, options, new AnimateFirstDisplayListener());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class RecommendFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private ImageView newIcon;
        private TextView nameTag;

        public RecommendFriendViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.id_griditem_friend_image);
            newIcon = (ImageView) itemView.findViewById(R.id.id_griditem_friend_new);
            nameTag = (TextView) itemView.findViewById(R.id.id_griditem_friend_tag);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(v, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClicked(View view, int position);
    }
}



