package com.entuition.wekend.view.main.fragment.campaign;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.like.LikeDBDaoImpl;
import com.entuition.wekend.model.data.product.ProductDaoImpl;
import com.entuition.wekend.model.data.product.ProductInfo;
import com.entuition.wekend.model.data.product.enums.ProductRegion;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.util.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

/**
 * Created by ryukgoo on 2016. 7. 20..
 */
public class CampaignRecyclerViewAdapter extends RecyclerView.Adapter<CampaignRecyclerViewAdapter.CampaignViewHolder> {

    private final String TAG = getClass().getSimpleName();

    private Context context;

    private OnItemClickListener listener;
    private DisplayImageOptions imageOptions;

    private Map<Integer, Integer> regionStringMap;

    private boolean isClickEnabled = true;

    public CampaignRecyclerViewAdapter(Context context) {
        this.context = context;
        this.imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_default_logo_gray)
                .showImageForEmptyUri(R.drawable.img_default_logo_gray)
                .showImageOnFail(R.drawable.img_default_logo_gray)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        regionStringMap = ProductRegion.getStringResourceMap();
    }

    @Override
    public CampaignViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.campaign_list_item, parent, false);

        return new CampaignViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CampaignViewHolder holder, int position) {
        ProductInfo productInfo = ProductDaoImpl.getInstance().getProductList().get(position);

        try {
            String regionName = context.getString(regionStringMap.get(productInfo.getRegion()));
            String title = "[" + regionName + "] " + productInfo.getTitleKor();
            holder.textTitle.setText(title);
        } catch (NullPointerException e) {
            holder.textTitle.setText("[지역정보없음] " + productInfo.getTitleKor());
        }

        holder.textSubTitle.setText(productInfo.getSubTitle());

        int likeCount = productInfo.getLikeCount() / Constants.LIKE_COUNT_DELIMETER;
        holder.button.setText(String.valueOf(likeCount));

//        if (productInfo.isLike()) {
        if (LikeDBDaoImpl.getInstance().hasLikeProduct(productInfo.getId())) {
//            holder.button.setBackgroundResource(R.drawable.img_heart_s);
            holder.button.setSelected(true);
        } else {
//            holder.button.setBackgroundResource(R.drawable.img_heart_n);
            holder.button.setSelected(false);
        }

        String imageName = Constants.PRODUCT_IMAGE_NAME_PREFIX + "_0" + Constants.JPG_FILE_FORMAT;
        String imagePath = productInfo.getId() + "/" + imageName;
        String imageUrl = S3Utils.getS3Url(Constants.PRODUCT_IMAGE_BUCKET_NAME, imagePath);

        ImageLoader.getInstance().displayImage(imageUrl, holder.imageView, imageOptions, new AnimateFirstDisplayListener());
    }

    @Override
    public int getItemCount() {
        return ProductDaoImpl.getInstance().getProductList().size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setClickEnabled(boolean enabled) {
        this.isClickEnabled = enabled;
    }

    public class CampaignViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView button;
        public TextView textTitle;
        public TextView textSubTitle;

        public CampaignViewHolder(View itemView) {
            super(itemView);

            this.imageView = (ImageView) itemView.findViewById(R.id.id_campaign_list_image);
            this.button = (TextView) itemView.findViewById(R.id.id_campaign_list_like_button);
            this.textTitle = (TextView) itemView.findViewById(R.id.id_campaign_list_title);
            this.textSubTitle = (TextView) itemView.findViewById(R.id.id_campaign_list_subtitle);

            itemView.setOnClickListener(this);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (!isClickEnabled) return;

            final int position = getAdapterPosition();

            if (v.getId() == R.id.id_campaign_list_like_button) {
                listener.onItemLikeClicked(position);
            } else {
                listener.onItemClicked(position);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
        void onItemLikeClicked(int position);
    }

}
