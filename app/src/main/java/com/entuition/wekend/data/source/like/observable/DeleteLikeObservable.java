package com.entuition.wekend.data.source.like.observable;

import com.entuition.wekend.data.source.like.LikeInfo;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 11. 23..
 */

public class DeleteLikeObservable extends Observable {

    public static final String TAG = DeleteLikeObservable.class.getSimpleName();

    private static DeleteLikeObservable instance;

    public static DeleteLikeObservable getInstance() {
        if (instance == null) {
            synchronized (DeleteLikeObservable.class) {
                if (instance == null) {
                    instance = new DeleteLikeObservable();
                }
            }
        }
        return instance;
    }

    private void triggerObservers(LikeInfo info) {
        synchronized (this) {
            setChanged();
            notifyObservers(info);
        }
    }

    public void deleteLike(LikeInfo info) {
        triggerObservers(info);
    }
}
