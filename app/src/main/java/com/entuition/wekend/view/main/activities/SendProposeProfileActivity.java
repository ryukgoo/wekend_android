package com.entuition.wekend.view.main.activities;

import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;

import com.entuition.wekend.R;
import com.entuition.wekend.model.Constants;
import com.entuition.wekend.model.data.mail.SendMail;
import com.entuition.wekend.model.data.mail.asynctask.GetSendMailTask;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.entuition.wekend.model.data.mail.asynctask.UpdateSendMailTask;
import com.entuition.wekend.model.data.user.asynctask.ConsumePointTask;
import com.entuition.wekend.view.main.ChangePointObservable;

/**
 * Created by ryukgoo on 2016. 5. 9..
 */
public class SendProposeProfileActivity extends ProposeProfileActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();

        getSendMail();
    }

    @Override
    protected void onInitViews() {
        phoneLayout.setVisibility(View.GONE);
        textMatchResult.setVisibility(View.GONE);
        sendButtons.setVisibility(View.VISIBLE);
        receiveButtons.setVisibility(View.GONE);
        textPoint.setVisibility(View.GONE);
    }

    @Override
    protected void setListeners() {
        buttonPropose.setOnClickListener(this);
    }

    @Override
    protected void onSuccessGetProposeStatus(String proposeStatus) {

        Log.d(TAG, "onSuccessGetProposeStatus > proposeStatue : " + proposeStatus);

        if (proposeStatus.equals(Constants.PROPOSE_STATUS_NOT_MADE)) {

            phoneLayout.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
            sendButtons.setVisibility(View.GONE);
            textPoint.setVisibility(View.GONE);

            textMatchResult.setText(getString(R.string.profile_propose_button_not_made));

        } else if (proposeStatus.equals(Constants.PROPOSE_STATUS_MADE)) {

            phoneLayout.setVisibility(View.VISIBLE);
            sendButtons.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
            textPoint.setVisibility(View.GONE);

            textMatchResult.setText(getString(R.string.profile_propose_text_match_success));

        } else if (proposeStatus.equals(Constants.PROPOSE_STATUS_REJECT)) {

            phoneLayout.setVisibility(View.GONE);
            sendButtons.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
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

    /**
     * button clicked
     * @param v
     */
    @Override
    public void onClick(View v) {

        // TODO : show popup for consumming point
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog))
                .setTitle(getString(R.string.dialog_consume_point_title))
                .setMessage(getString(R.string.dialog_consume_point_message))
                .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickProposeButton();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void getSendMail() {
        SendMail mail = new SendMail();
        mail.setUserId(senderId);
        mail.setReceiverId(receiverId);
        mail.setProductId(productId);

        GetSendMailTask task = new GetSendMailTask();
        task.setCallback(new GetSendMailCallback());
        task.execute(mail);
    }

    private void onClickProposeButton() {

        ConsumePointTask consumePointTask = new ConsumePointTask(this);
        consumePointTask.setCallback(new ISimpleTaskCallback() {

            @Override
            public void onPrepare() {

            }

            @Override
            public void onSuccess(@Nullable Object object) {

                Log.d(TAG, "consume Success");

                SendMail mail = new SendMail();
                mail.setUserId(senderId);
                mail.setUpdatedTime(updatedTime);
                mail.setReceiverId(receiverId);
                mail.setProductId(productId);
                mail.setStatus(Constants.PROPOSE_STATUS_NOT_MADE);

                UpdateSendMailTask task = new UpdateSendMailTask();
                task.setCallback(new UpdateSendMailCallback());
                task.execute(mail);

                int point = (int) object;
                ChangePointObservable.getInstance().change(point);
                textPoint.setText(getString(R.string.profile_owned_point) + " " + point + "P");
            }

            @Override
            public void onFailed() {

                new AlertDialog.Builder(new ContextThemeWrapper(SendProposeProfileActivity.this, R.style.CustomAlertDialog))
                        .setTitle(getString(R.string.dialog_propose_failed_no_point_title))
                        .setMessage(getString(R.string.dialog_propose_failed_no_point_message))
                        .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

                Log.d(TAG, "consume Failed");
            }
        });

        consumePointTask.execute();
    }

    private class UpdateSendMailCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {
            buttonPropose.setEnabled(false);
        }

        @Override
        public void onSuccess(@Nullable Object object) {
            phoneLayout.setVisibility(View.GONE);
            textMatchResult.setVisibility(View.VISIBLE);
            sendButtons.setVisibility(View.GONE);
            textPoint.setVisibility(View.VISIBLE);

            new AlertDialog.Builder(new ContextThemeWrapper(SendProposeProfileActivity.this, R.style.CustomAlertDialog))
                    .setTitle(getString(R.string.dialog_propose_success_title))
                    .setMessage(friendUserInfo.getNickname() + getString(R.string.dialog_propose_success_message))
                    .setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

            textMatchResult.setText(getString(R.string.profile_propose_button_not_made));
        }

        @Override
        public void onFailed() {
            buttonPropose.setEnabled(true);
        }
    }

    private class GetSendMailCallback implements ISimpleTaskCallback {

        @Override
        public void onPrepare() {
            progressBarPropose.setVisibility(View.VISIBLE);
            buttonPropose.setEnabled(false);
        }

        @Override
        public void onSuccess(@Nullable Object object) {

            SendMail result = (SendMail) object;
            onSuccessGetProposeStatus(result.getStatus());

            progressBarPropose.setVisibility(View.GONE);
            buttonPropose.setEnabled(true);
        }

        @Override
        public void onFailed() {
            progressBarPropose.setVisibility(View.GONE);
            buttonPropose.setEnabled(true);

            textPoint.setVisibility(View.VISIBLE);
        }
    }

}
