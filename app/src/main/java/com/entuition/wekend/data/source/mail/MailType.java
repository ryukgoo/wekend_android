package com.entuition.wekend.data.source.mail;

import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public enum MailType {

    receive(R.string.mailbox_receive),
    send(R.string.mailbox_send);

    private int resourceId;

    MailType(int resId) {
        this.resourceId = resId;
    }

    public int getResourceId() {
        return this.resourceId;
    }
}
