package com.entuition.wekend.data.google.gcm;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 7..
 */

public class MailNotificationObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static MailNotificationObservable instance;

    public static MailNotificationObservable getInstance() {
        if (instance == null) {
            instance = new MailNotificationObservable();
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
