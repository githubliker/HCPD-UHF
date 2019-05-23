package com.airhockey.wifi;

import android.Manifest;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airhockey.android.Constants;
import com.airhockey.android.R;
import com.airhockey.android.ChartActivity;
import com.airhockey.wifi.listener.OnItemClickListener;
import com.airhockey.wifi.util.wifiToolHelper;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiScan.ScanResultsListener;
import com.thanosfisherman.wifiutils.wifiState.WifiStateListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class WifiConnectActivity extends AppCompatActivity {

    private String TAG = "SCAN RESULTS IT'S EMPTY";
    RecyclerView mRecyclerView;
    ProgressBar bar;
    MyRecycleViewAdapter mAdapter;
    private ImageView wifiConnectIcon;
    private LinearLayout wifiStateLL;
    private TextView wifiName;
    private Button enter;
    List<ScanResult> newResult = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_state);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 555);

        initView();
        scanWifi();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openWifi(){
        WifiUtils.withContext(getApplicationContext()).enableWifi(new WifiStateListener() {
            @Override
            public void isSuccess(boolean isSuccess) {
                if(isSuccess){
                    bar.setVisibility(View.VISIBLE);
                    scanWifi();
                } else {
                    Toast.makeText(WifiConnectActivity.this,"打开WLAN失败",Toast.LENGTH_LONG).show();
                    bar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void closeWifi(){
        WifiUtils.withContext(getApplicationContext()).disableWifi();
        newResult.clear();
        mAdapter.notifyDataSetChanged();
    }
    private void scanWifi(){
        WifiUtils.withContext(getApplicationContext())
                 .scanWifi(new ScanResultsListener() {
                    @Override
                    public void onScanResults(@NonNull List<ScanResult> results) {
//                        Toast.makeText(WifiConnectActivity.this,"扫描到局放WLAN设备"+results.size(),Toast.LENGTH_LONG).show();
                        if (results.isEmpty()){
                            Log.e(TAG, "SCAN RESULTS IT'S EMPTY");
                            if(!handler.hasMessages(0)){
                                handler.sendEmptyMessageDelayed(0,1500);
                            }
                            return;
                        }
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

    private void initView() {
        mRecyclerView =   findViewById(R.id.recyclerView);
        bar = findViewById(R.id.progress);
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        wifiConnectIcon = findViewById(R.id.wifi_connect_icon);
        wifiStateLL = findViewById(R.id.wifi_state_ll);
        wifiName = findViewById(R.id.wifi_name);
        enter = findViewById(R.id.button_enter);

        if (enter == null) {
            return;
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WifiConnectActivity.this, ChartActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 连接成功
     */
    public void connectY(String ssid) {
        wifiConnectIcon.setImageResource(R.drawable.wifi_connect_y);
        wifiStateLL.setVisibility(View.GONE);
        wifiName.setVisibility(View.VISIBLE);
        enter.setVisibility(View.VISIBLE);
        wifiName.setText(ssid);
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     *连接失败
     */
    public void connectN() {
        wifiConnectIcon.setImageResource(R.drawable.wifi_connect_n);
        wifiStateLL.setVisibility(View.VISIBLE);
        wifiName.setVisibility(View.INVISIBLE);
        enter.setVisibility(View.VISIBLE);
        bar.setVisibility(View.VISIBLE);
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
                    connectY(ssid);
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
                        connectY(SSID);
                    } else {
                        ((TextView) view).setText("连接失败");
                        connectN();
                    }
                    Toast.makeText(WifiConnectActivity.this,"连接wifi "+SSID +" 状态 "+isSuccess,Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
