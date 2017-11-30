package com.entuition.wekend.view.join.viewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.text.Html;
import android.view.View;

import com.entuition.wekend.R;
import com.entuition.wekend.view.common.AbstractViewModel;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 10. 26..
 */

public class AgreementViewModel extends AbstractViewModel {

    private static final String TAG = AgreementViewModel.class.getSimpleName();

    public final ObservableField<String> agreement = new ObservableField<>();
    public final ObservableField<String> privacy = new ObservableField<>();
    public final ObservableBoolean isCheckAgreement = new ObservableBoolean();
    public final ObservableBoolean isCheckPrivacy = new ObservableBoolean();
    public final ObservableBoolean isValidButton = new ObservableBoolean();

    private final WeakReference<AgreementNavigator> navigator;

    public AgreementViewModel(Context context, AgreementNavigator navigator) {
        super(context);
        this.navigator = new WeakReference<AgreementNavigator>(navigator);
    }

    @Override
    public void onCreate() {
        agreement.set(String.valueOf(Html.fromHtml(getApplication().getString(R.string.text_agreement))));
        privacy.set(String.valueOf(Html.fromHtml(getApplication().getString(R.string.privacy_policy))));

        isCheckAgreement.set(false);
        isCheckPrivacy.set(false);
        isValidButton.set(false);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void onClickAgreement(View view) {
        isCheckAgreement.set(!isCheckAgreement.get());
        isValidButton.set(isCheckAgreement.get() && isCheckPrivacy.get());

    }

    public void onClickPrivacy(View view) {
        isCheckPrivacy.set(!isCheckPrivacy.get());
        isValidButton.set(isCheckAgreement.get() && isCheckPrivacy.get());
    }

    public void onClickButton(View view) {
        navigator.get().onConfirmAgreement();
    }
}
