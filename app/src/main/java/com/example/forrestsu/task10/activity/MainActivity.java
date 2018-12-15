package com.example.forrestsu.task10.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.forrestsu.task10.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    //初始化
    public void init() {
        //findViewById
        Button callBT = (Button) findViewById(R.id.bt_call);
        Button cameraBT = (Button) findViewById(R.id.bt_camera);

        //setListener
        callBT.setOnClickListener(this);
        cameraBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bt_call:
                Intent toCallActivity = new Intent(MainActivity.this, CallActivity.class);
                startActivity(toCallActivity);
                break;
            case R.id.bt_camera:
                Intent toCameraActivity = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(toCameraActivity);
                break;
            default:
                break;
        }
    }

}
