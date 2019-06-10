package com.airhockey.listener;

import com.blankj.utilcode.util.NetworkUtils;

public interface NetStateChangeObserver {
    void onNetDisconnected();
    void onNetConnected(NetworkUtils.NetworkType networkType);
    void onScanFinish();
}
