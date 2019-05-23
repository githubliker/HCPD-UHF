package com.airhockey.android.threeDimension;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airhockey.android.ChartActivity;
import com.airhockey.android.Constants;
import com.airhockey.android.R;
import com.airhockey.android.util.DataHelper;
import com.airhockey.wifi.listener.DataCallBack;
import com.airhockey.wifi.util.SocketConHelper;
import com.blankj.utilcode.util.ScreenUtils;

import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;


public class ChartThreeDimenFragment extends Fragment {

    private static String TAG = "ChartTwoDimenFragment";
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private ChartThreeDimenRenderer renderer;
    private float[] resultData = new float[SAMPLE_DATA_NUM *DATA_SPACE* SAMPLE_GROUP_NUM];
    private float[] statictisData;

    public ChartThreeDimenFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChartTwoDimenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartThreeDimenFragment newInstance() {
        ChartThreeDimenFragment fragment = new ChartThreeDimenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart_two_dimen, container, false);
        glSurfaceView = view.findViewById(R.id.gl_view);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer = new ChartThreeDimenRenderer(getContext()));
        TextView titleView = view.findViewById(R.id.chart_title);
        titleView.setText("PRPD");
        rendererSet = true;
        handler.sendEmptyMessageDelayed(0,1000);
        /** 触摸事件的注册 */
        ((ChartActivity)this.getActivity()).registerMyTouchListener(myTouchListener);
        return view;
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
    public void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    /** 接收MainActivity的Touch回调的对象，重写其中的onTouchEvent函数 */
    ChartActivity.MyTouchListener myTouchListener = new ChartActivity.MyTouchListener() {
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            if(y > ScreenUtils.getScreenHeight()*2/3){
                return false;
            }
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
    };
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /** 触摸事件的注销 */
        ((ChartActivity)this.getActivity()).unRegisterMyTouchListener(myTouchListener);
    }
}
