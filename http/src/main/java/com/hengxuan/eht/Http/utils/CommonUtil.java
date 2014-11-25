package com.hengxuan.eht.Http.utils;

import java.lang.Character.UnicodeBlock;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupSetting;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.json.JSONObjectProxy;


public class CommonUtil {


	public interface BrowserUrlListener
	{

		public abstract void onComplete(Context c, String s);
	}

	public interface MacAddressListener
	{

		public abstract void setMacAddress(String s);
	}

	public CommonUtil()
	{
	}
	
	public static boolean CheckNetWork(Context context) {
		
		ConnectivityManager localConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		int i = 0;
		if (localConnectivityManager == null) {
			return false;
		}
		NetworkInfo[] arrayOfNetworkInfo;
		arrayOfNetworkInfo = localConnectivityManager.getAllNetworkInfo();
		if (arrayOfNetworkInfo == null) {
			return false;
		}
		int k = arrayOfNetworkInfo.length;
		for (i = 0; i < k; i++) {
			if (arrayOfNetworkInfo[i].isConnected())
				break;
		}
		if (i >= k)
			return false;
		else
			return true;
	}

	public static void backToMain(Context context)
	{
		
		try
		{
			PackageManager packagemanager = context.getPackageManager();
			Intent intent = (new Intent("android.intent.action.MAIN")).addCategory("android.intent.category.HOME");
			ActivityInfo activityinfo = packagemanager.resolveActivity(intent, 0).activityInfo;
			Intent intent1 = new Intent("android.intent.action.MAIN");
			intent1.addCategory("android.intent.category.LAUNCHER");
			ComponentName componentname = new ComponentName(activityinfo.packageName, activityinfo.name);
			intent1.setComponent(componentname);
			intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
		}
		catch(ActivityNotFoundException exception)
		{
			exception.printStackTrace();
		}
		catch(SecurityException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean checkAddrWithSpace(String s)
	{
		return startCheck("[\\wһ-��\\-\\x20]+", s);
	}
	
	public static boolean checkEmailWithSuffix(String s)
	{
		return startCheck("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", s);
	}
	
	public static int checkNetWorkType() {
		
		byte newworkType;
		if (Proxy.getDefaultHost() != null)
			newworkType = 2;
		else
			newworkType = 1;
		
		return newworkType;
	}

	public static boolean checkPassword(String s, int i, int j)
	{
		return startCheck((new StringBuilder("[a-zA-Z_0-9\\-]{")).append(i).append(",").append(j).append("}").toString(), s);
	}

	public static boolean checkSDcard()
	{
		boolean flag;
		if (Environment.getExternalStorageState().equals("mounted"))
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean checkUsername(String s)
	{
		return startCheck("[\\wһ-��\\-a-zA-Z0-9_]+", s);
	}

	public static boolean checkUsername(String s, int i)
	{
		return startCheck((new StringBuilder("[\\wһ-��\\-a-zA-Z0-9_]{")).append(i).append(",}").toString(), s);
	}

	public static boolean checkUsername(String s, int i, int j)
	{
		return startCheck((new StringBuilder("[\\wһ-��\\-a-zA-Z0-9_]{")).append(i).append(",").append(j).append("}").toString(), s);
	}
	
	public static String getDeviceId(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

//	public static SharedPreferences getGySharedPreferences(Context context) {
//		return context.getSharedPreferences(ConstSysConfig.SYS_CUST_CLIENT, 0);
//	}
	
	public static int getLength(String s)
	{
		char ac[] = s.toCharArray();
		int i = 0;
		int j = 0;
		do
		{
			int k = ac.length;
			if (j >= k)
				return i;
			if (isChinese(ac[j]))
				i += 2;
			else
				i++;
			j++;
		} while (true);
	}
	
	public  static void getLocalMacAddress(final MacAddressListener listener, Context context)
	{
		
		final WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		String s = wifi.getConnectionInfo().getMacAddress();
		
		
		if (s == null) {
			final Object waiter = new Object();
			(new Thread() {
				
				@Override
				public void run() {
					boolean flag = wifi.setWifiEnabled(true);
					int i = 0;
					String s1 = wifi.getConnectionInfo().getMacAddress();
					while(true) {
					if (s1 != null || i >= 5)
					{
						boolean flag1 = wifi.setWifiEnabled(false);
						listener.setMacAddress(s1);
						return;
					}
					
					i++;
					Object obj = waiter;
					
					try {
						synchronized(waiter) {
							waiter.wait(500L);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				}
			}).start();
		} else {
			listener.setMacAddress(s);
		}
	}

	public static boolean isChinese(char c)
	{
		UnicodeBlock i = UnicodeBlock.of(c);
		UnicodeBlock j = UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
		if (i == j) 
			return true;

		UnicodeBlock k = UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS;
		if (i == k)
			return true;

		UnicodeBlock l = UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
		if (i == l)
			return true;

		UnicodeBlock i1 = UnicodeBlock.GENERAL_PUNCTUATION;
		if (i == i1)
			return true;

		UnicodeBlock j1 = UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION;
		if (i == j1)
			return true;

		UnicodeBlock k1 = UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
		if (i == k1)
			return true;

		return false;
	}
	
	public static void queryBrowserUrl(final Context context, String s, final URLParamMap params, final BrowserUrlListener listener)
	{	
		HttpGroupSetting httpgroupsetting = new HttpGroupSetting();
		httpgroupsetting.setType(1000);
//		HttpGroupaAsynPool httpgroupaasynpool = new HttpGroupaAsynPool(context, httpgroupsetting);
		HttpGroupaAsynPool httpgroupaasynpool = HttpGroupaAsynPool.getHttpGroupaAsynPool();
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setFunctionId("genToken");
		httpsetting.putJsonParam("action", s);
		httpsetting.setListener(new HttpGroup.OnCommonListener() {
			public void onEnd(HttpResponse httpresponse)
			{
				if(httpresponse!= null && httpresponse.getJSONObject()!= null)
				{
				JSONObjectProxy jsonobjectproxy = httpresponse.getJSONObject();
				String s = jsonobjectproxy.getStringOrNull("tokenKey");
				if (s == null)
					onError(null);
				
				String s1 = jsonobjectproxy.getStringOrNull("url");
				if (s1 == null)
					onError(null);
				
				params.put("tokenKey", s);
				String s3 = HttpGroup.mergerUrlAndParams(s1, params);
//				if (Log.D)
//				{
//					String s4 = (new StringBuilder("queryBrowserUrl() mergerUrl -->> ")).append(s3).toString();
//					Log.d("Temp", s4);
//				}
				
				listener.onComplete(context, s3);
				}
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onReady(HttpGroup.HttpSettingParams httpSettingParams) {
				// TODO Auto-generated method stub
				
			}
		});
		httpsetting.setNotifyUser(true);
		httpgroupaasynpool.add(httpsetting);
	}
	
	public static boolean startCheck(String s, String s1)
	{
		return Pattern.compile(s).matcher(s1).matches();
	}
	
	public static void toBrowser(Context context, String s, URLParamMap urlparammap)
	{	
		queryBrowserUrl(context, s, urlparammap, new BrowserUrlListener() {
			public void onComplete(Context c, String s)
			{
				Uri uri = Uri.parse(s);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				c.startActivity(intent);
			}
		});
	}
	
	public String getLocalIpAddress()
	{
		
		try 
		{
			Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
	
			while(enumeration.hasMoreElements())
			{
				Enumeration enumeration1 = ((NetworkInterface)enumeration.nextElement()).getInetAddresses();
				
				while(enumeration1.hasMoreElements())
				{
					InetAddress inetaddress = (InetAddress)enumeration1.nextElement();
					if (!inetaddress.isLoopbackAddress())
					{
						return inetaddress.getHostAddress().toString();
					}
				}
			}
		}
		catch(SocketException exception)
		{

		}
		
		return null;
	}

}
