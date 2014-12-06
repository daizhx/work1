package com.hengxuan.eht.massager2;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import com.hengxuan.eht.massager2.data.Constants;
import com.hengxuan.eht.massager2.splash.SplashActivity;

/**
 * Created by Administrator on 2014/12/5.
 */
public class MyApplication extends Application {
    private boolean isNewInstall;
    private boolean isNewVersion;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            int version = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
            int oldVersion = settings.getInt(Constants.VERSION_CODE, -1);
//            if(oldVersion == -1 || version > oldVersion){
//                Intent intent = new Intent(this, SplashActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                settings.edit().putInt(Constants.VERSION_CODE,version).commit();
//            }else{
//                Intent intent = new Intent(this, SplashActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }
}
