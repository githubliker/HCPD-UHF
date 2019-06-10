package com.airhockey.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.airhockey.BaseActivity;
import com.airhockey.android.listener.EventManager;
import com.airhockey.android.threeDimension.ChartThreeDimenFragment;
import com.airhockey.android.twoDimension.ChartTwoDimenFragment;
import com.airhockey.android.util.DataHelper;
import com.airhockey.wifi.listener.DataCallBack;
import com.airhockey.wifi.util.SocketConHelper;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import java.util.ArrayList;

import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;

public class ChartActivity extends BaseActivity {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private static String TAG = "ChartActivity";
    private float[] resultData = new float[SAMPLE_DATA_NUM *DATA_SPACE* SAMPLE_GROUP_NUM];
    private float[] statictisData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_layout);

        getSupportFragmentManager().beginTransaction().add(R.id.two_chart_container,
                ChartTwoDimenFragment.newInstance()).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.three_chart_container,
                ChartThreeDimenFragment.newInstance()).commitAllowingStateLoss();

        handler.sendEmptyMessageDelayed(0,1000);
//        SocketConHelper.getInstance().initialization();
//        SocketConHelper.getInstance().getDataFromPC(callBack);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                operationData(DataHelper.genOneViewData());
                statictisData = DataHelper.genPointViewData(resultData);
                EventManager.getInstance().publishMessage(resultData,statictisData);
                if(!hasMessages(0)){
                    handler.sendEmptyMessageDelayed(0,100);
                }
            }
        }
    };

    @Override
    public void onNetDisconnected() {
        super.onNetDisconnected();
        Toast.makeText(this,"网络已断开连接，正在重试",Toast.LENGTH_SHORT).show();
        SocketConHelper.getInstance().releaseAllServer();
        //TODO 打开wifi设备
        NetworkUtils.setWifiEnabled(true);
    }

    @Override
    public void onNetConnected(NetworkUtils.NetworkType networkType) {
        super.onNetConnected(networkType);
        if(networkType == NetworkUtils.NetworkType.NETWORK_WIFI){
            Log.e(TAG,"start work on soc");
            SocketConHelper.getInstance().initialization();
            SocketConHelper.getInstance().getDataFromPC(callBack);
        } else  if(networkType == NetworkUtils.NetworkType.NETWORK_4G||networkType == NetworkUtils.NetworkType.NETWORK_3G
                    || networkType == NetworkUtils.NetworkType.NETWORK_2G){
            NetworkUtils.setWifiEnabled(true);
        } else {
            //TODO 连接指定wifi设备
            WifiUtils.withContext(getApplicationContext()).connectWith(Constants.SSID,Constants.PASSWORD)
                    .setTimeout(10000)
                    .onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void isSuccessful(boolean isSuccess) {
                            if(isSuccess){
                                Toast.makeText(ChartActivity.this,"网络已恢复连接",Toast.LENGTH_SHORT).show();
                            } else {
                                NetworkUtils.setWifiEnabled(true);
                            }
                        }
                    }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler.hasMessages(0)){
            handler.removeMessages(0);
            handler = null;
        }
    }

    DataCallBack callBack = new DataCallBack() {
        @Override
        public void onReceive(float[] data) {
            operationData(DataHelper.genOneViewData(data));
            statictisData = DataHelper.genPointViewData(resultData);
            EventManager.getInstance().publishMessage(resultData,statictisData);
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


    /** 保存MyTouchListener接口的列表 */
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<>();

    /** 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法 */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /** 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法 */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove( listener );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
    public interface MyTouchListener {
        /** onTOuchEvent的实现 */
        boolean onTouchEvent(MotionEvent event);
    }

}
