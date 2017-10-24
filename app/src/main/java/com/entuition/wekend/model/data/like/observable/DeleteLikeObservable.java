package com.entuition.wekend.model.data.like.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 7. 6..
 */

public class DeleteLikeObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static DeleteLikeObservable instance;

    public static DeleteLikeObservable getInstance() {
        if (instance == null) {
            instance = new DeleteLikeObservable();
        }
        return instance;
    }

    private void triggerObservers(int productId) {
        synchronized (this) {
            setChanged();
            notifyObservers(productId);
        }
    }

    public void deleteLike(int productId) {
        triggerObservers(productId);
    }

}
