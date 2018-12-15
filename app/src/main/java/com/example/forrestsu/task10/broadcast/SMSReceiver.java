package com.example.forrestsu.task10.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.forrestsu.task10.activity.CallActivity;



public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(CallActivity.SMS_SEND_ACTION)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "onReceive: 发送成功");
                    Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Log.i(TAG, "onReceive: 发送失败");
                    Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;
                default:
                    break;
            }
        } else if(intent.getAction().equals(CallActivity.SMS_DELIVERED_ACTION)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "接收成功", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(context, "接收失败", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    break;
                default:
                    break;
            }
        }
    }
}
