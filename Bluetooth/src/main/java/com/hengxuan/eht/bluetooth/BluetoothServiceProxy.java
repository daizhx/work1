package com.hengxuan.eht.bluetooth;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class BluetoothServiceProxy {
    public static final String TAG = "BluetoothServiceProxy";
	public static String name;
	public static String mac;
	public static int i = 0;
	public static BluetoothSocket btSocket;
	public static OutputStream outStream ;
    //蓝牙是否打开
	public static boolean open_flag = false;
    //是否是音乐按摩模式
	public static boolean music_flag = false;
    //是否连接设备
    public static boolean connect_flag = false;
    /*
     * 编码规则：(两个字节，第一个字节代表标志，第二个字节指令内容)
     * 按摩机设置(00):0x0010~0x0100(size = 240)
     * **模式设置：0x0011~0x0030(size = 32, (火罐:0x0011,推拿:0x0012,锤击:0x0013,针灸:0x0014,按摩:0x0015,刮痧:0x0016,自动:0x0017音乐:x00018))
     * **强度设置：0x0031~0x0050(size = 32,(0:0x0031,~10:0x003b,~14:0x003f,15~0x0040,16:0x0041))
     * **时间设置：0x0051~0x0070(size = 32,(0:0x0051,5:0x0052,10:0x0053,15:0x0054,20:0x0055,25:0x0056,30:0x0057,35:0x0058,40:0x0059,45:0x005a,50:0x005b,55:0x005c,60:0x005d))
     * **频率设定：0x0071~0x0080(size= 16,(0x0071:低频,0x0072:高频))
     * **
     * 数据传输：0x1100(11代表数据传输,00为要传输的数据内容)
    */
	public static final short MODE_TAG_1 = 0x0011;//火罐
	public static final short MODE_TAG_2 = 0x0012;//推拿
	public static final short MODE_TAG_3 = 0x0013;//锤击
	public static final short MODE_TAG_4 = 0x0014;//针灸
	public static final short MODE_TAG_5 = 0x0015;//按摩
	public static final short MODE_TAG_6 = 0x0016;//刮痧
	public static final short MODE_TAG_7 = 0x0017;//自动
	public static final short MODE_TAG_8 = 0x0018;//音乐

    public static final short[] MODES = new short[]{
      MODE_TAG_1,MODE_TAG_2,MODE_TAG_3,MODE_TAG_4,MODE_TAG_5,MODE_TAG_6,MODE_TAG_7,MODE_TAG_8
    };
	
	public static final short STRENGTH_TAG = 0x0031;
	
	public static final short TIME_TAG = 0x0051;
	
	public static final short H_FR_TAG = 0x0071;
	public static final short L_FR_TAG = 0x0072;
	BluetoothServiceProxy()
	{
		name = null;
		mac = null;
		btSocket = null;
		outStream = null;
	}

    /**
     * 判断是否是音乐按摩模式，非音乐模式返回true，音乐模式返回false
     * @param command
     * @return boolean
     */
	static private boolean isChangeMassageMode(short command){
		if((command == MODE_TAG_1) ||
				(command == MODE_TAG_2) ||
				(command == MODE_TAG_3) ||
				(command == MODE_TAG_4) ||
				(command == MODE_TAG_5) ||
				(command == MODE_TAG_6) ||
				(command == MODE_TAG_7) ){
			return true;
		}
		return false;
	}
	
	private static byte[] shortToByteArray(short s) {
		   byte[] shortBuf = new byte[2];
		   for(int i=0;i<2;i++) {
		   int offset = (shortBuf.length - 1 -i)*8;
		   shortBuf[i] = (byte)((s>>>offset)&0xff);
		   }
		   return shortBuf;
	}

	static public Boolean sendCommandToDevice(short command)
	{
		StringBuilder strBuilder = new StringBuilder("command = ").append(command);
		Log.e("BlueTooth", "sendCommandToDevice()-" + strBuilder.toString());

			if(btSocket != null)
			{
				if(command >= 0x1100)
				{
					if(music_flag)
					{
						try{
							outStream = btSocket.getOutputStream();
							byte[] msgBuffer = shortToByteArray(command);
							outStream.write(msgBuffer);
                            return true;
						}catch(Exception e){
							disconnectBluetooth();
						}
					}
				} else {
					try{
						if(command == MODE_TAG_8) {
							music_flag = true;
						}
						if(isChangeMassageMode(command)) {
							music_flag = false;
						}
						
						outStream = btSocket.getOutputStream();
						byte[] msgBuffer = shortToByteArray(command);
						outStream.write(msgBuffer);
                        return true;
					}catch(Exception e){
						disconnectBluetooth();
					}
				}
			}
        //socket不可用 or socketIO异常
        Log.e(TAG, "sendCommandToDevice()-" + strBuilder.toString() + " fail!"+"music_flag="+music_flag);
		return false;
	}

    /**
     * 关闭蓝牙数据连接通道btsocket,清除数据
     */
	static public void disconnectBluetooth()
	{
		if(btSocket != null)
		{
			if (outStream != null) {
                try {
                	outStream.flush();
                	outStream = null;
                } catch (IOException e) {
                        e.printStackTrace();
                }
			}

			try {
				btSocket.close();
			} catch (IOException e2) {
                e2.printStackTrace();
			}
			
			btSocket = null;
            name = null;
            mac = null;
            connect_flag = false;
		}
	}

	static public Boolean isconnect()
	{
		if(btSocket != null){
            if(Build.VERSION.SDK_INT >= 14) {
                if (btSocket.isConnected()) {
                    return true;
                }
            }else{
                return true;
            }
		}
		return false;
	}


}
