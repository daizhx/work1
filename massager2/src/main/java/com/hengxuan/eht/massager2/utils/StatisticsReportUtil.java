package com.hengxuan.eht.massager2.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.hengxuan.eht.massager2.logger.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class StatisticsReportUtil {

	private static final String DEVICE_INFO_STR = "deviceInfoStr";
	private static final String DEVICE_INFO_UUID = "uuid";
	private static boolean already;
	private static String deivceUUID;
	private static String macAddress;
	private static CommonUtil.MacAddressListener macAddressListener = new CommonUtil.MacAddressListener() {
		public synchronized void setMacAddress(String s) {
			
			StatisticsReportUtil.macAddress = s;
			StatisticsReportUtil.already = true;
			notifyAll();
		}
	};
	private static String paramStr;
	private static String paramStrWithOutDeviceUUID;
	

	public StatisticsReportUtil()
	{
	}

	public static String getDeviceInfoStr(Context context)
	{
		
		JSONObject jsonobject = new JSONObject();
		String s = readDeviceUUID(context);
		
		String s1 = spilitSubString(Build.MANUFACTURER, 12);
		String s2 = spilitSubString(Build.MODEL, 12);
		
		try {
			jsonobject.put("uuid", s);
			jsonobject.put("platform", 100);
			jsonobject.put("brand", s1);
			jsonobject.put("model", s2);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}		

		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		String s3 = String.valueOf(display.getHeight());
		StringBuilder stringbuilder = (new StringBuilder(s3)).append("*");
		
		String s4 = stringbuilder.append(display.getWidth()).toString();

		 try {
			jsonobject.put("screen", s4);
			jsonobject.put("clientVersion", getSoftwareVersionName(context));
			jsonobject.put("osVersion",  Build.VERSION.RELEASE);
//			jsonobject.put("partner",  Configuration.getProperty("partner", ""));
			jsonobject.put("nettype", getNetworkTypeName(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonobject.toString();
	}

	public static String getNetworkTypeName(Context context)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getNetworkTypeName");
		}
		
		TelephonyManager telephonymanager;
		String s;
		NetworkInfo anetworkinfo[];
		int i;
		ConnectivityManager connectivitymanager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		telephonymanager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		s = null;
		anetworkinfo = connectivitymanager.getAllNetworkInfo();
		i = 0;
		
		for(i = 0; i < anetworkinfo.length; i++) {
			if (anetworkinfo[i].isConnected())
				if (anetworkinfo[i].getTypeName().toUpperCase().contains("MOBILE") ||  (anetworkinfo[i].getTypeName().toUpperCase().contains("WIFI")))
				{
					s = String.valueOf(telephonymanager.getNetworkType());
					break;
				} 
		}
		
		if(i >=  anetworkinfo.length)
			s =  "UNKNOWN";
		return s;
	}

	private static PackageInfo getPackageInfo(Context context)
	{
		if (Log.D) {
			Log.d("StatisticsReportUtil", "getPackageInfo");
		}
		
		PackageInfo packageinfo;
		PackageManager packagemanager = context.getPackageManager();
        String s = context.getPackageName();
		try {
			packageinfo = packagemanager.getPackageInfo(s , 0);
		} catch (NameNotFoundException e) {
			packageinfo = null;
			e.printStackTrace();
		}
		return packageinfo;
	}

	private static String getParamStr(Context context)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getParamStr");
		}
		
		String s2;
		if (!TextUtils.isEmpty(paramStr))
		{
			if (Log.D)
			{
				StringBuilder stringbuilder = new StringBuilder("getParamStr() -->> ");
				String s = paramStr;
				String s1 = stringbuilder.append(s).toString();
				Log.d("Temp", s1);
			}
			s2 = paramStr;
		} else
		{
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append("&uuid=");
			stringbuffer.append(readDeviceUUID(context));
			stringbuffer.append(getParamStrWithOutDeviceUUID(context));
			paramStr = stringbuffer.toString();
			if (Log.D)
			{
				StringBuilder stringbuilder1 = new StringBuilder("getParamStr() create -->> ");
				String s5 = paramStr;
				String s6 = stringbuilder1.append(s5).toString();
				Log.d("Temp", s6);
			}
			s2 = paramStr;
		}
		return s2;
	}

	private static String getParamStrWithOutDeviceUUID(Context context)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getParamStrWithOutDeviceUUID");
		}
		
		String s2;
		if (!TextUtils.isEmpty(paramStrWithOutDeviceUUID))
		{
			if (Log.D)
			{
				StringBuilder stringbuilder = new StringBuilder("getParamStrWithOutDeviceUUID() -->> ");
				String s = paramStrWithOutDeviceUUID;
				String s1 = stringbuilder.append(s).toString();
				Log.d("Temp", s1);
			}
			s2 = paramStrWithOutDeviceUUID;
		} else
		{
			StringBuffer stringbuffer = new StringBuffer();
			StringBuffer stringbuffer1 = stringbuffer.append("&clientVersion=");
			String s3 = spilitSubString(getSoftwareVersionName(context), 12);
			StringBuffer stringbuffer2 = stringbuffer1.append(s3);
			StringBuffer stringbuffer3 = stringbuffer.append("&client=").append("android");
			StringBuffer stringbuffer4 = stringbuffer.append("&osVersion=");
			String s4 = spilitSubString(Build.VERSION.RELEASE, 12);
			StringBuffer stringbuffer5 = stringbuffer4.append(s4);
			Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			StringBuffer stringbuffer6 = stringbuffer.append("&screen=");
			String s5 = String.valueOf(display.getHeight());
			StringBuilder stringbuilder1 = (new StringBuilder(s5)).append("*");
			int i = display.getWidth();
			String s6 = stringbuilder1.append(i).toString();
			StringBuffer stringbuffer7 = stringbuffer6.append(s6);
			paramStrWithOutDeviceUUID = stringbuffer.toString();
			if (Log.D)
			{
				StringBuilder stringbuilder2 = new StringBuilder("getParamStrWithOutDeviceUUID() create -->> ");
				String s7 = paramStrWithOutDeviceUUID;
				String s8 = stringbuilder2.append(s7).toString();
				Log.d("Temp", s8);
			}
			s2 = paramStrWithOutDeviceUUID;
		}
		return s2;
	}

	public static String getReportString(Context context,boolean flag)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getReportString");
		}
		
		String s;
		if (flag || getValidDeviceUUIDByInstant() != null)
			s = getParamStr(context);
		else
			s = getParamStrWithOutDeviceUUID(context);
		return s;
	}

	public static int getSoftwareVersionCode(Context context)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getSoftwareVersionCode");
		}
		
		PackageInfo packageinfo = getPackageInfo(context);
		int i;
		if (packageinfo == null)
			i = 0;
		else
			i = packageinfo.versionCode;
		return i;
	}

	public static String getSoftwareVersionName(Context context)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getSoftwareVersionName");
		}
		
		PackageInfo packageinfo = getPackageInfo(context);
		String s;
		if (packageinfo == null)
			s = "";
		else
			s = packageinfo.versionName;
		return s;
	}

	private static String getValidDeviceUUIDByInstant()
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "getValidDeviceUUIDByInstant");
		}
		
		String s;
		if (!TextUtils.isEmpty(deivceUUID))
		{
			s = deivceUUID;
		} else
		{
            String s1 = null;
//			String s1 = CommonUtil.getGySharedPreferences().getString("uuid", null);
			if (isValidDeviceUUID(s1))
			{
				deivceUUID = s1;
				s = deivceUUID;
			} else
			{
				s = null;
			}
		}
		return s;
	}

	private static boolean isValidDeviceUUID(String s)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "isValidDeviceUUID");
		}
		
		boolean flag;
		if (TextUtils.isEmpty(s))
		{
			flag = false;
		} else
		{
			String as[] = s.split("-");
			if (as.length > 1)
			{
				if (TextUtils.isEmpty(as[1]))
					flag = false;
				else
					flag = true;
			} else
			{
				flag = false;
			}
		}
		return flag;
	}

	public static String readDeviceUUID(Context context)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "readDeviceUUID");
		}
		
		String s = getValidDeviceUUIDByInstant();
		if(s != null) {
			if (Log.D)
			{
				String s1 = (new StringBuilder("readDeviceUUID() read deivceUUID -->> ")).append(s).toString();
				Log.d("Temp", s1);
			}
			return s;
		} else {
			StringBuilder stringbuilder;
			String s2;
			String s3;
			if (Log.D)
				Log.d("Temp", "readDeviceUUID() create -->> ");
			stringbuilder = new StringBuilder();
			s2 = CommonUtil.getDeviceId(context);
			if (!TextUtils.isEmpty(s2))
				s2 = s2.trim().replaceAll("-", "");
			s3 = macAddress;
			if (s3 != null) {
				if (!TextUtils.isEmpty(s3))
					s3 = s3.trim().replaceAll("-|\\.|:", "");
				if (!TextUtils.isEmpty(s2))
					stringbuilder.append(s2);
				stringbuilder.append("-");
				if (!TextUtils.isEmpty(s3))
					stringbuilder.append(s3);
				s = stringbuilder.toString();
				if (isValidDeviceUUID(s))
				{
					if (Log.D)
						Log.d("Temp", "readDeviceUUID() write -->> ");
//					boolean flag = CommonUtil.getGySharedPreferences().edit().putString("uuid", s).commit();
				}
				if (Log.D)
				{
					String s5 = (new StringBuilder("readDeviceUUID() create deivceUUID -->> ")).append(s).toString();
					Log.d("Temp", s5);
				}
			} else {
				CommonUtil.getLocalMacAddress(context, macAddressListener);
				if (!already)
				{
					if (Log.D)
						Log.d("Temp", "mac wait start -->> ");
					try {
						synchronized(macAddressListener) {
						macAddressListener.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (Log.D)
						Log.d("Temp", "mac wait end -->> ");
				}
			}
		}
		return s;
	}

	private static String spilitSubString(String s, int i)
	{
		if (Log.D) { 
			Log.d("StatisticsReportUtil", "spilitSubString");
		}
		
		if (s != null && s.length() > i)
			s = s.substring(0, i);
		return s;
	}

}
