package com.entuition.wekend.view.main.setting.adapter;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.databinding.SettingListItemBinding;
import com.entuition.wekend.view.main.setting.viewmodel.SettingNoticeItemListener;
import com.entuition.wekend.view.main.setting.viewmodel.SettingNoticeViewModel;

import java.util.List;

/**
 * Created by ryukgoo on 2017. 11. 1..
 */

public class SettingNoticeAdapter extends BaseAdapter {

    public static final String TAG = SettingNoticeAdapter.class.getSimpleName();

    private final SettingNoticeViewModel model;
    private List<NoticeInfo> noticeInfos;

    public SettingNoticeAdapter(SettingNoticeViewModel model, List<NoticeInfo> noticeInfos) {
        this.model = model;
        setList(noticeInfos);
    }

    public void replaceData(List<NoticeInfo> infos) {
        setList(infos);
    }

    @Override
    public int getCount() {
        return noticeInfos == null ? 0 : noticeInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return noticeInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SettingListItemBinding binding;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            binding = SettingListItemBinding.inflate(inflater, parent, false);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
        }

        SettingNoticeItemListener listener = new SettingNoticeItemListener() {
            @Override
            public void onClickNoticeItem(NoticeInfo info) {
                model.onClickNoticeItem(info);
            }
        };

        binding.setInfo(noticeInfos.get(position));
        binding.setListener(listener);
        binding.executePendingBindings();

        return binding.getRoot();
    }

    private void setList(List<NoticeInfo> infos) {
        noticeInfos = infos;
        notifyDataSetChanged();
    }
}
