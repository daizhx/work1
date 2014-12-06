/**
 * app����ҳ
 */

package com.hengxuan.eht.massager2.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TableLayout.LayoutParams;

import com.hengxuan.eht.massager2.MainActivity;
import com.hengxuan.eht.massager2.logger.MyUncaughtExceptionHandler;
import com.hengxuan.eht.massager2.user.User;


public class StartActivity extends Activity {
    //是否第一次安装
	private boolean isFirstStart = true;
    private boolean guideFlag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //捕捉未捕捉的异常
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(getApplicationContext()));
		ImageView iv = new ImageView(this);
//		iv.setImageResource(R.drawable.display_splash_bg);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
		setContentView(iv);

        if(getIntent().getAction().equals("massager.intent.action.ABOUT")){
            return;
        }

//        SharedPreferences sp = getSharedPreferences(PrefsData.CONFIG, Context.MODE_PRIVATE);
//        guideFlag = sp.getBoolean(PrefsData.CONFIG_OPEN_GUID, true);
//        isFirstStart = sp.getBoolean(PrefsData.CONFIG_IS_FIRST_INSTALL,true);

		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(isFirstStart){
                    Intent intent = new Intent(StartActivity.this, SplashActivity.class);
                    intent.putExtra("flag", guideFlag);
                    startActivity(intent);
				}else{
                    if(guideFlag) {
//                        startActivity(new Intent(StartActivity.this, guide.class));
                    }else{
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                    }
				}
				finish();
			}
		}, 3000);
//        boolean b = sp.edit().putBoolean(PrefsData.CONFIG_IS_FIRST_INSTALL, false).commit();
        User.init(this);
    }
}
