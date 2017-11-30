package com.entuition.wekend.view.main.setting;

import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.data.source.noticeinfo.NoticeInfo;
import com.entuition.wekend.data.source.noticeinfo.NoticeInfoRepository;
import com.entuition.wekend.databinding.SettingNoticeActivityBinding;
import com.entuition.wekend.view.main.setting.viewmodel.SettingNoticeNavigator;
import com.entuition.wekend.view.main.setting.viewmodel.SettingNoticeViewModel;

/**
 * Created by ryukgoo on 15. 8. 31..
 */
public class SettingNoticeActivity extends AppCompatActivity implements SettingNoticeNavigator {

    public static final String TAG = SettingNoticeActivity.class.getSimpleName();

    private SettingNoticeActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingNoticeViewModel model = new SettingNoticeViewModel(this, this, NoticeInfoRepository.getInstance());
        binding = DataBindingUtil.setContentView(this, R.layout.setting_notice_activity);

        binding.setModel(model);

        setupToolbar();
        setupFragment();

        model.onCreate();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.settingToolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setupFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.id_setting_list_fragment, SettingListFragment.newInstance(binding.getModel()));
        fragmentTransaction.commit();
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

    @Override
    public void showDetail(NoticeInfo info) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                R.animator.slide_out_right, R.animator.slide_in_right);
        fragmentTransaction.replace(R.id.id_setting_list_fragment, SettingContentFragment.newInstance(info.getTitle(), info.getContent()));
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }
}
