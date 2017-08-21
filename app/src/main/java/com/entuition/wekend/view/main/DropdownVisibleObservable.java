package com.entuition.wekend.view.main;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 6..
 */

public class DropdownVisibleObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static DropdownVisibleObservable instance;

    public static DropdownVisibleObservable getInstance() {
        if (instance == null) {
            instance = new DropdownVisibleObservable();
        }
        return instance;
    }

    private void triggerObservsers(boolean visible) {
        synchronized (this) {
            setChanged();
            notifyObservers(visible);
        }
    }

    public void change(boolean visible) {
        triggerObservsers(visible);
    }

}
