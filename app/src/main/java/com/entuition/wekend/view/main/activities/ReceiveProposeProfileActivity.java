package com.entuition.wekend.view.main.activities;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.mail.ReceiveMail;
import com.entuition.wekend.model.data.mail.asynctask.GetReceiveMailTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.asynctask.UpdateReceiveMailTask;

/**
 * Created by ryukgoo on 2016. 5. 9..
 */
public class ReceiveProposeProfileActivity extends ProposeProfileActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();

        getReceiveMail();
    }

    @Override
    protected void onInitViews() {
        phoneLayout.setVisibility(View.GONE);
        textMatchResult.setVisibility(View.GONE);
        sendButtons.setVisibility(View.GONE);
        receiveButtons.setVisibility(View.GONE);
        textPoint.setVisibility(View.GONE);
    }

    @Override
    protected void setListeners() {
        buttonAccept.setOnClickListener(this);
        buttonReject.setOnClickListener(this);
    }

    @Override
    protected void onSuccessGetProposeStatus(String proposeStatus) {

        if (proposeStatus.equals(Constants.PROPOSE_STATUS_NOT_MADE)) {

            phoneLayout.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.GONE);
            receiveButtons.setVisibility(View.VISIBLE);
            textPoint.setVisibility(View.GONE);

        } else if (proposeStatus.equals(Constants.PROPOSE_STATUS_MADE)) {

            phoneLayout.setVisibility(View.VISIBLE);
            textMatchResult.setVisibility(View.VISIBLE);
            receiveButtons.setVisibility(View.GONE);
            textPoint.setVisibility(View.GONE);
            textMatchResult.setText(getString(R.string.profile_propose_text_match_success));

        } else if (proposeStatus.equals(Constants.PROPOSE_STATUS_REJECT)) {

            phoneLayout.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
            receiveButtons.setVisibility(View.GONE);
            textPoint.setVisibility(View.GONE);
            textMatchResult.setText(getString(R.string.profile_propose_text_match_fail));

        } else if (proposeStatus.equals(Constants.PROPOSE_STATUS_ALREADY_MADE)) {

            phoneLayout.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
            sendButtons.setVisibility(View.GONE);
            textPoint.setVisibility(View.GONE);

            textMatchResult.setText(getString(R.string.profile_propose_text_match_already_made));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_button_friend_profile_ok :
                onClickAcceptButton();
                break;
            case R.id.id_button_friend_profile_reject :
                onClickRejectButton();
                break;
        }
    }

    private void getReceiveMail() {
        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(receiverId);
        mail.setSenderId(senderId);
        mail.setProductId(productId);

        GetReceiveMailTask task = new GetReceiveMailTask();
        task.setCallback(new GetReceiveMailCallback());
        task.execute(mail);
    }

    private void onClickAcceptButton() {
        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(receiverId);
        mail.setUpdatedTime(updatedTime);
        mail.setSenderId(senderId);
        mail.setProductId(productId);
        mail.setStatus(Constants.PROPOSE_STATUS_MADE);

        UpdateReceiveMailTask task = new UpdateReceiveMailTask();
        task.setCallback(new AcceptProposeCallback());
        task.execute(mail);
    }

    private void onClickRejectButton() {
        ReceiveMail mail = new ReceiveMail();
        mail.setUserId(receiverId);
        mail.setUpdatedTime(updatedTime);
        mail.setSenderId(senderId);
        mail.setProductId(productId);
        mail.setStatus(Constants.PROPOSE_STATUS_REJECT);

        UpdateReceiveMailTask task = new UpdateReceiveMailTask();
        task.setCallback(new RejectProposeCallback());
        task.execute(mail);
    }

    private class GetReceiveMailCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {
            progressBarPropose.setVisibility(View.VISIBLE);
            buttonPropose.setEnabled(false);
        }

        @Override
        public void onSuccess(@Nullable Object object) {

            ReceiveMail result = (ReceiveMail) object;
            onSuccessGetProposeStatus(result.getStatus());

            progressBarPropose.setVisibility(View.GONE);
            buttonPropose.setEnabled(true);
        }

        @Override
        public void onFailed() {
            progressBarPropose.setVisibility(View.GONE);
            buttonPropose.setEnabled(true);
        }
    }

    private class AcceptProposeCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {

        }

        @Override
        public void onSuccess(@Nullable Object object) {
            phoneLayout.setVisibility(View.VISIBLE);
            textMatchResult.setVisibility(View.VISIBLE);
            receiveButtons.setVisibility(View.GONE);

            new AlertDialog.Builder(new ContextThemeWrapper(ReceiveProposeProfileActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.dialog_propose_made_title))
                    .setMessage(friendUserInfo.getNickname() + getString(R.string.dialog_propose_made_message))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

            textMatchResult.setText(getString(R.string.profile_propose_text_match_success));
        }

        @Override
        public void onFailed() {

        }
    }

    private class RejectProposeCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {

        }

        @Override
        public void onSuccess(@Nullable Object object) {
            phoneLayout.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
            receiveButtons.setVisibility(View.GONE);

            new AlertDialog.Builder(new ContextThemeWrapper(ReceiveProposeProfileActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.dialog_propose_reject_title))
                    .setMessage(friendUserInfo.getNickname() + getString(R.string.dialog_propose_reject_message))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

            textMatchResult.setText(getString(R.string.profile_propose_text_match_fail));
        }

        @Override
        public void onFailed() {

        }
    }
}
