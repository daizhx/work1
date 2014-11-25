package com.hengxuan.eht.lens.Utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Administrator on 2014/11/19.
 */
public class LenUtils {
    /**
     * 是否连接了镜头
     * @param context
     * @return
     */
    public static boolean isConnectEHT(Context context){
        WifiManager mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if(wifiInfo == null){
            return false;
        }
        if(wifiInfo.getSSID().equals("\"" + "EHT"
                + "\"")){
            return true;
        }else{
            return false;
        }
    }
}
