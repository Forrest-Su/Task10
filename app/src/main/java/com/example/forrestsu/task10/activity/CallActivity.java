package com.example.forrestsu.task10.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.forrestsu.task10.R;
import com.example.forrestsu.task10.broadcast.SMSReceiver;

import java.util.ArrayList;
import java.util.List;

public class CallActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "CallActivity";
    public static final String SMS_SEND_ACTION = "SMS_SEND_ACTION";
    public static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";


    private EditText phoneNumberET, messageET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        requestPermission();
        init();
        //注册广播
        SMSReceiver sendReceiver = new SMSReceiver();
        SMSReceiver deliverReceiver = new SMSReceiver();
        IntentFilter ifSend = new IntentFilter(SMS_SEND_ACTION);
        IntentFilter ifDeliver = new IntentFilter(SMS_DELIVERED_ACTION);
        registerReceiver(sendReceiver, ifSend);
        registerReceiver(deliverReceiver, ifDeliver);
    }

    //初始化
    public void init() {
        //findViewById
        Button callBT1 = (Button) findViewById(R.id.bt_call_1);
        Button callBT2 = (Button) findViewById(R.id.bt_call_2);
        Button messageBT1 = (Button) findViewById(R.id.bt_message_0);
        Button messageBT2 = (Button) findViewById(R.id.bt_message_1);
        Button messageBT3 = (Button) findViewById(R.id.bt_message_2);
        phoneNumberET = (EditText) findViewById(R.id.et_phone_number);
        messageET = (EditText) findViewById(R.id.et_message);

        //setListener
        callBT1.setOnClickListener(this);
        callBT2.setOnClickListener(this);
        messageBT1.setOnClickListener(this);
        messageBT2.setOnClickListener(this);
        messageBT3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bt_call_1:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumberET.getText()));
                startActivity(intent);
                break;
            case R.id.bt_call_2:
                Intent intent2 = new Intent(Intent.ACTION_CALL);
                intent2.setData(Uri.parse("tel:" + phoneNumberET.getText()));
                startActivity(intent2);
                break;
            case R.id.bt_message_0:
                Intent sendMessage = new Intent(Intent.ACTION_SENDTO);
                sendMessage.setData(Uri.parse("smsto:" + phoneNumberET.getText()));
                sendMessage.putExtra("sms_body", messageET.getText().toString());
                startActivity(sendMessage);
                break;
            case R.id.bt_message_1:
                sendMessage(phoneNumberET.getText().toString(), messageET.getText().toString());
                break;
            case R.id.bt_message_2:
                sendMessage2(phoneNumberET.getText().toString(), messageET.getText().toString());
                break;
            default:
                break;
        }
    }

    //分条发送、接收
    public void sendMessage(String phoneNumber, String message) {
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(message)) {
            SmsManager smsManager = SmsManager.getDefault();
            Intent itSend = new Intent(SMS_SEND_ACTION);
            Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
            PendingIntent piSend = PendingIntent.getBroadcast(
                    CallActivity.this, 0, itSend, 0);
            PendingIntent piDeliver = PendingIntent.getBroadcast(
                    CallActivity.this, 0, itDeliver, 0);

            if (message.length() > 70) {
                //拆分短信，70字符为一String，将String存入List
                ArrayList<String> msgs = smsManager.divideMessage(message);
                for (String msg : msgs) {
                    smsManager.sendTextMessage(phoneNumber, null, msg, piSend, piDeliver);
                }
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, piSend, piDeliver);
            }
        } else {
            Toast.makeText(CallActivity.this, "号码和内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    //合并发送、接收
    public void sendMessage2(String phoneNumber, String message) {
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(message)) {
            SmsManager smsManager = SmsManager.getDefault();
            Intent itSend = new Intent(SMS_SEND_ACTION);
            Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
            PendingIntent piSend = PendingIntent.getBroadcast(
                    CallActivity.this, 0, itSend, 0);
            PendingIntent piDeliver = PendingIntent.getBroadcast(
                    CallActivity.this, 0, itDeliver, 0);

            ArrayList<PendingIntent> piSends = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> piDelivers = new ArrayList<PendingIntent>();

            if (message.length() > 70) {
                //拆分短信，70字符为一String，将String存入List
                ArrayList<String> msgs = smsManager.divideMessage(message);
                for (int i = 0; i < msgs.size(); i++) {
                    piSends.add(piSend);
                    piDelivers.add(piDeliver);
                }
                smsManager.sendMultipartTextMessage(phoneNumber, null, msgs, piSends, piDelivers);
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, piSend, piDeliver);
            }
        } else {
            Toast.makeText(CallActivity.this, "号码和内容不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    申请权限:电话权限、短信权限
     */
    public void requestPermission() {
        List<String> permissionList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(CallActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CALL_PHONE);
        }
        if(ContextCompat.checkSelfPermission(CallActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(CallActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionList.isEmpty()) {
            //
        } else {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(CallActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case 1:
                if (grantResult.length > 0) {
                    for (int result : grantResult) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(CallActivity.this, "请授予权限", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(CallActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
