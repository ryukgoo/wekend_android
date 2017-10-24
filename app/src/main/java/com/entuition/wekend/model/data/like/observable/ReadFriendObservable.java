package com.entuition.wekend.model.data.like.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

public class ReadFriendObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ReadFriendObservable instance;

    public static ReadFriendObservable getInstance() {
        if (instance == null) {
            instance = new ReadFriendObservable();
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
