package com.airhockey.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
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

import com.airhockey.BaseActivity;
import com.airhockey.android.ChartActivity;
import com.airhockey.android.Constants;
import com.airhockey.android.R;
import com.airhockey.wifi.listener.OnItemClickListener;
import com.airhockey.wifi.util.wifiToolHelper;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class WifiConnectActivity extends BaseActivity {

    private String TAG = "SCAN RESULTS IT'S EMPTY";
    RecyclerView mRecyclerView;
    ProgressBar bar;
    MyRecycleViewAdapter mAdapter;
    private ImageView wifiConnectIcon;
    private LinearLayout wifiStateLL;
    private TextView wifiName;
    private Button enter;
    List<ScanResult> newResult = new ArrayList<>();
    private WifiManager mWifiManager;

    @SuppressLint("WifiManagerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_state);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 555);

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        initView();
        newResult.addAll(mWifiManager.getScanResults());
        mAdapter = new MyRecycleViewAdapter(newResult);
        mAdapter.setOnItemClickListener(onClickListener);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WifiUtils.withContext(getApplicationContext()).enableWifi(isSuccess -> {
            Log.e(TAG, "WIFI STATE " + isSuccess);
            if (isSuccess) {
                runOnUiThread(() -> {
                    bar.setVisibility(View.VISIBLE);
                    mWifiManager.startScan();
                });
            } else {
                Toast.makeText(WifiConnectActivity.this, "请允许本应用访问您的WIFI设备", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onScanFinish() {
        super.onScanFinish();
        newResult.clear();
        newResult.addAll(mWifiManager.getScanResults());
        mAdapter.notifyDataSetChanged();

        if (!handler.hasMessages(1)) {
            handler.sendEmptyMessageDelayed(1, 100);
        }

        if (!handler.hasMessages(0)) {
            handler.sendEmptyMessageDelayed(0, 2000);
        }
    }

    private void connectWifi(String SSID, String BSSID, ConnectionSuccessListener listener) {
        WifiUtils.withContext(getApplicationContext())
                .connectWith(SSID, Constants.PASSWORD)
                .setTimeout(1000000)
                .onConnectionResult(listener)
                .start();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
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
     * 连接失败
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
        if (handler != null) {
            handler.removeMessages(0);
            handler.removeMessages(1);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String ssid = wifiToolHelper.getConnectSSID(getApplicationContext());
                if (!TextUtils.isEmpty(ssid)) {
                    Constants.SSID = ssid;
                    mAdapter.setConnectSSID(ssid);
                    mAdapter.notifyDataSetChanged();
                    connectY(ssid);
                } else {
                    handler.sendEmptyMessageDelayed(1, 2000);
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
            connectWifi(SSID, BSSID, new ConnectionSuccessListener() {
                @Override
                public void isSuccessful(boolean isSuccess) {
                    Constants.SSID = SSID;
                    if (isSuccess) {
                        ((TextView) view).setText("已连接");
                        connectY(SSID);
                    } else {
                        ((TextView) view).setText("连接失败");
                        connectN();
                    }
                    Toast.makeText(WifiConnectActivity.this, "连接wifi " + SSID + " 状态 " + isSuccess, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
