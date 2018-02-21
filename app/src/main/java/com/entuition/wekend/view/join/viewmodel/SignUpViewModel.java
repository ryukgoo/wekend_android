package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 30..
 */

public class SignUpViewModel extends AbstractViewModel {

    private static final String TAG = SignUpViewModel.class.getSimpleName();

    private final WeakReference<SignUpViewNavigator> navigator;

    public SignUpViewModel(Context context, SignUpViewNavigator navigator) {
        super(context);
        this.navigator = new WeakReference<SignUpViewNavigator>(navigator);
    }

    @Override
    public void onCreate() {}

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    public void onClickSignUpButton(View view) {
        if (navigator.get() != null) {
            navigator.get().onShowAgreement();
        }
    }

    public void onClickFindAccount() {
        Log.d(TAG, "onClickFindAccount");
        if (navigator.get() != null) {
            navigator.get().onFindAccount();
        }
    }
}
