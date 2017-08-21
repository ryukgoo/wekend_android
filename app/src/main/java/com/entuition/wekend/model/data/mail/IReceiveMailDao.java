package com.entuition.wekend.model.data.mail;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 9. 7..
 */
public interface IReceiveMailDao {
    List<ReceiveMail> getReceiveMailList();
    boolean loadReceiveMailList(String userId);
    ReceiveMail getReceiveMail(String userId, String senderId, int productId);
    boolean updateReceiveMail(ReceiveMail mail);
    boolean deleteReceiveMail(ReceiveMail mail);
    int getNewMail(String userId);
}
