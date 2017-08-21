package com.entuition.wekend.model.data.mail;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 8. 9..
 */

public class UpdateSendMailObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static UpdateSendMailObservable instance;

    public static UpdateSendMailObservable getInstance() {
        if (instance == null) {
            instance = new UpdateSendMailObservable();
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
