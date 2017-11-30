package com.entuition.wekend.data.source.like.observable;

import com.entuition.wekend.data.source.like.LikeInfo;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 11. 28..
 */

public class ReadFriendObservable extends Observable {

    public static final String TAG = ReadFriendObservable.class.getSimpleName();

    private static ReadFriendObservable INSTANCE;

    public static ReadFriendObservable getInstance() {
        if (INSTANCE == null) {
            synchronized (ReadFriendObservable.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReadFriendObservable();
                }
            }
        }
        return INSTANCE;
    }

    private void triggerObservers(LikeInfo info) {
        synchronized (this) {
            setChanged();
            notifyObservers(info);
        }
    }

    public void readFriend(LikeInfo info) {
        triggerObservers(info);
    }
}
