package com.airhockey.android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import com.airhockey.android.R;
import com.airhockey.android.listener.EventManager;
import com.airhockey.android.threeDimension.ChartThreeDimenFragment;
import com.airhockey.android.twoDimension.ChartTwoDimenFragment;
import com.airhockey.android.util.DataHelper;
import com.airhockey.wifi.listener.DataCallBack;
import com.airhockey.wifi.util.SocketConHelper;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;

import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;

public class ChartActivity extends FragmentActivity{
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

        Utils.init(getApplicationContext());
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
