package com.entuition.wekend.view.main.container.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.data.SharedPreferencesWrapper;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 31..
 */

public class GuidePopupViewModel extends AbstractViewModel {

    public static final String TAG = GuidePopupViewModel.class.getSimpleName();

    public final ObservableBoolean isCheckBox = new ObservableBoolean();
    public final ObservableBoolean isShowCheckbox = new ObservableBoolean();

    private final WeakReference<GuidePopupNavigator> navigator;

    public GuidePopupViewModel(Context context, GuidePopupNavigator navigator) {
        super(context);
        this.navigator = new WeakReference<GuidePopupNavigator>(navigator);
    }

    @Override
    public void onCreate() {}

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onClickCloseButton(View view) {

        Log.d(TAG, "onClickCloseButton > showNoMore : " + isCheckBox.get());

        SharedPreferencesWrapper.setShowNoMoreGuide(PreferenceManager.getDefaultSharedPreferences(getApplication()), isCheckBox.get());
        navigator.get().dismissPopup();
    }

    public void onClickCheckBox(View view) {
        isCheckBox.set(!isCheckBox.get());

        Log.d(TAG, "onClickCheckBox > showNoMore : " + isCheckBox.get());

        SharedPreferencesWrapper.setShowNoMoreGuide(PreferenceManager.getDefaultSharedPreferences(getApplication()), isCheckBox.get());
        navigator.get().dismissPopup();
    }
}
