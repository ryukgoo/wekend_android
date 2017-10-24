package com.entuition.wekend.model.data.mail.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 8. 23..
 */

public class ReadReceiveMailObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ReadReceiveMailObservable instance;

    public static ReadReceiveMailObservable getInstance() {
        if (instance == null) {
            instance = new ReadReceiveMailObservable();
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
