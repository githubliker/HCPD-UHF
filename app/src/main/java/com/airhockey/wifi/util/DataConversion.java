package com.airhockey.wifi.util;

import java.math.BigInteger;

public class DataConversion {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte) Integer.parseInt(inHex,16);
    }

    /**
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] buf = new char[bytes.length * 3];
        int index = 0;
        for(byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
            buf[index++] = ' ';
        }

        return new String(buf);
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 基数可以转换进制的范围(2-36)，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }
}
