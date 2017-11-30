package com.entuition.wekend.view.main.setting;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.databinding.SettingListFragmentBinding;
import com.entuition.wekend.view.main.setting.adapter.SettingNoticeAdapter;
import com.entuition.wekend.view.main.setting.viewmodel.SettingNoticeViewModel;

import java.util.ArrayList;

/**
 * Created by ryukgoo on 2016. 10. 20..
 */

@SuppressLint("ValidFragment")
public class SettingListFragment extends ListFragment {

    public static final String TAG = SettingListFragment.class.getSimpleName();

    private SettingNoticeAdapter adapter;
    private SettingNoticeViewModel model;

    public static SettingListFragment newInstance(SettingNoticeViewModel model) {
        return new SettingListFragment(model);
    }

    public SettingListFragment(SettingNoticeViewModel model) {
        this.model = model;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingListFragmentBinding binding = SettingListFragmentBinding.inflate(inflater, container, false);

        // TODO : obtain viewModel

        binding.setModel(model);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SettingNoticeAdapter(model, new ArrayList<NoticeInfo>(0));
        getListView().setAdapter(adapter);
    }
}
