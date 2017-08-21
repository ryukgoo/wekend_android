package com.entuition.wekend.model.authentication.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.entuition.wekend.model.Utilities;
import com.entuition.wekend.model.data.mail.asynctask.ISimpleTaskCallback;
import com.surem.api.sms.SureSMSAPI;
import com.surem.net.SendReport;
import com.surem.net.sms.SureSMSDeliveryReport;

/**
 * Created by ryukgoo on 2016. 1. 7..
 */
public class RequestVerificationCodeTask extends AsyncTask<String, Void, Void> {

    private final String TAG = getClass().getSimpleName();

    private static String verificationCode;

    private ISimpleTaskCallback callback;
    private String result;

    public void setCallback(ISimpleTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {

        String phoneNumber = params[0];

        Log.d(TAG, "phoneNumber : " + phoneNumber);

        verificationCode = String.format("%06d", Utilities.generateRandomVerificationCode());

        int member = 100;
        String usercode = "picnic";
        String username = "guest";
        String callphone1 = phoneNumber.substring(0, 3);
        String callphone2 = phoneNumber.substring(3, 7);
        String callphone3 = phoneNumber.substring(7, 11);

        final String callmessage = "[위켄드] 인증번호는 " + verificationCode + " 입니다. 정확히 입력해 주세요.";
        String rdate = "00000000";
        String rtime = "000000";
        String reqphone1 = "070";
        String reqphone2 = "7565";
        String reqphone3 = "4702";
        String callname = "인증번호발신";
        String deptcode = "GC-OIJ-B5";

        SureSMSAPI sms = new SureSMSAPI() {

            @Override
            public void report(SureSMSDeliveryReport sureSMSDeliveryReport) {
                Log.d(TAG, "msgkey = " + sureSMSDeliveryReport.getMember());	// 메시지 고유값
                Log.d(TAG, "result = " + sureSMSDeliveryReport.getResult());	// '2': 전송 결과 성공.  '4': 전송 결과 실패
                Log.d(TAG, "errorcode = " + sureSMSDeliveryReport.getErrorCode());	// 결과 코드
                Log.d(TAG, "recvtime = " + sureSMSDeliveryReport.getRecvDate() + sureSMSDeliveryReport.getRecvTime());	// 단말기 수신 시간
            }
        };

        SendReport sendReport = sms.sendMain(member, usercode, deptcode, username,
                callphone1, callphone2, callphone3, callname, reqphone1, reqphone2, reqphone3, callmessage, rdate, rtime);

        result = sendReport.getStatus();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        Log.d(TAG, "onPostExecute > result : " + result);

        if (result.trim().equals("O")) {
            callback.onSuccess(null);
        } else {
            callback.onFailed();
        }
    }

    public static String getVerificationCode() {
        return verificationCode;
    }

    public static boolean validateVerificationCode(String inputCode) {
        return (verificationCode != null && inputCode.equals(verificationCode));
    }

    public static void clearVerificationCode() {
        verificationCode = "-1";
    }

}
