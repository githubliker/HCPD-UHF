package com.airhockey.wifi.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.airhockey.wifi.listener.DataCallBack;
import com.blankj.utilcode.util.NetworkUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketConHelper {
    private final static String TAG = "SocketConHelper";
    private String cmd = "EB 90 EB 90 64 09 00 00 00 C8 00 00";
    private String cmdData;
    private byte[] cmdDataByte;
    private String SERVERIP = "192.168.1.1";
    private int SERVERPORT = 8899;
    private String CLIENTIP = "192.168.1.1";
    private int CLIENTPORT = 8899;

    private InetAddress serverAddr, clientAddress;
    private DatagramSocket clinetSocket;

    private String meGetString, msSendString;
    private Client client;
    private Server server;

    private DataCallBack mCallBack;

    private static class SocketConHolder{
          private static final SocketConHelper instance=new SocketConHelper();
    }

     private SocketConHelper(){}
     public static SocketConHelper getInstance(){
         return SocketConHolder.instance;
    }
    public void initialization() {

        //获取从当客户端的IP和端口
        CLIENTIP = NetworkUtils.getIPAddress(true);
        CLIENTPORT = SERVERPORT;

        try {
            //生成address和socket
            serverAddr = InetAddress.getByName(SERVERIP);
            clientAddress = InetAddress.getByName(CLIENTIP);
            clinetSocket = new DatagramSocket(CLIENTPORT, clientAddress);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        server = new Server();
        new Thread(server).start();

    }

    public void getDataFromPC(DataCallBack callBack){
        mCallBack = callBack;
        client = new Client();
        new Thread(client).start();
    }

    public void stopClientServer(){
        try {
            client.cancel();
        }catch (Exception e){}
    }

    public void releaseAllServer(){
        try {
            client.cancel();
            server.cancel();
        }catch (Exception e){}
    }
    //客户端发送线程
    public class Client implements Runnable {
        boolean cancel = false;
        @SuppressLint("NewApi")
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                cmdData = cmd.replace(" ","");
                cmdDataByte = DataConversion.hexToByteArray(cmdData);
//                msSendString = DataConversion.bytesToHex(cmdDataByte);
//                Log.e(TAG,"send data "+cmd);
                DatagramPacket packet = new DatagramPacket(cmdDataByte, cmdDataByte.length, serverAddr, SERVERPORT);
                while (!cancel){
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    clinetSocket.send(packet);
                    Log.e(TAG,"send data time    "+System.currentTimeMillis());
                }

            } catch (Exception e) {
                e.getStackTrace();
            }

        }

        public void cancel(){
            cancel = true;
        }
    }

    //服务端接受线程
    public class Server implements Runnable {
        boolean cancel = false;
        @Override
        public void run() {

            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                while (!cancel) {
                    byte[] buf = new byte[1012];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    clinetSocket.receive(packet);
                    if (packet.getData() != null) {
                        meGetString = DataConversion.bytesToHex(packet.getData());
                        if(mCallBack != null){
//                            Log.e(TAG,"receive data "+meGetString);
                            Log.e(TAG,"receive data time "+System.currentTimeMillis());
                            boolean isAvaliable = HexDataHelper.checkDataAvaliable(meGetString);
                            if(isAvaliable){
                                mCallBack.onReceive(HexDataHelper.hexData2Array(meGetString));
                            } else {
                                Log.e(TAG,"数据校验不通过");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        public void cancel(){
            cancel = true;
        }
    }


}
