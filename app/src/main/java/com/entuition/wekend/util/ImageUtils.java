package com.entuition.wekend.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 3. 1..
 */
public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();

    public static final String PROFILE_IMAGE_BUCKET_NAME = "entuition-user-profile";
    public static final String PROFILE_THUMB_BUCKET_NAME = "entuition-user-profile-thumb";
    public static final String PRODUCT_IMAGE_BUCKET_NAME = "entuition-product-images";
    public static final String PRODUCT_THUMB_BUCKET_NAME = "entuition-product-images-thumb";

    private static final int IMG_WIDTH = 1024;
    private static final int IMG_HEIGHT = 1024;

    private static final String AMAZON_S3_ADDRESS = "s3-ap-northeast-1.amazonaws.com";

    private static final String JPG_FILE_FORMAT = ".jpg";
    private static final String PROFILE_IMAGE_NAME_PREFIX = "profile_image";
    private static final String PRODUCT_IMAGE_NAME_PREFIX = "product_image";

    public static Bitmap decodeFile(File file) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "decodeFile > FileNotFoundException : " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "decodeFile > Exception : " + e.getMessage());
        }
        return null;
    }

    public static Bitmap getMaskedCircleBitmap(Bitmap loadedImage, Bitmap mask) {

        int loadedImageSize = Math.min(loadedImage.getWidth(), loadedImage.getHeight());

        float scale = ((float) mask.getHeight()) / ((float)loadedImageSize);
        int dstWidth = (int) (loadedImage.getWidth() * scale);
        int dstHeight = (int) (loadedImage.getHeight() * scale);

        int dstX = (mask.getWidth() - dstWidth) / 2;
        int dstY = (mask.getHeight() - dstHeight) / 2;

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage, dstWidth, dstHeight, true);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(scaledBitmap, dstX, dstY, null);
        canvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        return result;
    }

    public static Intent getCroppedImageIntent(Context context, File output, Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        int size = resolveInfoList.size();

        if (size > 0) {
            ResolveInfo resolveInfo = resolveInfoList.get(0);
            intent.setData(uri);
            intent.putExtra("outputX", IMG_WIDTH);
            intent.putExtra("outputY", IMG_HEIGHT);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
            return intent;
        } else {
            return null;
        }
    }

    public static String getCampaignImageUrl(int id, int position) {
        String imageName = PRODUCT_IMAGE_NAME_PREFIX + "_" + position + JPG_FILE_FORMAT;
        String imagePath = id + "/" + imageName;
        return getHttpUrl(PRODUCT_IMAGE_BUCKET_NAME, imagePath);
    }

    public static String getUploadedPhotoFileName(String folderName, int position) {
        return folderName + "/" + getPhotoFileName(position);
    }

    private static String getPhotoFileName(int position) {
        return getPhotoTempName(position) + JPG_FILE_FORMAT;
    }

    private static String getPhotoTempName(int position) {
        return PROFILE_IMAGE_NAME_PREFIX + "_" + position;
    }

    public static String getHttpUrl(String bucket, String key) {
        return "https://" + bucket + "." + AMAZON_S3_ADDRESS + "/" + key;
    }
}
