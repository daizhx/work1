package com.hengxuan.eht.massager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengxuan.eht.massager2.user.User;

import java.util.Set;

/**
 * Created by Administrator on 2014/12/4.
 */
public class SettingsActivity extends BaseActivity{
    private Button btnExitLogin;
    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBarLeftIcon.setImageResource(R.drawable.ic_back);
        actionBarLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        actionBarTitle.setText(R.string.settings);
        setContentView(R.layout.activity_settings);
        btnExitLogin = (Button) findViewById(R.id.exit_btn);
        mListView = (ListView) findViewById(R.id.list);
        if(User.isLogin){
            btnExitLogin.setTextColor(getResources().getColor(R.color.red));
        }
        String[] listItems = new String[]{getString(R.string.about),getString(R.string.reset_pw)};
        mListView.setAdapter(new ArrayAdapter(SettingsActivity.this,R.layout.simple_list,R.id.text,listItems));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(SettingsActivity.this,ResetPWActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
        btnExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.Logout(SettingsActivity.this);
                btnExitLogin.setTextColor(getResources().getColor(R.color.text_color));
                finish();
            }
        });
    }
}
