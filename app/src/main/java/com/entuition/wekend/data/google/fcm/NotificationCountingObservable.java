package com.entuition.wekend.data.google.fcm;

import com.entuition.wekend.util.Constants.NotificationType;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 7..
 */

public class NotificationCountingObservable extends Observable {

    public static final String TAG = NotificationCountingObservable.class.getSimpleName();

    private static NotificationCountingObservable instance;

    public static NotificationCountingObservable getInstance() {
        if (instance == null) {
            synchronized (NotificationCountingObservable.class) {
                if (instance == null) {
                    instance = new NotificationCountingObservable();
                }
            }
        }
        return instance;
    }

    private void triggerObservers(NotificationType type) {
        synchronized (this) {
            setChanged();
            notifyObservers(type);
        }
    }

    public void change(NotificationType type) {
        triggerObservers(type);
    }

}
