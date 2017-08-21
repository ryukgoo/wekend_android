package com.entuition.wekend.view.main.activities.setting;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.NoticeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 2016. 10. 20..
 */

public class SettingListFragment extends ListFragment {

    private final String TAG = getClass().getSimpleName();

    private SettingListAdapter adapter;
    private List<NoticeInfo> settingDatas;
    private AdapterView.OnItemClickListener clickListener;

    public SettingListFragment() {
        settingDatas = new ArrayList<NoticeInfo>();
    }

    public void setClickListener(AdapterView.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new SettingListAdapter();
        setListAdapter(adapter);

        getListView().setOnItemClickListener(clickListener);
    }

    public void setSettingDatas(List<NoticeInfo> datas) {
        settingDatas = datas;
        adapter.notifyDataSetChanged();
    }

    private class SettingListAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public SettingListAdapter() {
            this.inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return settingDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            SettingViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_setting, parent, false);

                holder = new SettingViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.id_setting_listitem_title);
                holder.subTitle = (TextView) convertView.findViewById(R.id.id_setting_listitem_subtitle);

                convertView.setTag(holder);
            } else {
                holder = (SettingViewHolder) convertView.getTag();
            }

            NoticeInfo info = settingDatas.get(position);

            holder.title.setText(info.getTitle());
            holder.subTitle.setText(info.getSubTitle());

            return convertView;
        }
    }

    private class SettingViewHolder {
        public TextView title;
        public TextView subTitle;
    }
}
