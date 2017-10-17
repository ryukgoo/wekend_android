package com.entuition.wekend.view.common;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.transfer.S3Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 7. 12..
 */

public class BigSizeImageLoadingListener extends SimpleImageLoadingListener {

    private final String TAG = getClass().getSimpleName();

    private String filename;
    private DisplayImageOptions options;

    public BigSizeImageLoadingListener(String filename) {
        this.filename = filename;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

        List<Bitmap> cachedMemoryBitmaps = MemoryCacheUtils.findCachedBitmapsForImageUri(imageUri, ImageLoader.getInstance().getMemoryCache());

        if (cachedMemoryBitmaps == null || cachedMemoryBitmaps.size() == 0) {

            Log.d(TAG, "onLoadingStarted > imaegUri : " + imageUri);

            String photoUrl = S3Utils.getS3Url(Constants.PROFILE_THUMB_BUCKET_NAME, filename);
            ImageLoader.getInstance().displayImage(photoUrl, (ImageView) view, options, new ThumbImageLoadingListener(filename));
        } else {
            ((ImageView) view).setImageBitmap(cachedMemoryBitmaps.get(0));
            view.setTag(filename + "/complete");
        }
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        ((ImageView) view).setImageResource(R.drawable.profile_default);
    }
}
