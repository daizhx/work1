package com.hengxuan.eht.massager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class BaseActivity extends ActionBarActivity {
    protected ImageView actionBarLeftIcon;
    protected TextView actionBarTitle;
    protected  ImageView actionBarRightIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
//        ab.setDisplayShowCustomEnabled(true);
        View v = getLayoutInflater().inflate(R.layout.action_bar,null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ab.setCustomView(v, lp);
        actionBarTitle = (TextView) v.findViewById(R.id.title);
        actionBarLeftIcon = (ImageView) v.findViewById(R.id.left_icon);
        //默认为返回箭头
        actionBarLeftIcon.setImageResource(R.drawable.ic_back);
        actionBarLeftIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();

            }
        });
        actionBarRightIcon = (ImageView) v.findViewById(R.id.right_icon);
//        setContentView(R.layout.activity_base);
    }

    protected void setRightIcon(int resId){
        actionBarRightIcon.setImageResource(resId);
    }
    protected void setRightIconClickListener(View.OnClickListener listener){
        findViewById(R.id.right_icon_container).setOnClickListener(listener);
    }
    protected void setRightIcon(int resId, View.OnClickListener listener){
        actionBarRightIcon.setImageResource(resId);
        findViewById(R.id.right_icon_container).setOnClickListener(listener);
    }
    protected void setActionBarTitle(String s){
        actionBarTitle.setText(s);
    }

    protected void setActionBarTitle(int resId){
        actionBarTitle.setText(resId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
