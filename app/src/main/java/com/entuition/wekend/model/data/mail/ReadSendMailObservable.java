package com.entuition.wekend.model.data.mail;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 8. 23..
 */

public class ReadSendMailObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ReadSendMailObservable instance;

    public static ReadSendMailObservable getInstance() {
        if (instance == null) {
            instance = new ReadSendMailObservable();
        }
        return instance;
    }

    private void triggerObservers(int position) {
        synchronized (this) {
            setChanged();
            notifyObservers(position);
        }
    }

    public void read(int position) {
        triggerObservers(position);
    }

}
