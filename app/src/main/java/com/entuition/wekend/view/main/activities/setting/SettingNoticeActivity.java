package com.entuition.wekend.view.main.activities.setting;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.common.ISimpleTaskCallback;
import com.entuition.wekend.model.data.user.NoticeInfo;
import com.entuition.wekend.model.data.user.asynctask.LoadNoticeInfoTask;
import com.entuition.wekend.view.common.WekendAbstractActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryukgoo on 15. 8. 31..
 */
public class SettingNoticeActivity extends WekendAbstractActivity {

    private final String TAG = getClass().getSimpleName();

    private List<NoticeInfo> settingDatas;

    private SettingSubPageFragment subPageFragment;
    private SettingListFragment listFragment;


    public SettingNoticeActivity() {
        this.settingDatas = new ArrayList<NoticeInfo>();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reinitialize(savedInstanceState)) return;
        setContentView(R.layout.activity_setting_help);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_setting_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleView = (TextView) findViewById(R.id.id_toolbar_title);
        titleView.setText(getString(R.string.label_setting_notice));

        listFragment = new SettingListFragment();
        listFragment.setClickListener(new OnNoticeItemClickListener());

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.id_setting_list_fragment, listFragment);
        fragmentTransaction.commit();

        loadNoticeInfos();
    }

    private void loadNoticeInfos() {
        LoadNoticeInfoTask task = new LoadNoticeInfoTask(this);
        task.setCallback(new ISimpleTaskCallback() {
            @Override
            public void onPrepare() {

            }

            @Override
            public void onSuccess(@Nullable Object object) {
                settingDatas = (List<NoticeInfo>) object;
                listFragment.setSettingDatas(settingDatas);
            }

            @Override
            public void onFailed() {

            }
        });
        task.execute("Help");
//        task.execute("Notice");
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

    public void gotoSubPage(int position) {

        NoticeInfo info = settingDatas.get(position);

        String title = info.getTitle();
        String message = info.getContent();

        // subPageFragment set Data by POSITION
        subPageFragment = new SettingSubPageFragment();
        subPageFragment.setTitle(title);
        subPageFragment.setMessage(message);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_out_right, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.id_setting_list_fragment, subPageFragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();

    }

    private class OnNoticeItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            gotoSubPage(position);
        }
    }

}
