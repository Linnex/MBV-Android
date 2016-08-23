package com.mbv.pokket.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mbv.pokket.dao.constants.AppConstants;

/**
 * Created by arindamnath on 10/02/16.
 */
public class SMSReceiver extends BroadcastReceiver {

    public static final String OTP_BROADCAST_TAG = "appOTPBroadcast";

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private static final String OTP_TAG = "MBVOTP";
    private static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(SMS_RECEIVED)) {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    Object[] pdusObj = (Object[]) bundle.get(SMS_BUNDLE);
                    for (Object aPdusObj : pdusObj) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                        String senderAddress = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();
                        if (senderAddress.toLowerCase().contains(OTP_TAG.toLowerCase())) {
                            String verificationCode = getVerificationCode(message);
                            Log.i(AppConstants.APP_TAG, "OTP received: " + verificationCode);
                            Intent i = new Intent(OTP_BROADCAST_TAG);
                            i.putExtra("otp", verificationCode);
                            context.sendBroadcast(i);
                        } else {
                            //TODO capture other SMS
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_TAG, "Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(":");

        if (index != -1) {
            int start = index + 2;
            int length = 5;
            code = message.substring(start, start + length);
            return code;
        }
        return code;
    }
}
