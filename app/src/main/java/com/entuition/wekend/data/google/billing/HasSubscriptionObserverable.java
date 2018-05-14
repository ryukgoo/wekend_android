package com.entuition.wekend.data.google.billing;

import java.util.Observable;

public class HasSubscriptionObserverable extends Observable {

    public static final String TAG = HasSubscriptionObserverable.class.getSimpleName();

    private static HasSubscriptionObserverable instance;

    public static HasSubscriptionObserverable getInstance() {
        if (instance == null) {
            synchronized (HasSubscriptionObserverable.class) {
                if (instance == null) {
                    instance = new HasSubscriptionObserverable();
                }
            }
        }
        return instance;
    }

    private void triggerObservers() {
        synchronized (this) {
            setChanged();
            notifyObservers();
        }
    }

    public void hasSubscribe() {
        triggerObservers();
    }

}
