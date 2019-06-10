package com.airhockey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.airhockey.listener.NetStateChangeObserver;
import com.airhockey.listener.NetStateChangeReceiver;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;

public class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {
    @Override
    public void onNetDisconnected() {

    }

    @Override
    public void onNetConnected(NetworkUtils.NetworkType networkType) {

    }

    @Override
    public void onScanFinish() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Utils.init(getApplicationContext());
        NetStateChangeReceiver.getInstance().registerReceiver(this);
    }

    @Override
    protected void onDestroy() {
        NetStateChangeReceiver.getInstance().unRegisterReceiver(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume( );
        NetStateChangeReceiver.getInstance().registerObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause( );
        NetStateChangeReceiver.getInstance().unRegisterObserver(this);
    }
}
