/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.android;

public class Constants {
    public static final String HEADSSID = "HC-";
    public static String SSID = "";
    public static final String PASSWORD = "HCDQ1234";

    public static final int BYTES_PER_FLOAT = 4;
    public static final int X_DATA_VIEW_COUNT = 345;
    public static final float Z_DATA_VIEW_COUNT = 360f;
    public static final int DATA_SPACE = 12;

    public static final int SAMPLE_DATA_NUM = 200;//一组采样数据采样个数
    public static final int SAMPLE_GROUP_NUM = 50; //显示多少组采样数据
    public static final float TIME_SPACE = (float) (Z_DATA_VIEW_COUNT *0.01/ SAMPLE_GROUP_NUM); //每次更新数据延Z轴移动距离
}
