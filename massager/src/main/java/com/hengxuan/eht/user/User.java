package com.hengxuan.eht.user;

import javax.crypto.spec.PSource;
import com.hengxuan.eht.logger.Log;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 保存用户基本信息及相关操作方法，在使用前调用init方法
 */
public class User {
	public static final String IS_LOGIN = "is_login";
	public static final String USER_PREFS = "user";
	public static final String USER_NAME = "user_name";
	public static final String PW = "pw";
	public static final String GENDER = "gender";
    public static final String USER_PIN = "user_pin";
	
	//�Ƿ��ѵ�¼
	public static boolean isLogin = false;
	//�û���-������prefs��
	public static String userName = null;
	//�û�����-������prefs��
	public static String password = null;

    //userpin 服务器识别用户的id号
    public static String userPin = null;
	public static String gender;

    public static void init(Context context){
        SharedPreferences sp = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        isLogin = sp.getBoolean(IS_LOGIN, false);
        userName = sp.getString(USER_NAME, "");
        password = sp.getString(PW, "");
        gender = sp.getString(GENDER, "");
        userPin = sp.getString(USER_PIN, "");
    }
	
	public static boolean isLogin(){
		return isLogin;
	}

    public static void setUserPin(Context c, String userpin){
        SharedPreferences.Editor editor = c.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(USER_PIN, userpin);
        editor.commit();
        userPin = userpin;
    }

	public static void setLogin(Context c,String name, String pw, boolean b){
		isLogin = b;
		userName = name;
		password = pw;
		SharedPreferences.Editor editor = c.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit();
		editor.putBoolean(IS_LOGIN, b);
		editor.putString(USER_NAME, name);
		editor.putString(PW, pw);
		editor.commit();
	}
	
	public static String getUserName(){
		return userName;
	}
	
	public static String getPassword(){
		return password;
		
	}

	public static void Logout(Context c){
		SharedPreferences.Editor editor = c.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
		isLogin = false;
		userName = null;
		password = null;
		gender = null;
	}

}
