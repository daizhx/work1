package com.hengxuan.eht.massager2;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hengxuan.eht.bluetooth.BTBaseActivity;
import com.hengxuan.eht.bluetooth.BTIndicator;


public class MyAciontBarActivity extends BTBaseActivity implements BTIndicator {
    protected ImageView actionBarLeftIcon;
    protected TextView actionBarTitle;
    protected  ImageView actionBarRightIcon;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        View v = getLayoutInflater().inflate(R.layout.action_bar,null);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ab.setCustomView(v, lp);
        actionBarTitle = (TextView) v.findViewById(R.id.title);
        actionBarLeftIcon = (ImageView) v.findViewById(R.id.left_icon);
        //默认为返回箭头
        actionBarLeftIcon.setImageResource(R.drawable.ic_person);
        actionBarLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
            }
        });
        actionBarRightIcon = (ImageView) v.findViewById(R.id.right_icon);
        actionBarRightIcon.setImageResource(R.drawable.bt_off);
//        setContentView(R.layout.activity_base);
        setIndicator(this);
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
    public void on() {
        actionBarRightIcon.setImageResource(R.drawable.bt_on);
        actionBarRightIcon.setEnabled(false);
    }

    @Override
    public void off() {
        actionBarRightIcon.setImageResource(R.drawable.bt_off);
        actionBarRightIcon.setEnabled(true);
    }

    @Override
    public void twinkle() {
        actionBarRightIcon.setImageResource(R.drawable.bt_connectting_indicate);
        AnimationDrawable aim = (AnimationDrawable) actionBarRightIcon.getDrawable();
        aim.start();
        actionBarRightIcon.setEnabled(false);
    }
}
