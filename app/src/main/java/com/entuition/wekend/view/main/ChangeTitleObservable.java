package com.entuition.wekend.view.main;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 6..
 */

public class ChangeTitleObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ChangeTitleObservable instance;

    public static ChangeTitleObservable getInstance() {
        if (instance == null) {
            instance = new ChangeTitleObservable();
        }
        return instance;
    }

    private void triggerObservers(String title) {
        synchronized (this) {
            setChanged();
            notifyObservers(title);
        }
    }

    public void change(String title) {
        triggerObservers(title);
    }

}
