package com.entuition.wekend.view.main.container.viewmodel;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.entuition.wekend.R;
import com.entuition.wekend.util.ImageOptions;
import com.entuition.wekend.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class MainBindingAdapters {

    public static final String TAG = MainBindingAdapters.class.getSimpleName();

    @BindingAdapter("profileImageMask")
    public static void loadImage(ImageView imageView, @Nullable String userId) {
        if (userId == null) {
            imageView.setImageResource(R.drawable.img_bg_thumb_male);
        } else {
            String fileName = ImageUtils.getUploadedPhotoFileName(userId, 0);
            String imageUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_THUMB_BUCKET_NAME, fileName);
            ImageLoader.getInstance().displayImage(imageUrl, imageView, ImageOptions.PROFILE_MASKED_BIG);
        }
    }
}
