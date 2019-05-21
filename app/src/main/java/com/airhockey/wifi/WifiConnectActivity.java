package com.airhockey.wifi;

import android.Manifest;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airhockey.android.Constants;
import com.airhockey.android.R;
import com.airhockey.wifi.listener.OnItemClickListener;
import com.airhockey.wifi.util.wifiToolHelper;
import com.kyleduo.switchbutton.SwitchButton;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class WifiConnectActivity extends AppCompatActivity {

    private String TAG = "WifiConnectActivity";
    RecyclerView mRecyclerView;
    SwitchButton stateButton;
    ProgressBar bar;
    MyRecycleViewAdapter mAdapter;
    List<ScanResult> newResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 555);
        mRecyclerView =   findViewById(R.id.recyclerView);
        stateButton = findViewById(R.id.state_button);
        bar = findViewById(R.id.progress);
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        scanWifi();
        stateButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    openWifi();
                } else {
                    closeWifi();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 555){
//            if(wifiToolHelper.isWifiEnabled(getApplicationContext())){
//                stateButton.setChecked(true);
//                bar.setVisibility(View.VISIBLE);
//                scanWifi();
//            } else {
//                openWifi();
//            }
//        }
    }

    private void openWifi(){
        WifiUtils.withContext(getApplicationContext()).enableWifi(new WifiStateListener() {
            @Override
            public void isSuccess(boolean isSuccess) {
                if(isSuccess){
                    stateButton.setChecked(true);
                    bar.setVisibility(View.VISIBLE);
                    scanWifi();
                } else {
                    stateButton.setChecked(false);
                    Toast.makeText(WifiConnectActivity.this,"打开WLAN失败",Toast.LENGTH_LONG).show();
                    bar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void closeWifi(){
        stateButton.setChecked(false);
        WifiUtils.withContext(getApplicationContext()).disableWifi();
        newResult.clear();
        mAdapter.notifyDataSetChanged();
    }
    private void scanWifi(){
        WifiUtils.withContext(getApplicationContext())
                 .scanWifi(new ScanResultsListener() {
                    @Override
                    public void onScanResults(@NonNull List<ScanResult> results) {
                        if (results.isEmpty()){
                            Log.i(TAG, "SCAN RESULTS IT'S EMPTY");
//                            Toast.makeText(WifiConnectActivity.this,"未扫描到任何WLAN设备",Toast.LENGTH_LONG).show();
                            if(!handler.hasMessages(0)){
                                handler.sendEmptyMessageDelayed(0,1500);
                            }
                            return;
                        }
                        bar.setVisibility(View.INVISIBLE);
                        newResult.clear();
                        for(int i = 0;i<results.size();i++){
                         /*   if(results.get(i).SSID.startsWith(Constants.HEADSSID)){
                                newResult.add(results.get(i));
                            }*/
                            if(!results.get(i).SSID.isEmpty()){
                                newResult.add(results.get(i));
                            }
                        }
                        if(newResult.size() == 0){
                            Toast.makeText(WifiConnectActivity.this,"未扫描到任何局放WLAN设备",Toast.LENGTH_LONG).show();
                        } else {
                            mAdapter = new MyRecycleViewAdapter(newResult);
                            mAdapter.setOnItemClickListener(onClickListener);
                            mRecyclerView.setAdapter(mAdapter);
                            if(!handler.hasMessages(1)){
                                handler.sendEmptyMessageDelayed(1,100);
                            }
                        }
                        bar.setVisibility(View.INVISIBLE);
                    }
                }).start();
    }

    private void connectWifi(String SSID ,String BSSID,ConnectionSuccessListener listener){
        WifiUtils.withContext(getApplicationContext())
                .connectWith(SSID, Constants.PASSWORD)
                .setTimeout(1000000)
                .onConnectionResult(listener)
                .start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(handler != null){
            handler.removeMessages(0);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                scanWifi();
            } else if (msg.what == 1){
                String ssid = wifiToolHelper.getConnectSSID(getApplicationContext());
                if(!TextUtils.isEmpty(ssid)){
                    mAdapter.setConnectSSID(ssid);
                    mAdapter.notifyDataSetChanged();
                } else {
                    handler.sendEmptyMessageDelayed(1,2000);
                }
            }
        }
    };
    OnItemClickListener onClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
            String SSID = newResult.get(position).SSID;
            String BSSID = newResult.get(position).BSSID;
            ((TextView) view).setText("连接中");
            connectWifi(SSID,BSSID, new ConnectionSuccessListener() {
                @Override
                public void isSuccessful(boolean isSuccess) {
                    if(isSuccess){
                        ((TextView) view).setText("已连接");
                    } else {
                        ((TextView) view).setText("连接失败");
                    }
                    Toast.makeText(WifiConnectActivity.this,"连接wifi "+SSID +" 状态 "+isSuccess,Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
