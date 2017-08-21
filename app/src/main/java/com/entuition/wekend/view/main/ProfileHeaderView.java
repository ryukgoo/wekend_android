package com.entuition.wekend.view.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.user.UserInfo;
import com.entuition.wekend.model.data.user.UserInfoDaoImpl;
import com.entuition.wekend.model.transfer.S3Utils;
import com.entuition.wekend.view.main.activities.setting.SettingProfileActivity;
import com.entuition.wekend.view.util.ChangeProfileImageObservable;
import com.entuition.wekend.view.util.ImageUtilities;
import com.entuition.wekend.view.util.MaskBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ryukgoo on 2016. 8. 10..
 */
public class ProfileHeaderView extends LinearLayout {

    private Context context;

    private ImageView imageView;
    private TextView textUserNickname;
    private TextView textUsername;
    private TextView textUserPoint;

    private String userId;
    private UserInfo userInfo;
    private DisplayImageOptions displayImageOptions;

    public ProfileHeaderView(Context context) {
        this(context, null);
    }

    public ProfileHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
    }

    public void init() {

        imageView = (ImageView) findViewById(R.id.id_navigationview_image);
        textUserNickname = (TextView) findViewById(R.id.id_navigationview_nickname);
        textUsername = (TextView) findViewById(R.id.id_navigationview_username);
        textUserPoint = (TextView) findViewById(R.id.id_user_point);

        displayImageOptions = new DisplayImageOptions.Builder()
                .displayer(new MaskBitmapDisplayer(context.getApplicationContext(), MaskBitmapDisplayer.MASK_BIG))
                .showImageOnLoading(R.drawable.img_bg_thumb_male)
                .showImageForEmptyUri(R.drawable.img_bg_thumb_male)
                .showImageOnFail(R.drawable.img_bg_thumb_male)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ChangeProfileImageObservable.getInstance().addObserver(new ChangeProfileImageObserver());
        ChangePointObservable.getInstance().addObserver(new ChangePointObserver());
    }

    public void setViews() {
        userId = UserInfoDaoImpl.getInstance().getUserId(context);
        userInfo = UserInfoDaoImpl.getInstance().getUserInfo(userId);

        textUserNickname.setText(userInfo.getNickname());
        textUsername.setText(userInfo.getUsername());

        textUserPoint.setText(context.getString(R.string.profile_owned_point) + " " + userInfo.getBalloon() + "P");

        String photoFileName = UserInfoDaoImpl.getUploadedPhotoFileName(userId, 0);
        String photoUrl = S3Utils.getS3Url(Constants.PROFILE_THUMB_BUCKET_NAME, photoFileName);
        ImageLoader.getInstance().displayImage(photoUrl, imageView, displayImageOptions);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileHeaderView", "click profileImageView");
                Intent intent = new Intent(context, SettingProfileActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private class ChangeProfileImageObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Bitmap bitmap = (Bitmap) data;

            Bitmap maskedBitmap = ImageUtilities.getMaskedCircleBitmap(context, bitmap, 0);
            imageView.setImageBitmap(maskedBitmap);
        }
    }

    private class ChangePointObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            int point = (int) data;
            textUserPoint.setText(context.getString(R.string.profile_owned_point) + " " + point + "P");
        }
    }
}
