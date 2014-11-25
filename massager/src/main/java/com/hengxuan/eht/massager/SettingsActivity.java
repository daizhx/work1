package com.hengxuan.eht.massager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hengxuan.eht.massager.R;
import com.hengxuan.eht.user.LoginActivity;
import com.hengxuan.eht.user.User;

public class SettingsActivity extends BaseActivity {

    //修改密码
    private View resetPW;
    //退出登录
    private TextView tvExitView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.action_settings);
        resetPW = findViewById(R.id.item1);
        resetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(SettingsActivity.this, ResetPWActivity.class));
            }
        });
        tvExitView = (TextView) findViewById(R.id.tv_logout);
        if(!User.isLogin){
            tvExitView.setText(R.string.login);
        }
        tvExitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(User.isLogin) {
                    User.Logout(SettingsActivity.this);
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                }else{
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                }
            }
        });
    }

}
