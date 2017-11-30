package com.entuition.wekend.view.main.setting.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.util.Log;

import com.entuition.wekend.data.SharedPreferencesWrapper;
import com.entuition.wekend.view.common.AbstractViewModel;

/**
 * Created by ryukgoo on 2017. 11. 1..
 */

public class SettingAlarmViewModel extends AbstractViewModel {

    public static final String TAG = SettingAlarmViewModel.class.getSimpleName();

    public final ObservableBoolean isAvailableAlarm = new ObservableBoolean();
    public final ObservableBoolean isAvailableVibration = new ObservableBoolean();

    private final SharedPreferences sharedPreferences;

    public SettingAlarmViewModel(Context context, SharedPreferences sharedPreferences) {
        super(context);
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onResume() {
        isAvailableAlarm.set(SharedPreferencesWrapper.getNotificationAlarm(sharedPreferences));
        isAvailableVibration.set(SharedPreferencesWrapper.getNotificationVibration(sharedPreferences));
    }

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onChangeAlarm(boolean isChecked) {
        Log.d(TAG, "onChangeAlarm > checked : " + isChecked);
        isAvailableAlarm.set(isChecked);
        SharedPreferencesWrapper.saveNotificationAlarm(sharedPreferences, isAvailableAlarm.get());
    }

    public void onChangeVibration(boolean isChecked) {
        Log.d(TAG, "onChangeVibration > checked : " + isChecked);
        isAvailableVibration.set(isChecked);
        SharedPreferencesWrapper.saveNotificationVibration(sharedPreferences, isAvailableVibration.get());
    }

}
