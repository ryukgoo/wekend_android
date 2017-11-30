package com.entuition.wekend.data.source.mail.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 11. 23..
 */

public class SendMailObservable extends Observable {

    public static final String TAG = SendMailObservable.class.getSimpleName();

    private static SendMailObservable instance;

    public static SendMailObservable getInstance() {
        if (instance == null) {
            synchronized (SendMailObservable.class) {
                if (instance == null) {
                    instance = new SendMailObservable();
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
