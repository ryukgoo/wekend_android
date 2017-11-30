package com.entuition.wekend.view.main.setting.viewmodel;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.util.ImageOptions;
import com.entuition.wekend.util.ImageUtils;
import com.entuition.wekend.util.Utilities;
import com.entuition.wekend.view.common.BigSizeImageLoadingListener;
import com.entuition.wekend.view.common.PagerIndicator;
import com.entuition.wekend.view.main.setting.adapter.ProfileViewPagerAdapter;
import com.entuition.wekend.view.main.setting.adapter.SettingNoticeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 2..
 * {@link BindingAdapter}
 */

public class SettingBindingAdapters {

    public static final String TAG = SettingBindingAdapters.class.getSimpleName();

    /**
     * set profile user's profile images to viewPager
     * @param viewPager
     * @param photos
     */
    @BindingAdapter("profileViewPager")
    public static void setPhotos(ViewPager viewPager, List<String> photos) {
        ProfileViewPagerAdapter adapter = (ProfileViewPagerAdapter) viewPager.getAdapter();
        if (adapter != null) {
            List<String> items = new ArrayList<>();
            items.addAll(photos);
            adapter.replaceData(items);
        }
    }

    /**
     * set profile image size to indicator
     * @param indicator
     * @param photos
     */
    @BindingAdapter("profilePagerIndicator")
    public static void setIndicator(PagerIndicator indicator, List<String> photos) {
        indicator.setPageCount(photos == null ? 0 : photos.size());
    }


    /**
     * profile view pager item
     * @param view
     * @param photo
     */
    @BindingAdapter("profileImageDefault")
    public static void loadImage(ImageView view, @Nullable String photo) {
        Log.d(TAG, "loadImage > photo : " + photo);
        if (photo == null) {
            view.setImageResource(R.drawable.profile_default);
        } else {
            String imageUrl = ImageUtils.getHttpUrl(ImageUtils.PROFILE_IMAGE_BUCKET_NAME, photo);
            ImageLoader.getInstance().displayImage(imageUrl, view, ImageOptions.PROFILE_DEFAULT, new BigSizeImageLoadingListener(photo));
        }
    }

    /**
     * set age from birth
     * @param view
     * @param birth
     */
    @BindingAdapter("profileAge")
    public static void setAge(TextView view, int birth) {
        if (birth < 1900) return;
        view.setText(Utilities.getAgeFromBirthYear(birth));
    }

    /**
     * set noticeInfo to Adapter
     * @param listView
     * @param items
     */
    @BindingAdapter("noticeInfos")
    public static void setItems(ListView listView, List<NoticeInfo> items) {
        SettingNoticeAdapter adapter = (SettingNoticeAdapter) listView.getAdapter();
        if (adapter != null) {
            adapter.replaceData(items);
        }
    }
}
