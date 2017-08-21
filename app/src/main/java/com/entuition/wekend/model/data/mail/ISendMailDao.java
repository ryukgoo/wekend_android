package com.entuition.wekend.model.data.mail;

import java.util.List;

/**
 * Created by ryukgoo on 2016. 9. 6..
 */
public interface ISendMailDao {
    List<SendMail> getSendMailList();
    boolean loadSendMailList(String userId);
    SendMail getSendMail(String userId, String receiverId, int productId);
    boolean updateSendMail(SendMail sendMail);
    boolean deleteSendMail(SendMail mail);
    int getNewMail(String userId);
}
