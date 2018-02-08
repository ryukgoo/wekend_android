package com.entuition.wekend.view.main.mailbox.viewmodel;

import android.content.DialogInterface;
import android.databinding.ObservableField;
import android.text.Editable;

import java.lang.ref.WeakReference;

/**
 * Created by ryukgoo on 2017. 12. 4..
 */

public class UserInputDialogViewModel implements DialogInterface.OnClickListener {

    public static final String TAG = UserInputDialogViewModel.class.getSimpleName();

    public final ObservableField<String> textCount = new ObservableField<>();
    public final ObservableField<String> message = new ObservableField<>();

    private final WeakReference<UserInputDialogListener> listener;

    public UserInputDialogViewModel(UserInputDialogListener listener) {
        this.listener = new WeakReference<UserInputDialogListener>(listener);
        this.textCount.set("0/200");
    }

    public void onChangeMessageText(Editable editable) {
        if (editable != null) {
            textCount.set(editable.length() + "/200");
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        dialogInterface.dismiss();
        if (which == DialogInterface.BUTTON_POSITIVE && listener.get() != null) {
            listener.get().onCompleteInputMessage(message.get());
        }
    }
}
