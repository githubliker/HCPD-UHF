package com.airhockey.wifi.util;

import android.util.Log;

import java.math.BigDecimal;

public class HexDataHelper {

    private final static String TAG = "HexDataHelper";
    private final static String HEAD_INFO = "eb 90 eb 90 64 29 ";
    public static boolean checkDataAvaliable(String hexData){
        boolean isAvaliable = false;
        if(hexData.toLowerCase().startsWith(HEAD_INFO)){
            hexData.replace(HEAD_INFO,"");
            String hexNum = hexData.substring(27,32);
//            Log.e(TAG, "读取当前数据总数 "+hexNum);
            if(hexNum.equals("c8 00")){
                isAvaliable = true;
            }
        }
        return isAvaliable;
    }

    public static float[] hexData2Array(String data){
        String hexData = data.substring(33);
        String[] hex = hexData.split(" ");
        int[] result = new int[500];
        int index = 0;
        for(int i = 0;i<hex.length - 1;i = i+2){
            result[index++] = (int) Long.parseLong(hex[i+1] +hex[i],  16);
//            Log.e(TAG,"i "+(index-1)+" -   " + result[index - 1]);
        }
        return dataStandardization(result);
    }
    /*
        归一化算法有：
        线性转换：y=(x-MinValue) / (MaxValue-MinValue）
        对数函数转换：y=log10(x)
        反余切函数转换：y=atan(x)*2/PI
        线性也与对数函数结合
    */
    public static float[] dataStandardization(int[] data){
        float[] result = new float[data.length];
        int max = 4096;
        int min = 0;
        float temp;
        for(int i = 0;i<data.length;i++){
//            result[i] = (float) (Math.atan(data[i])*2/Math.PI);
            temp= (float) (data[i] - min )/ (max -min);
            BigDecimal b = new BigDecimal(temp);
           result[i] = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }
        return result;
    }
}
