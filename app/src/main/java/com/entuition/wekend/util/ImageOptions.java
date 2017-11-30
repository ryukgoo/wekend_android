package com.entuition.wekend.util;

import android.graphics.Bitmap;

import com.entuition.wekend.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by ryukgoo on 2017. 10. 31..
 */

public class ImageOptions {

    public static DisplayImageOptions PROFILE_MASKED_BIG = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000, 20))
            .showImageOnFail(R.drawable.img_bg_thumb_male)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true)
            .cacheOnDisk(false)
            .build();

    public static DisplayImageOptions PROFILE_DEFAULT = new DisplayImageOptions.Builder()
            .showImageOnFail(R.drawable.profile_default)
            .cacheInMemory(true)
            .cacheOnDisk(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions CAMPAIGN_LIST_ITEM = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_default_logo_gray)
            .showImageForEmptyUri(R.drawable.img_default_logo_gray)
            .showImageOnFail(R.drawable.img_default_logo_gray)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions CAMPAIGN_SMALL_CIRCLE = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000, 12))
            .showImageOnFail(R.drawable.img_bg_thumb_s_logo)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    public static DisplayImageOptions FRIEND_THUMB_CIRCLE_GRID = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000, 20))
            .showImageOnFail(R.drawable.img_bg_thumb_male)
            .cacheInMemory(true)
            .cacheOnDisk(false)
            .build();

    public static DisplayImageOptions FRIEND_THUMB_CIRCLE_LIST = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000, 12))
            .showImageOnFail(R.drawable.img_bg_thumb_male)
            .cacheInMemory(true)
            .cacheOnDisk(false)
            .build();
}
