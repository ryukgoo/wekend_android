package com.entuition.wekend.model.googleservice.gcm;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 7..
 */

public class BadgeNotificationObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static BadgeNotificationObservable instance;

    public static BadgeNotificationObservable getInstance() {
        if (instance == null) {
            instance = new BadgeNotificationObservable();
        }
        return instance;
    }

    private void triggerObservers(String type) {
        synchronized (this) {
            setChanged();
            notifyObservers(type);
        }
    }

    public void change(String type) {
        triggerObservers(type);
    }

}
