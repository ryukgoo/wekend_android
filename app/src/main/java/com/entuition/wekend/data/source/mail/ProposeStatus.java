package com.entuition.wekend.data.source.mail;

import com.entuition.wekend.R;

/**
 * Created by ryukgoo on 2017. 11. 16..
 */

public enum ProposeStatus {

    none(R.string.propose_button),
    notMade(R.string.propose_not_made),
    Made(R.string.propose_accepted),
    reject(R.string.propose_rejected),
    delete(R.string.propose_rejected),
    alreadyMade(R.string.propose_already_made);

    private final int statusId;

    ProposeStatus(int statusId) {
        this.statusId = statusId;
    }

    public int getMessageId(MailType type) {
        switch (type) {
            case receive:
                switch (this) {
                    case notMade:
                        return R.string.propose_receive_message;
                    case Made:
                        return R.string.propose_made_message;
                    case reject:
                        return R.string.propose_rejected_message;
                    case alreadyMade:
                        return R.string.propose_made_message;
                }
                break;
            case send:
                switch (this) {
                    case notMade:
                        return R.string.propose_request_message;
                    case Made:
                        return R.string.propose_made_message;
                    case reject:
                        return R.string.propose_rejecte_message;
                    case alreadyMade:
                        return R.string.propose_made_message;
                }
                break;
        }
        return statusId;
    }

    public int getStatusId() {
        return statusId;
    }

}
