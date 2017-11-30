package com.entuition.wekend.view.main.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.entuition.wekend.R;
import com.entuition.wekend.databinding.SettingAlarmActivityBinding;
import com.entuition.wekend.view.main.setting.viewmodel.SettingAlarmViewModel;

/**
 * Created by ryukgoo on 15. 8. 31..
 */
public class SettingAlarmActivity extends AppCompatActivity {

    public static final String TAG = SettingAlarmActivity.class.getSimpleName();

    private SettingAlarmActivityBinding binding;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingAlarmViewModel model = new SettingAlarmViewModel(this,
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        binding = DataBindingUtil.setContentView(this, R.layout.setting_alarm_activity);
        binding.setModel(model);

        setupToolbar();


        model.onCreate();
    }

    private void setupToolbar() {
        toolbar = binding.settingToolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    protected void onResume() {
        super.onResume();
        binding.getModel().onResume();
    }
}
