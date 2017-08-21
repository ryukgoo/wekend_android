package com.entuition.wekend.view.join;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.user.asynctask.IUploadResizedImageCallback;
import com.entuition.wekend.model.data.user.asynctask.UploadResizedImageTask;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.main.ContainerActivity;
import com.entuition.wekend.view.util.ChangeProfileImageObservable;
import com.entuition.wekend.view.util.ImageUtilities;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Kim on 2015-08-17.
 */
public class InsertPhotoActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_REQ_CODE = 34;

    private static final String TAG = "InsertPhotoActivity";
    private final int ACTION_REQUEST_GALLERY = 1003;
    private final int ACTION_REQUEST_CAMERA = 1004;
    private final int ACTION_REQUEST_CROP = 1005;

    private Button btnNext;
    private ImageView imageProfile;
    private String uploadedImagePath;

    private Uri tempImageUri;
    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_photo);

        imageProfile = (ImageView) findViewById(R.id.id_image_photo);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ACTION_REQUEST_GALLERY);
            }
        });

        btnNext = (Button) findViewById(R.id.id_button_insert_photo);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertPhotoActivity.this, ContainerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(InsertPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_GALLERY :
                case ACTION_REQUEST_CAMERA :
                    tempImageUri = data.getData();
                    cropImage();
                    break;
                case ACTION_REQUEST_CROP :
                    uploadProfileIamge();
                    break;
                default:
                    break;
            }
        }
    }

    private void cropImage() {

        outputFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        int size = resolveInfoList.size();

        if (size > 0) {
            ResolveInfo resolveInfo = resolveInfoList.get(0);
            intent.setData(tempImageUri);
            intent.putExtra("outputX", 1024);
            intent.putExtra("outputY", 1024);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
            intent.setComponent(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
            startActivityForResult(intent, ACTION_REQUEST_CROP);
        } else {
            Toast.makeText(this, "자르기용 어플리케이션이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void uploadProfileIamge() {
        UploadResizedImageTask task = new UploadResizedImageTask(this);
        task.setCallback(new UploadImageCallback());
        task.setListener(new UploadListener());
        task.execute(Uri.fromFile(outputFile));

        Bitmap bitmap = ImageUtilities.decodeFile(outputFile);
        Bitmap maksedBitmap = ImageUtilities.getMaskedCircleBitmap(this, bitmap, 0);
        imageProfile.setImageBitmap(maksedBitmap);

        ChangeProfileImageObservable.getInstance().change(bitmap);
    }

    private class UploadImageCallback implements IUploadResizedImageCallback {

        @Override
        public void onSuccess(String imageUrl) {
            uploadedImagePath = imageUrl;
        }

        @Override
        public void onFailed() {

        }
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {

            Log.d(TAG, "TransferListener > onStateChange > state : " + state.toString());

            if (state == TransferState.COMPLETED) {
                if (uploadedImagePath != null) {

                    String cachedPhotoKey = S3Utils.getS3Url(Constants.PROFILE_IMAGE_BUCKET_NAME, uploadedImagePath);
                    String cachedThumbKey = S3Utils.getS3Url(Constants.PROFILE_THUMB_BUCKET_NAME, uploadedImagePath);

                    MemoryCacheUtils.removeFromCache(cachedPhotoKey, ImageLoader.getInstance().getMemoryCache());
                    DiskCacheUtils.removeFromCache(cachedPhotoKey, ImageLoader.getInstance().getDiskCache());
                    MemoryCacheUtils.removeFromCache(cachedThumbKey, ImageLoader.getInstance().getMemoryCache());
                    DiskCacheUtils.removeFromCache(cachedThumbKey, ImageLoader.getInstance().getDiskCache());

                    uploadedImagePath = null;
                }
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onError(int id, Exception ex) {

        }
    }
}