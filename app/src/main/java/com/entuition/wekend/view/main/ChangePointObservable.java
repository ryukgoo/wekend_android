package com.entuition.wekend.view.main;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 13..
 */

public class ChangePointObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ChangePointObservable instance;

    public static ChangePointObservable getInstance() {
        if (instance == null) {
            instance = new ChangePointObservable();
        }
        return instance;
    }

    private void triggerObserver(int point) {
        synchronized (this) {
            setChanged();
            notifyObservers(point);
        }
    }

    public void change(int point) {
        triggerObserver(point);
    }

}
