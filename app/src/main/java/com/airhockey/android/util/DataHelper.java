package com.airhockey.android.util;

import com.airhockey.android.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.airhockey.android.Constants.DATA_SPACE;
import static com.airhockey.android.Constants.SAMPLE_GROUP_NUM;
import static com.airhockey.android.Constants.X_DATA_VIEW_COUNT;
import static com.airhockey.android.Constants.SAMPLE_DATA_NUM;

public class DataHelper {

    public static void reverseData(float[] data,int start,int end){
        for(int i = start;i <= (int)((start + end) / 2);i++){
            float m = data[i];
            data[i] = data[end - (i - start)];
            data[end - (i - start)] = m;
        }
    }

    public static float[] statisticSampleData(float[][] data){
        ArrayList<Map<Float, Integer>> result = new ArrayList<>();
        for (int i = 0; i < Constants.SAMPLE_DATA_NUM; i++) {
            Map<Float, Integer> map = new HashMap<>();
            for(int j = 0; j <Constants.SAMPLE_GROUP_NUM; j++){
                float y = data[j][i];
                Integer integer = map.get(y);
                map.put(y, integer == null?1:integer+1);
            }
            result.add(map);
        }

        //一个数据分为 X Y Z R G B 6个分量
        ArrayList<Float> arrResule = new ArrayList<>();
        for(int i = 0;i<result.size();i++){
            Set<Map.Entry<Float, Integer>> set = result.get(i).entrySet();
            for (Map.Entry<Float, Integer> entry : set) {
//                System.out.println(entry.getKey() + "---" + entry.getValue());
                float y = entry.getKey();
                float x = -1.98f+i*X_DATA_VIEW_COUNT*0.01F/ SAMPLE_DATA_NUM;
                float yNum = entry.getValue();
                float[] color = genViewColorData(yNum/ Constants.SAMPLE_GROUP_NUM);
                arrResule.add(x);
                arrResule.add(y);
                arrResule.add(-2f);
                arrResule.add(color[0]);
                arrResule.add(color[1]);
                arrResule.add(color[2]);
            }
//            System.out.print("-----------------------------------\n");
        }
        float[] r = new float[arrResule.size()];
        for(int i = 0;i<arrResule.size();i++){
            r[i] = arrResule.get(i);
        }
        return r;
    }


    public static float[] genViewColorData(float y){
        float[] color = new float[3];
        if(y< 0.3){
            color[0] = 0;
            color[1] = 1;
            color[2] = 0;
        } else if(y<0.4){
            color[0] = 0.3f;
            color[1] = 0.8f;
            color[2] = 0;
        } else if (y<0.5){
            color[0] = 0.4f;
            color[1] = 0.6f;
            color[2] = 0;
        } else if(y<0.7){
            color[0] = 0.7f;
            color[1] = 0.5f;
            color[2] = 0;
        } else if(y<0.9){
            color[0] = 0.8f;
            color[1] = 0.2f;
            color[2] = 0;
        } else {
            color[0] = 1;
            color[1] = 0;
            color[2] = 0;
        }
        return color;
    }

    /*根据采样数据生成pointview 所需要的data*/
    public static float[] genPointViewData(float[] dataViewMatrix){
        float[] tempData = new float[SAMPLE_DATA_NUM * SAMPLE_GROUP_NUM];
        int j = 0;
        /*取出数组中所有的y坐标的值*/
        for(int i = 0;i<dataViewMatrix.length;i= i+DATA_SPACE){
            tempData[j++] = dataViewMatrix[i+1];
        }
        return statisticSampleData(DimensionOpration(tempData));
    }

    //横坐标从 -1.98f  到  1.42
    //z轴从1.6 到 -2
    public static float[] genOneViewData(){
        float[] newData = new float[SAMPLE_DATA_NUM *DATA_SPACE];
        Random r=new Random();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        for(int i = 0; i< SAMPLE_DATA_NUM; i++){
            float y = Float.valueOf(df.format(r.nextFloat()));
            float x = -1.98f+i*X_DATA_VIEW_COUNT*0.01F/ SAMPLE_DATA_NUM;
            float[] color = DataHelper.genViewColorData(y);
            newData[i*DATA_SPACE] = x;
            newData[DATA_SPACE*i+1] = y;
            newData[DATA_SPACE*i+2] = 1.6f;
            newData[DATA_SPACE*i+3] = color[0];
            newData[DATA_SPACE*i+4] = color[1];
            newData[DATA_SPACE*i+5] = color[2];
            newData[DATA_SPACE*i+6] = x;
            newData[DATA_SPACE*i+7] = 0.0f;
            newData[DATA_SPACE*i+8] = 1.6f;
            newData[DATA_SPACE*i+9] = color[0];
            newData[DATA_SPACE*i+10] = color[1];
            newData[DATA_SPACE*i+11] = color[2];
        }
        return newData;
    }

    public static float[] genOneViewData(float[] originalData){
        float[] newData = new float[SAMPLE_DATA_NUM *DATA_SPACE];
        Random r=new Random();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        int index = 0;
        for(int i = 0; i< SAMPLE_DATA_NUM; i++){
            float y = originalData[index++];
            float x = -2f+i*X_DATA_VIEW_COUNT*0.01F/ SAMPLE_DATA_NUM;
            float[] color = DataHelper.genViewColorData(y);
            newData[i*DATA_SPACE] = x;
            newData[DATA_SPACE*i+1] = y;
            newData[DATA_SPACE*i+2] = 1.6f;
            newData[DATA_SPACE*i+3] = color[0];
            newData[DATA_SPACE*i+4] = color[1];
            newData[DATA_SPACE*i+5] = color[2];
            newData[DATA_SPACE*i+6] = x;
            newData[DATA_SPACE*i+7] = 0.0f;
            newData[DATA_SPACE*i+8] = 1.6f;
            newData[DATA_SPACE*i+9] = color[0];
            newData[DATA_SPACE*i+10] = color[1];
            newData[DATA_SPACE*i+11] = color[2];
        }
        return newData;
    }
    //一维数组转化为二维数组
    public static float[][] DimensionOpration(float[] one){
        float[][] arr=new float[Constants.SAMPLE_GROUP_NUM][Constants.SAMPLE_DATA_NUM];
        for (int i = 0; i < Constants.SAMPLE_GROUP_NUM; i++) {
            for(int j = 0; j <Constants.SAMPLE_DATA_NUM; j++){
                arr[i][j]=one[Constants.SAMPLE_DATA_NUM * i+j];
            }
        }
        return arr;
    }
}
