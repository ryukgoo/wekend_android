package com.entuition.wekend.model.data.mail.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 8. 9..
 */

public class UpdateReceiveMailObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static UpdateReceiveMailObservable instance;

    public static UpdateReceiveMailObservable getInstance() {
        if (instance == null) {
            instance = new UpdateReceiveMailObservable();
        }
        return instance;
    }

    private void triggerObservers() {
        synchronized (this) {
            setChanged();
            notifyObservers();
        }
    }

    public void update() {
        triggerObservers();
    }
}
