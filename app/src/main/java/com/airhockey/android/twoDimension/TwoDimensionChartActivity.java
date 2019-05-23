package com.airhockey.android.twoDimension;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import com.airhockey.android.R;
import com.airhockey.android.threeDimension.ChartThreeDimenFragment;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;

public class TwoDimensionChartActivity extends FragmentActivity{
    /**
     * Hold a reference to our GLSurfaceView
     */
    private static String TAG = "TwoDimensionChartActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_layout);

        Utils.init(getApplicationContext());
        getSupportFragmentManager().beginTransaction().add(R.id.two_chart_container,
                ChartTwoDimenFragment.newInstance()).commitAllowingStateLoss();
        getSupportFragmentManager().beginTransaction().add(R.id.three_chart_container,
                ChartThreeDimenFragment.newInstance()).commitAllowingStateLoss();

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
