package com.airhockey.wifi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class wifiToolHelper {
    /**
     * 获取当前连接WIFI的SSID
     */
    public static String getConnectSSID(Context context) {
        WifiManager wm = (WifiManager)context.getSystemService(WIFI_SERVICE);
        if (wm != null) {
            WifiInfo winfo = wm.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
    }

    public static boolean isWifiOpen(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
