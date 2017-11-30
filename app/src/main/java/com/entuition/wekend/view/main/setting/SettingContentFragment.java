package com.entuition.wekend.view.main.setting;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.databinding.SettingContentFragmentBinding;

/**
 * Created by ryukgoo on 2016. 10. 20..
 */

public class SettingContentFragment extends Fragment {

    public static final String TAG = SettingContentFragment.class.getSimpleName();

    private static final String ARG_TITLE = "title_key";
    private static final String ARG_MESSAGE = "message_key";

    private NoticeInfo info;

    public SettingContentFragment() {}

    public static SettingContentFragment newInstance(String title, String message) {
        SettingContentFragment fragment = new SettingContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            info = new NoticeInfo();
            info.setTitle(getArguments().getString(ARG_TITLE));
            info.setContent(getArguments().getString(ARG_MESSAGE));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingContentFragmentBinding binding = SettingContentFragmentBinding.inflate(inflater, container, false);
        binding.setInfo(info);
        binding.setSelf(this);

        return binding.getRoot();
    }

    public void onBackPressed(View view) {
        getActivity().onBackPressed();
    }
}
