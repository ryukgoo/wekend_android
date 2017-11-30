package com.entuition.wekend.view.main.setting.adapter;

import android.app.Service;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.databinding.ProfilePagerItemBinding;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 2..
 */

public class ProfileViewPagerAdapter extends PagerAdapter {

    public static final String TAG = ProfileViewPagerAdapter.class.getSimpleName();

    private final ProfileViewPagerListener listener;
    private List<String> photos;

    public ProfileViewPagerAdapter(ProfileViewPagerListener listener, List<String> photos) {
        this.listener = listener;
        this.photos = photos;
    }

    public void replaceData(List<String> photos) {
        setList(photos);
    }

    @Override
    public int getCount() {
        return photos == null ? 0 : photos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        ProfilePagerItemBinding binding = ProfilePagerItemBinding.inflate(inflater, container, false);
        binding.getRoot().setTag(position);
        container.addView(binding.getRoot());

        ProfileViewPagerListener pagerListener = new ProfileViewPagerListener() {
            @Override
            public void onClickPagerItem(String photo) {
                listener.onClickPagerItem(photo);
            }
        };

        binding.setListener(pagerListener);
        binding.setPhoto(photos == null ? null : photos.get(position));
        binding.executePendingBindings();

        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void setList(List<String> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }
}
