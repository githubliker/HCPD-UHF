package com.airhockey.android;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;

import com.airhockey.android.listener.EventManager;
import com.airhockey.android.listener.Observer;

public class BaseFragment extends Fragment implements Observer {
    protected Handler handler;

    public BaseFragment() {
       EventManager.getInstance().registerObserver(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChartActivity) {
            ChartActivity activity = (ChartActivity) context;
            this.handler = activity.handler;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventManager.getInstance().removeObserver(this);
    }

    @Override
    public void update(float[] data, float[] data2) {

    }
}
