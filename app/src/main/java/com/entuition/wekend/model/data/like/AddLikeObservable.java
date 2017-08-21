package com.entuition.wekend.model.data.like;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 6..
 */

public class AddLikeObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static AddLikeObservable instance;

    public static AddLikeObservable getInstance() {
        if (instance == null) {
            instance = new AddLikeObservable();
        }
        return instance;
    }

    private void triggerObservers(int productId) {
        synchronized (this) {
            setChanged();
            notifyObservers(productId);
        }
    }

    public void addLike(int productId) {
        triggerObservers(productId);
    }

}
