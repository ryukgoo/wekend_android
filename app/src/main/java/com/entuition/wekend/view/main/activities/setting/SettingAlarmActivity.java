package com.entuition.wekend.view.main.activities.setting;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.entuition.wekend.R;
import com.entuition.wekend.model.data.SharedPreferencesWrapper;
import com.entuition.wekend.view.common.WekendAbstractActivity;

/**
 * Created by ryukgoo on 15. 8. 31..
 */
public class SettingAlarmActivity extends WekendAbstractActivity {

    private final String TAG = getClass().getSimpleName();

    private SwitchCompat switchAlarm;
    private SwitchCompat switchVibration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (reinitialize(savedInstanceState)) return;
        setContentView(R.layout.activity_setting_alarm);

        initView();
    }

    private void initView() {

        final Toolbar toolbar = (Toolbar) findViewById(R.id.id_setting_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) findViewById(R.id.id_toolbar_title);
        title.setText(getString(R.string.label_setting_alarm));

        switchAlarm = (SwitchCompat) findViewById(R.id.id_switch_setting_alarm);
        switchVibration = (SwitchCompat) findViewById(R.id.id_switch_setting_vibration);

        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged > isChecked : " + isChecked);

                int onOff = (isChecked) ? 1 : 0;
                SharedPreferencesWrapper.saveNotificationAlarm(
                        PreferenceManager.getDefaultSharedPreferences(SettingAlarmActivity.this.getApplicationContext()), onOff);
            }
        });

        switchVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged > isChecked : " + isChecked);

                int onOff = (isChecked) ? 1 : 0;
                SharedPreferencesWrapper.saveNotificationVibration(
                        PreferenceManager.getDefaultSharedPreferences(SettingAlarmActivity.this.getApplicationContext()), onOff);
            }
        });
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

        int alarmOnOff = SharedPreferencesWrapper.getNotificationAlarm(
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()));
        int vibrationOnOff = SharedPreferencesWrapper.getNotificationVibration(
                PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()));

        if (alarmOnOff == 1) {
            switchAlarm.setChecked(true);
        } else {
            switchAlarm.setChecked(false);
        }

        if (vibrationOnOff == 1) {
            switchVibration.setChecked(true);
        } else {
            switchVibration.setChecked(false);
        }
    }
}
