package com.entuition.wekend.model.data.user.observable;

import java.util.Observable;

/**
 * Created by ryukgoo on 2017. 10. 10..
 */

public class ChangeNicknameObservable extends Observable {

    private final String TAG = getClass().getSimpleName();

    private static ChangeNicknameObservable instance;

    public static ChangeNicknameObservable getInstance() {
        if (instance == null) {
            instance = new ChangeNicknameObservable();
        }
        return instance;
    }

    private void triggerObservers(String nickname) {
        synchronized (this) {
            setChanged();
            notifyObservers(nickname);
        }
    }

    public void change(String nickname) {
        triggerObservers(nickname);
    }

}
