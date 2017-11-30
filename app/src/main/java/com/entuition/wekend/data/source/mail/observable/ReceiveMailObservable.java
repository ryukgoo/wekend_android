package com.entuition.wekend.data.source.mail.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 11. 23..
 */

public class ReceiveMailObservable extends Observable {

    public static final String TAG = ReceiveMailObservable.class.getSimpleName();

    private static ReceiveMailObservable instance;

    public static ReceiveMailObservable getInstance() {
        if (instance == null) {
            synchronized (ReceiveMailObservable.class) {
                if (instance == null) {
                    instance = new ReceiveMailObservable();
                }
            }
        }
        return instance;
    }

    private void triggerObservers() {
        synchronized (this) {
            setChanged();
            notifyObservers();
        }
    }

    public void change() {
        triggerObservers();
    }
}
