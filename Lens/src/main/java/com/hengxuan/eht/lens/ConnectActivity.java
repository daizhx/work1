package com.hengxuan.eht.lens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hengxuan.eht.lens.Utils.LenUtils;

/**
 * Created by Administrator on 2014/11/19.
 */
public class ConnectActivity extends ActionBarActivity {
    private ImageButton btnSetting;
    private static int REQUEST_CODE = 520;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_activity_connect);
        ImageView leftIcon = (ImageView) findViewById(R.id.left_icon);
        leftIcon.setImageResource(R.drawable.up_btn);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_up);

        btnSetting = (ImageButton) findViewById(R.id.wifi_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE){
            if(LenUtils.isConnectEHT(ConnectActivity.this)){
                startActivity(new Intent(ConnectActivity.this, LensMainActivity.class));
                finish();
            }else{
                //TODO
            }
        }
    }
}
