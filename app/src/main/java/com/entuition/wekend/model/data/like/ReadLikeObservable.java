package com.entuition.wekend.model.data.like;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 20..
 */

public class ReadLikeObservable extends Observable {
    private final String TAG = getClass().getSimpleName();

    private static ReadLikeObservable instance;

    public static ReadLikeObservable getInstance() {
        if (instance == null) {
            instance = new ReadLikeObservable();
        }
        return instance;
    }

    private void triggerObservers(int productId) {
        synchronized (this) {
            setChanged();
            notifyObservers(productId);
        }
    }

    public void read(int productId) {
        triggerObservers(productId);
    }
}
