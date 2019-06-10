package com.airhockey.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.blankj.utilcode.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class NetStateChangeReceiver extends BroadcastReceiver {

    private NetworkUtils.NetworkType mType = NetworkUtils.getNetworkType();
    private List<NetStateChangeObserver> mObservers = new ArrayList<>();

    private static class InstanceHolder{
        private static final NetStateChangeReceiver INSTANCE = new NetStateChangeReceiver();
    }

    public static NetStateChangeReceiver getInstance(){
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            NetworkUtils.NetworkType networkType = NetworkUtils.getNetworkType();
            notifyObservers(networkType);
        } else  if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            notifyObservers();
        }
    }

    public void registerReceiver(Context context){
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(NetStateChangeReceiver.getInstance(),intentFilter);
    }

    public void unRegisterReceiver(Context context){
        context.unregisterReceiver(NetStateChangeReceiver.getInstance());
    }

    public void registerObserver(NetStateChangeObserver observer){
        if (observer == null) {
            return;
        }
        if (!mObservers.contains(observer)){
            NetStateChangeReceiver.getInstance().mObservers.add(observer);
        }
    }

    public void unRegisterObserver(NetStateChangeObserver observer){
        if (observer == null) {
            return;
        }
        if (mObservers == null) {
            return;
        }
        mObservers.remove(observer);
    }

    private void notifyObservers(NetworkUtils.NetworkType networkType){
        if (mType == networkType) {
            return;
        }
        mType = networkType;
        if (networkType == NetworkUtils.NetworkType.NETWORK_NO){
            for (NetStateChangeObserver observer : mObservers){
                observer.onNetDisconnected();
            }
        }else {
            for (NetStateChangeObserver observer : mObservers){
                observer.onNetConnected(networkType);
            }
        }
    }

    private void notifyObservers(){
        for (NetStateChangeObserver observer : mObservers){
            observer.onScanFinish();
        }
    }

}
