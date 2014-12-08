package com.jiuzhansoft.ehealthtec.massager;

import android.os.Bundle;

import com.jiuzhansoft.ehealthtec.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;


/**
 * Created by Administrator on 2014/11/27.
 */
public class MassagerActivity extends BTBaseActivity implements FragmentChangeListener{

    private static final String TAG = "MassagerActivity";
    private FragmentManager mFrManager;
    private ModeSettingFragment mModeSettingFragment = null;
    private StrengthSettingFragment mStrengthSettingFragment = null;
    private TimeSettingFragment mTimeSettingFragment = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.massager);

        setContentView(R.layout.activity_massager);
        if(savedInstanceState != null){
            return;
        }
        if(mModeSettingFragment == null){
            mModeSettingFragment = new ModeSettingFragment();
        }
        Bundle args = new Bundle();

        mModeSettingFragment.setArguments(args);
        mFrManager = getFragmentManager();
        mFrManager.beginTransaction().add(R.id.fragment_container, (Fragment)mModeSettingFragment).commit();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }



    @Override
    public void onChangeTimeSetting() {
        // TODO Auto-generated method stub
        if(mTimeSettingFragment == null){
            mTimeSettingFragment = new TimeSettingFragment();
        }
        FragmentTransaction transaction =  mFrManager.beginTransaction();
        transaction.replace(R.id.fragment_container, (Fragment)mTimeSettingFragment);
        transaction.commit();
    }


    @Override
    public void onChangeStrengthSetting() {
        // TODO Auto-generated method stub
        if(mStrengthSettingFragment == null){
            mStrengthSettingFragment = new StrengthSettingFragment();
        }
        FragmentTransaction transaction =  mFrManager.beginTransaction();
        transaction.replace(R.id.fragment_container, (Fragment)mStrengthSettingFragment);
        transaction.commit();
    }


    @Override
    public void onChangeModeSetting() {
        // TODO Auto-generated method stub
        if(mModeSettingFragment == null){
            mModeSettingFragment = new ModeSettingFragment();
        }
        FragmentTransaction transaction =  mFrManager.beginTransaction();
        transaction.replace(R.id.fragment_container, (Fragment)mModeSettingFragment);
        transaction.commit();
    }

}
