package com.hengxuan.eht.massager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.preference.CheckBoxPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.hengxuan.eht.data.PrefsData;
import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.utils.CommonUtil;

import java.io.IOException;
import java.io.InputStream;


public class guide extends Activity {

    private CheckBox checkBox;
    private boolean guideFlag = true;

    //去主页
    private Button mBtnGoHome;
    private ImageView img1,img2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    guideFlag = false;
                }else{
                    guideFlag = true;
                }
            }
        });
        mBtnGoHome = (Button)findViewById(R.id.btn_go_home);
        mBtnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(guide.this, MainActivity.class));
                finish();
            }
        });

        img1 = (ImageView)findViewById(R.id.img1);
        img2 = (ImageView)findViewById(R.id.img2);

        if(CommonUtil.getLocalLauguage(this) == 1) {
            decodeBmpFromAssets(img1,"guide/guide1.png");
            decodeBmpFromAssets(img2,"guide/guide2.png");
        }else if(CommonUtil.getLocalLauguage(this) == 2){
            decodeBmpFromAssets(img1,"guide/guide1.png");
            decodeBmpFromAssets(img2,"guide/guide2.png");
        }else{
            decodeBmpFromAssets(img1,"guide/guide1_en.png");
            decodeBmpFromAssets(img2,"guide/guide2_en.png");
        }
    }

    private void decodeBmpFromAssets(ImageView iv, String file){
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(file);
            Bitmap bmp = BitmapFactory.decodeStream(is);
            int w = getWindowManager().getDefaultDisplay().getWidth();
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            float ratio = (float)w/bmpWidth;
            Matrix m = new Matrix();
            m.setScale(ratio,ratio);
            Bitmap newBmp = Bitmap.createBitmap(bmp,0,0,bmpWidth,bmpHeight,m,false);
            iv.setImageBitmap(newBmp);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void decodeBmp(ImageView iv,int resId){
        InputStream is = getResources().openRawResource(resId);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        int w = getWindowManager().getDefaultDisplay().getWidth();
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        float ratio = (float)w/bmpWidth;
        Matrix m = new Matrix();
        m.setScale(ratio,ratio);
        Bitmap newBmp = Bitmap.createBitmap(bmp,0,0,bmpWidth,bmpHeight,m,false);
        iv.setImageBitmap(newBmp);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sp = getSharedPreferences(PrefsData.CONFIG, Context.MODE_PRIVATE);
        sp.edit().putBoolean(PrefsData.CONFIG_OPEN_GUID, guideFlag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(guide.this, MainActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
