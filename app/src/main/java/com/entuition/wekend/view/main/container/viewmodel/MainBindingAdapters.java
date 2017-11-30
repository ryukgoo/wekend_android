package com.entuition.wekend.view.main.container.viewmodel;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.entuition.wekend.R;
import com.entuition.wekend.util.ImageOptions;
import com.entuition.wekend.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ryukgoo on 2017. 11. 6..
 */

public class MainBindingAdapters {

    public static final String TAG = MainBindingAdapters.class.getSimpleName();

    @BindingAdapter("profileImageMask")
    public static void loadImage(ImageView imageView, Set<String> photos) {
        if (photos == null || photos.size() == 0) {
            imageView.setImageResource(R.drawable.img_bg_thumb_male);
            return;
        }
        List<String> photoArr = new ArrayList<>(photos);
        String imageUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_THUMB_BUCKET_NAME, photoArr.get(0));
        ImageLoader.getInstance().displayImage(imageUrl, imageView, ImageOptions.PROFILE_MASKED_BIG);
    }
}
