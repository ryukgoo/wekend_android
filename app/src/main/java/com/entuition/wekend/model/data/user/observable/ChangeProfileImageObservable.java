package com.entuition.wekend.model.data.user.observable;

import android.graphics.Bitmap;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 13..
 */

public class ChangeProfileImageObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ChangeProfileImageObservable instance;

    public static ChangeProfileImageObservable getInstance() {
        if (instance == null) {
            instance = new ChangeProfileImageObservable();
        }
        return instance;
    }

    private void triggerObservers(Bitmap bitmap) {
        synchronized (this) {
            setChanged();
            notifyObservers(bitmap);
        }
    }

    public void change(Bitmap bitmap) {
        triggerObservers(bitmap);
    }

}
