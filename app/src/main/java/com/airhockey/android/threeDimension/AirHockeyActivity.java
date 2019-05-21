/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android.threeDimension;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.airhockey.android.Constants;
import com.airhockey.android.R;
import com.airhockey.android.util.DataHelper;
import com.airhockey.wifi.listener.DataCallBack;
import com.airhockey.wifi.WifiConnectActivity;
import com.airhockey.wifi.util.SocketConHelper;
import com.airhockey.wifi.util.wifiToolHelper;

import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;

public class AirHockeyActivity extends Activity implements View.OnClickListener {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private static String TAG = "AirHockeyActivity";
    private ImageView openDevice;
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private AirHockeyRenderer renderer;
//    private float[] oldData;
    private float[] resultData = new float[SAMPLE_DATA_NUM *DATA_SPACE* SAMPLE_GROUP_NUM];
    private float[] statictisData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_view);
        openDevice = findViewById(R.id.open_device);
        ActivityManager activityManager = 
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
            .getDeviceConfigurationInfo();
        final boolean supportsEs2 =
            configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                 && (Build.FINGERPRINT.startsWith("generic")
                  || Build.FINGERPRINT.startsWith("unknown")
                  || Build.MODEL.contains("google_sdk")
                  || Build.MODEL.contains("Emulator")
                  || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(renderer = new AirHockeyRenderer(this));
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG).show();
            return;
        }
        handler.sendEmptyMessageDelayed(0,1000);
        openDevice.setOnClickListener(this);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                operationData(DataHelper.genOneViewData());
                statictisData = DataHelper.genPointViewData(resultData);
                renderer.setDataViewMatrix(resultData);
                renderer.setPointViewData(statictisData);
                glSurfaceView.requestRender();
                handler.sendEmptyMessageDelayed(0,100);
            } else if(msg.what == 1){
                SocketConHelper.getInstance().getDataFromPC(callBack);
            }
        }
    };

    DataCallBack callBack = new DataCallBack() {
        @Override
        public void onReceive(float[] data) {
            operationData(DataHelper.genOneViewData(data));
            statictisData = DataHelper.genPointViewData(resultData);
            renderer.setDataViewMatrix(resultData);
            renderer.setPointViewData(statictisData);
            glSurfaceView.requestRender();
        }
    };
    /*
      将数组中的数据沿着Z轴移动TIME_SPCAE 距离，
      并将新生成的数据添加到数组的最前面
    */
    private void operationData(float[] data){
        DataHelper.reverseData(resultData,0, SAMPLE_DATA_NUM *DATA_SPACE -1);
        DataHelper.reverseData(resultData, SAMPLE_DATA_NUM *DATA_SPACE,resultData.length -1);
        DataHelper.reverseData(resultData,0,resultData.length -1);
        for(int i = SAMPLE_DATA_NUM *DATA_SPACE; i<resultData.length; i= i+DATA_SPACE){
            resultData[i+2] = resultData[i+2]- Constants.TIME_SPACE;
            resultData[i+8] = resultData[i+8]-Constants.TIME_SPACE;
        }
        System.arraycopy(data, 0, resultData, 0, data.length);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        SocketConHelper.getInstance().releaseAllServer();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = (x - mPreviousX)*0.05f;
                float dy = (y - mPreviousY)*0.1f;
                renderer.setAngle(
                        ((dx) * TOUCH_SCALE_FACTOR),dy*TOUCH_SCALE_FACTOR);
                glSurfaceView.requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.open_device){
            String ssid = wifiToolHelper.getConnectSSID(getApplicationContext());
            if(ssid.startsWith(Constants.HEADSSID)){
                Toast.makeText(AirHockeyActivity.this,"设备"+ssid+"已经连接，即将传输数据",Toast.LENGTH_SHORT).show();
                SocketConHelper.getInstance().initialization();
                SocketConHelper.getInstance().getDataFromPC(callBack);
                openDevice.setImageResource(R.drawable.icon_connect);
            } else {
                Intent i = new Intent(this, WifiConnectActivity.class);
                startActivity(i);
            }
        }
    }
}