package com.entuition.wekend.view.main.mailbox.viewmodel;

import com.daimajia.swipe.SwipeLayout;
import com.entuition.wekend.data.source.mail.IMail;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public interface MailBoxNavigator {

    void gotoMailProfileView(IMail mail);

    void onCompleteDeleteMail(int position, SwipeLayout layout);
    void onFailedDeleteMail();

}
