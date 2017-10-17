package com.entuition.wekend.view.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.entuition.wekend.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ryukgoo on 2016. 3. 1..
 */
public class ImageUtilities {

    private static final int IMG_WIDTH = 800;
    private static final int IMG_HEIGHT = 800;

    private static final String TEMP_FILE_SUFFIX = ".jpg";

    @Nullable
    public static File resizeImageFile(Context context, Uri uri, String outputPath) {

        try {
            int realWidth = 0;
            int realHeight = 0;

            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            inputStream = null;

            realWidth = options.outWidth;
            realHeight = options.outHeight;

            inputStream = context.getContentResolver().openInputStream(uri);
            options = new BitmapFactory.Options();
            options.inSampleSize = Math.max(realWidth / IMG_WIDTH, realHeight / IMG_HEIGHT);
            Bitmap roughBitmap = BitmapFactory.decodeStream(inputStream, null, options);

            Matrix matrix = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, IMG_WIDTH, IMG_HEIGHT);
            matrix.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            matrix.getValues(values);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap,
                    (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);

            File outputDir = context.getCacheDir();
            File outputFile = File.createTempFile(outputPath, TEMP_FILE_SUFFIX, outputDir);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

            return outputFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

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

        }
        return null;
    }

    public static File getTempImageFile(Context context, String outputPath) {
        File outputDir = context.getCacheDir();
        File outputFile = null;
        try {
            outputFile = File.createTempFile(outputPath, TEMP_FILE_SUFFIX, outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    private static int calculatedInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

    public static Bitmap getMaskedCircleBitmap(Context context, Bitmap loadedImage, int maskSize) {

        Bitmap mask = null;
        if (maskSize == 0) {
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_thumb_default_2);
        } else if (maskSize == 1) {
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_bg_thumb_s_2);
        } else if (maskSize == 2) {
            mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_profile_thumb_b_2);
        }

        if (mask == null) return null;

        return getMaskedCircleBitmap(loadedImage, mask);
    }
}
