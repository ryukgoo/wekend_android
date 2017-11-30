package com.entuition.wekend.data.source.like.observable;

import com.entuition.wekend.data.source.like.LikeInfo;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 11. 23..
 */

public class AddLikeObservable extends Observable {

    public static final String TAG = AddLikeObservable.class.getSimpleName();

    private static AddLikeObservable instance;

    public static AddLikeObservable getInstance() {
        if (instance == null) {
            synchronized (AddLikeObservable.class) {
                if (instance == null) {
                    instance = new AddLikeObservable();
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

    public void addLike(LikeInfo info) {
        triggerObservers(info);
    }
}
