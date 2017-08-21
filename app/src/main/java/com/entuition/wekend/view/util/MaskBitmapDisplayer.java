package com.entuition.wekend.view.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.entuition.wekend.R;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by ryukgoo on 2016. 8. 9..
 */
public class MaskBitmapDisplayer implements BitmapDisplayer {

    public static final int MASK_BIG = 0;
    public static final int MASK_SMALL = 1;
    public static final int MASK_BIG_PROFILE = 2;

    private final Bitmap bitmapMask;

    public MaskBitmapDisplayer(Context context, int maskSize) {
        if (maskSize == MASK_BIG) {
            bitmapMask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_thumb_default_2);
        } else if (maskSize == MASK_SMALL) {
            bitmapMask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_thumb_s_2);
        } else {
            bitmapMask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_profile_thumb_b_2);
        }
    }

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        imageAware.setImageBitmap(getMaskCircleBitmap(bitmap));
    }

    private Bitmap getMaskCircleBitmap(Bitmap loadedImage) {
        return ImageUtilities.getMaskedCircleBitmap(loadedImage, bitmapMask);
    }
}
