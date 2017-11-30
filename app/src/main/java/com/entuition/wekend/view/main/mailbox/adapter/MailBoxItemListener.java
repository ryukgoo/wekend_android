package com.entuition.wekend.view.main.mailbox.adapter;

import com.entuition.wekend.data.source.mail.IMail;

/**
 * Created by ryukgoo on 2017. 11. 15..
 */

public interface MailBoxItemListener {

    void onClickItem(IMail mail);
    void onClickDeleteItem(IMail mail);

}
