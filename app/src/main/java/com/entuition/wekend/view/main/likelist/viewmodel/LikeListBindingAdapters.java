package com.entuition.wekend.view.main.likelist.viewmodel;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.entuition.wekend.data.source.like.LikeInfo;
import com.entuition.wekend.util.ImageOptions;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.view.common.AnimateFirstDisplayListener;
import com.entuition.wekend.view.main.likelist.adapter.LikeListAdapter;
import com.entuition.wekend.view.main.likelist.adapter.RecommendFriendViewAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 7..
 */

public class LikeListBindingAdapters {

    public static final String TAG = LikeListBindingAdapters.class.getSimpleName();

    @BindingAdapter("likeListImage")
    public static void setLikeListImage(ImageView view, int productId) {
        String imageName = productId + "/product_image_0.jpg";
        String imageUrl = ImageUtils.getHttpUrl(ImageUtils.PRODUCT_THUMB_BUCKET_NAME, imageName);
        ImageLoader.getInstance().displayImage(imageUrl, view, ImageOptions.CAMPAIGN_SMALL_CIRCLE, new AnimateFirstDisplayListener());
    }

    @BindingAdapter("likeCampaignDesc")
    public static void setCampaignDesc(TextView view, String description) {
        if (description != null) {
            view.setText(Html.fromHtml(description));
        }
    }

    @BindingAdapter("refreshLikeInfos")
    public static void refreshLikeInfoList(RecyclerView view, List<LikeInfo> items) {
        LikeListAdapter adapter = (LikeListAdapter) view.getAdapter();
        if (adapter != null) {
            List<LikeInfo> newItems = new ArrayList<>(items);
            adapter.replaceData(newItems);
            adapter.closeAllItems();
        }
    }

    @BindingAdapter("refreshFriends")
    public static void refreshFriendInfos(RecyclerView view, List<LikeInfo> items) {
        RecommendFriendViewAdapter adapter = (RecommendFriendViewAdapter) view.getAdapter();
        if (adapter != null) {
            List<LikeInfo> newItems = new ArrayList<>(items);
            adapter.replaceData(newItems);
        }
    }

    @BindingAdapter("friendProfileImage")
    public static void loadFriendProfileImage(ImageView view, String userId) {

        ImageView imageView = (ImageView) view;
        imageView.setImageBitmap(null);

        String photoFileName = ImageUtils.getUploadedPhotoFileName(userId, 0);
        String photoUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_THUMB_BUCKET_NAME, photoFileName);

        ImageLoader.getInstance().displayImage(photoUrl, view, ImageOptions.FRIEND_THUMB_CIRCLE_GRID, new AnimateFirstDisplayListener());

//        Bitmap loadedBitmap = ImageLoader.getInstance().loadImageSync(photoUrl);
//
//        if (loadedBitmap == null) {
//            ImageLoader.getInstance().displayImage(photoUrl, view, ImageOptions.FRIEND_THUMB_CIRCLE_GRID, new AnimateFirstDisplayListener());
//        } else {
//            ImageLoader.getInstance().displayImage(photoUrl, view, ImageOptions.FRIEND_THUMB_CIRCLE_GRID);
//        }
    }
}
