package com.hengxuan.eht.massager2.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

import com.hengxuan.eht.massager2.logger.Log;

import java.lang.Character.UnicodeBlock;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Pattern;

public class CommonUtil {

	public interface BrowserUrlListener {

		public abstract void onComplete(String s);
	}

	public interface MacAddressListener {

		public abstract void setMacAddress(String s);
	}

	public CommonUtil() {
	}

	public static boolean CheckNetWork(Context context) {
		if (Log.D) {
			Log.d("CommonUtil", "CheckNetWork");
		}

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

	public static void backToMain(Context context) {
		if (Log.D) {
			Log.d("CommonUtil", "backToMain");
		}

		try {
			PackageManager packagemanager = context.getPackageManager();
			Intent intent = (new Intent("android.intent.action.MAIN"))
					.addCategory("android.intent.category.HOME");
			ActivityInfo activityinfo = packagemanager.resolveActivity(intent,
					0).activityInfo;
			Intent intent1 = new Intent("android.intent.action.MAIN");
			intent1.addCategory("android.intent.category.LAUNCHER");
			ComponentName componentname = new ComponentName(
					activityinfo.packageName, activityinfo.name);
			intent1.setComponent(componentname);
			intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
		} catch (ActivityNotFoundException exception) {
			exception.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkAddrWithSpace(String s) {
		return startCheck("[\\wһ-��\\-\\x20]+", s);
	}

	public static boolean checkEmailWithSuffix(String s) {
		return startCheck(
				"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$",
				s);
	}

	public static int checkNetWorkType() {
		if (Log.D) {
			Log.d("CommonUtil", "checkNetWorkType");
		}

		byte newworkType;
		if (Proxy.getDefaultHost() != null)
			newworkType = 2;
		else
			newworkType = 1;

		return newworkType;
	}

    /**
     * 检查输入的密码是否符合约定的规则
     * @param s
     * @param i 最少长度
     * @param j 最大长度
     * @return true
     */
	public static boolean checkPassword(String s, int i, int j) {
		return startCheck((new StringBuilder("[a-zA-Z_0-9\\-]{")).append(i)
				.append(",").append(j).append("}").toString(), s);
	}

	public static boolean checkSDcard() {
		boolean flag;
		if (Environment.getExternalStorageState().equals("mounted"))
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean checkUsername(String s) {
		return startCheck("[\\wһ-��\\-a-zA-Z0-9_]+", s);
	}

	public static boolean checkUsername(String s, int i) {
		return startCheck(
				(new StringBuilder("[\\wһ-��\\-a-zA-Z0-9_]{")).append(i)
						.append(",}").toString(), s);
	}

	public static boolean checkUsername(String s, int i, int j) {
		return startCheck(
				(new StringBuilder("[\\wһ-��\\-a-zA-Z0-9_]{")).append(i)
						.append(",").append(j).append("}").toString(), s);
	}

    //获取手机IMEI号
	public static String getDeviceId(Context context) {
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	public static int getLength(String s) {
		char ac[] = s.toCharArray();
		int i = 0;
		int j = 0;
		do {
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

    //获取手机wifi mac地址
	public static void getLocalMacAddress(Context context, final MacAddressListener listener) {
		if (Log.D) {
			Log.d("CommonUtil", "getLocalMacAddress");
		}
		final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String s = wifi.getConnectionInfo().getMacAddress();
		if (Log.D) {
			String s1 = (new StringBuilder(
					"getMacAddress() macAddress without open -->> ")).append(s)
					.toString();
			Log.d("Temp", s1);
		}

		if (s == null) {
			final Object waiter = new Object();
			(new Thread() {

				@Override
				public void run() {
					if (Log.D)
						Log.d("Temp", "run() -->> ");
					boolean flag = wifi.setWifiEnabled(true);
					if (Log.D)
						Log.d("Temp", "run() setWifiEnabled -->> true");
					int i = 0;
					String s1 = wifi.getConnectionInfo().getMacAddress();
					while (true) {
						if (s1 != null || i >= 5) {
							boolean flag1 = wifi.setWifiEnabled(false);
							if (Log.D)
								Log.d("Temp", "run() setWifiEnabled -->> false");

							if (Log.D) {
								String s2 = (new StringBuilder(
										"getMacAddress() macAddress with open -->> "))
										.append(s1).toString();
								Log.d("Temp", s2);
							}
							listener.setMacAddress(s1);
							return;
						}

						i++;
						Object obj = waiter;

						if (Log.D)
							Log.d("Temp",
									"getMacAddress() wait start 500 -->> ");

						try {
							synchronized (waiter) {
								waiter.wait(500L);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						if (Log.D)
							Log.d("Temp", "getMacAddress() wait end 500 -->> ");
					}
				}
			}).start();
		} else {
			listener.setMacAddress(s);
		}
	}

	public static boolean isChinese(char c) {
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

//	public static void queryBrowserUrl(String s, final URLParamMap params,
//			final BrowserUrlListener listener) {
//		if (Log.D) {
//			Log.d("CommonUtil", "queryBrowserUrl");
//		}
//
//		HttpGroupSetting httpgroupsetting = new HttpGroupSetting();
//		httpgroupsetting.setType(1000);
//		HttpGroupaAsynPool httpgroupaasynpool = new HttpGroupaAsynPool(
//				httpgroupsetting);
//		HttpSetting httpsetting = new HttpSetting();
//		httpsetting.setFunctionId("genToken");
//		httpsetting.putJsonParam("action", s);
//		httpsetting.setListener(new HttpGroup.OnCommonListener() {
//			public void onEnd(HttpResponse httpresponse) {
//				if (Log.D) {
//					Log.d("CommonUtil", "queryBrowserUrl.httpsetting.onEnd");
//				}
//				if (httpresponse != null
//						&& httpresponse.getJSONObject() != null) {
//					JSONObjectProxy jsonobjectproxy = httpresponse
//							.getJSONObject();
//					String s = jsonobjectproxy.getStringOrNull("tokenKey");
//					if (s == null)
//						onError(null);
//
//					String s1 = jsonobjectproxy.getStringOrNull("url");
//					if (s1 == null)
//						onError(null);
//
//					params.put("tokenKey", s);
//					String s3 = HttpGroup.mergerUrlAndParams(s1, params);
//					if (Log.D) {
//						String s4 = (new StringBuilder(
//								"queryBrowserUrl() mergerUrl -->> "))
//								.append(s3).toString();
//						Log.d("Temp", s4);
//					}
//
//					listener.onComplete(s3);
//				}
//			}
//
//			public void onError(HttpError httperror) {
//			}
//
//			public void onReady(HttpSettingParams httpsettingparams) {
//			}
//		});
//		httpsetting.setNotifyUser(true);
//		httpgroupaasynpool.add(httpsetting);
//	}

	public static boolean startCheck(String s, String s1) {
		return Pattern.compile(s).matcher(s1).matches();
	}

//	public static void toBrowser(String s, URLParamMap urlparammap) {
//		if (Log.D) {
//			Log.d("CommonUtil", "toBrowser");
//		}
//
//		queryBrowserUrl(s, urlparammap, new BrowserUrlListener() {
//			public void onComplete(String s) {
//				Uri uri = Uri.parse(s);
//				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				EHTApplication.getInstance().startActivity(intent);
//			}
//		});
//	}

    //获取本地ip地址
	public String getLocalIpAddress() {
		if (Log.D) {
			Log.d("CommonUtil", "getLocalIpAddress");
		}

		try {
			Enumeration enumeration = NetworkInterface.getNetworkInterfaces();

			while (enumeration.hasMoreElements()) {
				Enumeration enumeration1 = ((NetworkInterface) enumeration
						.nextElement()).getInetAddresses();

				while (enumeration1.hasMoreElements()) {
					InetAddress inetaddress = (InetAddress) enumeration1
							.nextElement();
					if (!inetaddress.isLoopbackAddress()) {
						return inetaddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException exception) {
			//TODO
		}

		return null;
	}

//	public static void verifyProduct(final Context context, Product product){
//		isTransferSequenceCode(context, product.mEntryIntent, product.mTypeId, product.theCode, product.clientCode);
//	}


//	public static boolean isTransferSequenceCode(final Context getContext,
//			final String intentString, final int getcodeid, final String thecode,
//			final String clientCode) {
//		final String getUserId = ((BaseActivity) getContext)
//				.getStringFromPreference(ConstHttpProp.USER_PIN);
//
//		final Dialog dialog = new Dialog(getContext, R.style.dialog2);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.show();
//		Window window = dialog.getWindow();
//		window.setContentView(R.layout.dialog_input);
//
//		// final EditText codeet = (EditText) window.findViewById(R.id.getcode);
//		final EditText numet = (EditText) window.findViewById(R.id.getnum);
//		final Button okbtn = (Button) window.findViewById(R.id.mybtn);
//		numet.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				// TODO Auto-generated method stub
//				okbtn.setEnabled(true);
//			}
//		});
//		okbtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if (!TextUtils.isEmpty(numet.getText().toString())) {
//					verificationCode(getUserId, getcodeid, numet.getText()
//							.toString(), getContext, intentString, thecode,
//							clientCode);
//				}
//				dialog.dismiss();
//			}
//		});
//		Button canclebtn = (Button) window.findViewById(R.id.mybtnCancle);
//		canclebtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//		});
//		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//
//		});
//		return true;
//	}
//
//	public static void verificationCode(String userPin, int code,
//			String serialNum, final Context getContext, final String intentString,
//			final String thecode, final String clientCode) {
//		HttpSetting httpsetting = new HttpSetting();
//		httpsetting.setFunctionId(ConstFuncId.SERIALNUM);
//		httpsetting.putJsonParam("userPin", userPin);
//		httpsetting.putJsonParam("client_code", clientCode);
//		httpsetting.putJsonParam("equipment_code", code);
//		httpsetting.putJsonParam("serialNum", serialNum);
//		httpsetting.setListener(new HttpGroup.OnAllListener() {
//
//			@Override
//			public void onStart() {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onEnd(HttpResponse response) {
//				// TODO Auto-generated method stub
//				if (response.getJSONObject() != null) {
//					try {
//						final String result_code = response.getJSONObject()
//								.getString("code");
//						Handler handler = new Handler(((Activity)getContext).getMainLooper());
//						handler.post(new Runnable() {
//
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								if (result_code.equals("1")) {
//									((BaseActivity) getContext)
//											.putBoolean2Preference(thecode,
//													true);
//									Toast.makeText(
//											getContext,
//											getContext.getResources()
//													.getString(
//															R.string.verified),
//											Toast.LENGTH_LONG).show();
//									Intent intent = new Intent(intentString);
//									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//									getContext.startActivity(intent);
//								} else {
//									Toast.makeText(
//											getContext,
//											getContext
//													.getResources()
//													.getString(
//															R.string.verify_failed),
//											Toast.LENGTH_SHORT).show();
//								}
//							}
//						});
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//
//			@Override
//			public void onError(HttpError httpError) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onProgress(int i, int j) {
//				// TODO Auto-generated method stub
//
//			}
//
//		});
//		httpsetting.setNotifyUser(true);
//		httpsetting.setShowProgress(true);
//		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
//		localHttpGroupSetting.setMyActivity((Activity)getContext);
//		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSON);
//		HttpGroupaAsynPool httpGroupaAsynPool = new HttpGroupaAsynPool(
//				localHttpGroupSetting);
//		httpGroupaAsynPool.add(httpsetting);
//	}


    /**
     * 获取本地语言环境，中文简单返回1，中文繁体返回2，英语返回3，其他返回0
     * @return
     */
    public static int getLocalLauguage(Context context){
        int code = 0;
        Locale local = context.getResources().getConfiguration().locale;
        String s = local.getLanguage();
        String contry = local.getCountry();
        if(s.equals("en")){
            code = 3;
        }else if(s.equals("zh")){
            if(contry.equals("TW")){
                code = 2;
            }else {
                code = 1;
            }
        }
        return code;
    }
}
