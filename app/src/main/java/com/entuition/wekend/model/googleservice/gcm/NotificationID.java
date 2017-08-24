package com.entuition.wekend.model.googleservice.gcm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ryukgoo on 2017. 8. 24..
 */
public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }
}
