package com.entuition.wekend.view.common;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.entuition.wekend.R;
import com.entuition.wekend.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by ryukgoo on 2017. 7. 12..
 */

public class ThumbImageLoadingListener extends SimpleImageLoadingListener {

    public static final String TAG = ThumbImageLoadingListener.class.getSimpleName();

    private String filename;
    private DisplayImageOptions options;

    public ThumbImageLoadingListener(String filename) {
        this.filename = filename;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        if (view.getTag() != null && view.getTag().equals(filename + "/complete")) return;

        ((ImageView) view).setImageBitmap(loadedImage);
        view.setTag(filename + "/thumb");

        String photoUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_IMAGE_BUCKET_NAME, filename);
        ImageLoader.getInstance().displayImage(photoUrl, (ImageView) view, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setTag(filename + "/complete");
            }
        });
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        Log.d(TAG, "onLoadingFailed > failReason : " + failReason.getType().toString());

        String photoUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_IMAGE_BUCKET_NAME, filename);
        ImageLoader.getInstance().displayImage(photoUrl, (ImageView) view, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setTag(filename + "/complete");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ImageView) view).setImageResource(R.drawable.profile_default);
            }
        });
    }
}
