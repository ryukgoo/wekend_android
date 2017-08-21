package com.entuition.wekend.view.main.activities.setting;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.authentication.asynctask.RequestVerificationCodeTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.data.user.asynctask.ILoadUserInfoCallback;
import com.entuition.wekend.model.data.user.asynctask.IUploadResizedImageCallback;
import com.entuition.wekend.model.data.user.asynctask.LoadUserInfoTask;
import com.entuition.wekend.model.data.user.asynctask.UpdateUserInfoTask;
import com.entuition.wekend.model.data.user.asynctask.UploadResizedImageTask;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.util.BigSizeImageLoadingListener;
import com.entuition.wekend.view.util.ChangeProfileImageObservable;
import com.entuition.wekend.view.util.ImageUtilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 15. 8. 31..
 */
public class SettingEditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_REQ_CODE = 34;

    private final String TAG = getClass().getSimpleName();

    private final int ACTION_REQUEST_GALLERY = 1003;
    private final int ACTION_REQUEST_CAMERA = 1004;
    private final int ACTION_REQUEST_CROP = 1005;

    private TextView textViewNickname;
    private TextView textViewAge;
    private TextView textViewPoint;
    private TextView txtYellowBalloon;

    private EditText editTextPhoneNumber;
    private Button buttonEditPhoneNumber;
    private Button buttonRequestVerification;
    private Button buttonConfirmVerification;
    private EditText editTextVerification;
    private TextView buttonEditDone;

    private ViewPager viewPager;
    private ProfilePagerAdapter viewPagerAdapter;
    private LinearLayout pageIndicator;
    private int dotsCount = 0;
    private ImageView[] dots;

    private String userId;
    private UserInfo userInfo;
    private ArrayList<String> profilePhotoList;

    private DisplayImageOptions options;

    private Uri tempImageUri;
    private File outputFile;
    private String uploadedFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_edit_profile);

        Log.d(TAG, "onCreate!!!!");

        profilePhotoList = new ArrayList<String>();
        initViews();
        loadUserInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initViews() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_profile_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewNickname = (TextView) findViewById(R.id.id_profile_nickname);
        textViewAge = (TextView) findViewById(R.id.id_profile_age);
        textViewPoint = (TextView) findViewById(R.id.id_profile_pink_balloon);
        txtYellowBalloon = (TextView) findViewById(R.id.id_profile_yellow_balloon);

        editTextPhoneNumber = (EditText) findViewById(R.id.id_setting_profile_edittext_phone);
        editTextPhoneNumber.addTextChangedListener(new PhoneNumberTextWatcher());
        buttonEditPhoneNumber = (Button) findViewById(R.id.id_setting_profile_button_edit_phonenumber);
        buttonEditPhoneNumber.setOnClickListener(new OnClickListeners());
        editTextVerification = (EditText) findViewById(R.id.id_setting_profile_edittext_verification);
        editTextVerification.addTextChangedListener(new VerificationCodeTextWatcher());
        buttonRequestVerification = (Button) findViewById(R.id.id_setting_profile_button_request_verification);
        buttonRequestVerification.setOnClickListener(new OnClickListeners());
        buttonConfirmVerification = (Button) findViewById(R.id.id_setting_profile_button_confirm_verification);
        buttonConfirmVerification.setOnClickListener(new OnClickListeners());
        buttonEditDone = (TextView) findViewById(R.id.id_profile_edit_button);
        buttonEditDone.setText(getString(R.string.profile_edit_done));
        buttonEditDone.setOnClickListener(new OnClickListeners());

        viewPager = (ViewPager) findViewById(R.id.id_profile_viewpager);
        viewPagerAdapter = new ProfilePagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        pageIndicator = (LinearLayout) findViewById(R.id.id_profile_viewpager_indicator);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.profile_default)
                .showImageOnFail(R.drawable.profile_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void loadUserInfo() {
        userId = UserInfoDaoImpl.getInstance().getUserId(this);
        LoadUserInfoTask task = new LoadUserInfoTask();
        task.setCallback(new ILoadUserInfoCallback() {
            @Override
            public void onSuccess(UserInfo result) {
                userInfo = result;
                profilePhotoList = Utilities.asSortedArrayList(userInfo.getPhotos());
                setViews();
            }

            @Override
            public void onFailed() {
                // TODO : failed Exceptions~
            }
        });
        task.execute(userId);
    }

    private void setViews() {
        textViewNickname.setHint(userInfo.getNickname());
        editTextPhoneNumber.setHint(userInfo.getPhone());
        int birthYear = userInfo.getBirth();
        textViewAge.setText(String.valueOf(Utilities.getAgeFromBirthYear(birthYear)));

        textViewPoint.setText(getString(R.string.profile_pink_balloon) + " : " + userInfo.getBalloon());
        txtYellowBalloon.setText(getString(R.string.profile_yellow_balloon) + " : " + userInfo.getBalloon());

        viewPagerAdapter.notifyDataSetChanged();

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        if (pageIndicator.getChildCount() > 0) {
            pageIndicator.removeAllViews();
        }

        for (int i = 0 ; i < dotsCount ; i ++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.drawable_indicator_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(7, 0, 7, 0);

            pageIndicator.addView(dots[i], params);
        }

        if (dotsCount > 0) dots[0].setSelected(true);

        findViewById(R.id.id_profile_appbar).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume!!!!");
        if (ContextCompat.checkSelfPermission(SettingEditProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

                case ACTION_REQUEST_CROP:
                    uploadProfileImage();
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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

    private void uploadProfileImage() {
        UploadResizedImageTask task = new UploadResizedImageTask(this);
        task.setCallback(new UploadImageCallback());
        task.setListener(new UploadListener());
        task.execute(Uri.fromFile(outputFile));

        View view = viewPager.findViewWithTag(viewPager.getCurrentItem());
        ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);
        Bitmap bitmap = ImageUtilities.decodeFile(outputFile);
        imageView.setImageBitmap(bitmap);

        ChangeProfileImageObservable.getInstance().change(bitmap);
    }

    private void requestVerificationCode() {
        String phoneNumber = editTextPhoneNumber.getText().toString();

        RequestVerificationCodeTask task = new RequestVerificationCodeTask();
        task.setCallback(new ISimpleTaskCallback() {
            @Override
            public void onPrepare() {

            }

            @Override
            public void onSuccess(@Nullable Object object) {
                new AlertDialog.Builder(new ContextThemeWrapper(SettingEditProfileActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.confirm_verification_code))
                        .setMessage(getString(R.string.insert_phonenumber_alert_message_receive_verification_number))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailed() {
                new AlertDialog.Builder(new ContextThemeWrapper(SettingEditProfileActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.send_verification_code_failed_title))
                        .setMessage(getString(R.string.send_verification_code_failed_message))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });
        task.execute(phoneNumber);
    }

    private class ProfilePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Math.max(1, profilePhotoList.size());
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.campaign_pager_item, container, false);
            container.addView(view);

            final ImageView imageView = (ImageView) view.findViewById(R.id.image_pager_item);
            Button editButton = (Button) view.findViewById(R.id.id_pager_item_button_edit);
            editButton.setVisibility(View.VISIBLE);

            if (profilePhotoList.size() != 0 && profilePhotoList.get(position) != null) {
                String photoUrl = S3Utils.getS3Url(Constants.PROFILE_IMAGE_BUCKET_NAME, profilePhotoList.get(position));
                ImageLoader.getInstance().displayImage(photoUrl, imageView, options, new BigSizeImageLoadingListener(profilePhotoList.get(position)));
            }

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, ACTION_REQUEST_GALLERY);
                }
            });

            view.setTag(position);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {

            if (state == TransferState.COMPLETED) {

                if (uploadedFileName != null) {

                    String cachedPhotoKey = S3Utils.getS3Url(Constants.PROFILE_IMAGE_BUCKET_NAME, uploadedFileName);
                    String cachedThumbKey = S3Utils.getS3Url(Constants.PROFILE_THUMB_BUCKET_NAME, uploadedFileName);

                    MemoryCacheUtils.removeFromCache(cachedPhotoKey, ImageLoader.getInstance().getMemoryCache());
                    DiskCacheUtils.removeFromCache(cachedPhotoKey, ImageLoader.getInstance().getDiskCache());
                    MemoryCacheUtils.removeFromCache(cachedThumbKey, ImageLoader.getInstance().getMemoryCache());
                    DiskCacheUtils.removeFromCache(cachedThumbKey, ImageLoader.getInstance().getDiskCache());

                    uploadedFileName = null;
                }
            }
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

        @Override
        public void onError(int id, Exception ex) { }
    }

    private class UploadImageCallback implements IUploadResizedImageCallback {

        @Override
        public void onSuccess(String imageUrl) {
            uploadedFileName = imageUrl;
        }

        @Override
        public void onFailed() { }
    }

    private class PhoneNumberTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Utilities.isValidPhoneNumberExpression(s.toString())) {
                buttonRequestVerification.setEnabled(true);
            } else {
                buttonRequestVerification.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private class VerificationCodeTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 6) {
                buttonConfirmVerification.setEnabled(true);
            } else {
                buttonConfirmVerification.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private class OnClickListeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_profile_edit_button:
                    onBackPressed();
                    break;
                case R.id.id_setting_profile_button_edit_phonenumber:
                    editTextPhoneNumber.requestFocus();
                    break;
                case R.id.id_setting_profile_button_request_verification:
                    requestVerificationCode();
                    break;
                case R.id.id_setting_profile_button_confirm_verification:
                    String verificationCode = editTextVerification.getText().toString();
                    if (RequestVerificationCodeTask.validateVerificationCode(verificationCode)) {
                        // TODO : update user Info
                        String newPhoneNumber = editTextPhoneNumber.getText().toString();
                        userInfo.setPhone(newPhoneNumber);
                        UpdateUserInfoTask task = new UpdateUserInfoTask();
                        task.setCallback(new ISimpleTaskCallback() {
                            @Override
                            public void onPrepare() { }

                            @Override
                            public void onSuccess(@Nullable Object object) {
                                new AlertDialog.Builder(new ContextThemeWrapper(SettingEditProfileActivity.this, R.style.CustomAlertDialog))
                                        .setTitle(getString(R.string.profile_edit_phone_complete_title))
                                        .setMessage(getString(R.string.profile_edit_phone_complete_message))
                                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }

                            @Override
                            public void onFailed() {
                                new AlertDialog.Builder(new ContextThemeWrapper(SettingEditProfileActivity.this, R.style.CustomAlertDialog))
                                        .setTitle(getString(R.string.profile_edit_phone_failed_title))
                                        .setMessage(getString(R.string.profile_edit_phone_failed_message))
                                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        });
                        task.execute(userInfo);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromInputMethod(buttonEditDone.getWindowToken(), 0);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}