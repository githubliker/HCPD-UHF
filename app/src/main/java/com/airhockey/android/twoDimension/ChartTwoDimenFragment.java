package com.airhockey.android.twoDimension;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airhockey.android.BaseFragment;
import com.airhockey.android.Constants;
import com.airhockey.android.R;
import com.airhockey.android.util.DataHelper;
import com.airhockey.wifi.listener.DataCallBack;
import com.airhockey.wifi.util.SocketConHelper;

import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;


public class ChartTwoDimenFragment extends BaseFragment {

    private static String TAG = "ChartTwoDimenFragment";
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private TwoDimensionChartRenderer renderer;

    public ChartTwoDimenFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChartTwoDimenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartTwoDimenFragment newInstance() {
        ChartTwoDimenFragment fragment = new ChartTwoDimenFragment();
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
        glSurfaceView.setRenderer(renderer = new TwoDimensionChartRenderer(getContext()));
        TextView titleView = view.findViewById(R.id.chart_title);
        titleView.setText("PRPS");
        rendererSet = true;
        return view;
    }

    @Override
    public void update(float[] data, float[] data2) {
        renderer.setPointViewData(data2);
        glSurfaceView.requestRender();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

}
