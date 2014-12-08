package com.jiuzhansoft.ehealthtec.massager;

import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.jiuzhansoft.ehealthtec.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class TimeSettingFragment extends Fragment implements View.OnClickListener, CircleSettingView.OnValueChangeListener{

	private FragmentChangeListener mFragmentChangeListener;
	private View mViewGroup;
	private CircleSettingView circleSettingView;
	private int currentTime = 2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mViewGroup = inflater.inflate(R.layout.fragment_time_setting, null, false);
		Button btn1 = (Button) mViewGroup.findViewById(R.id.setting_strength);
		btn1.setOnClickListener(this);
		Button btn2 = (Button) mViewGroup.findViewById(R.id.setting_mode);
		btn2.setOnClickListener(this);
		circleSettingView = (CircleSettingView)mViewGroup.findViewById(R.id.circle_setting);
		circleSettingView.setCurrentValue(currentTime);
		circleSettingView.setOnValueChangeListener(this);
		return mViewGroup;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try{
			mFragmentChangeListener = (FragmentChangeListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implement FragmentChangeListener");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.setting_strength:
			mFragmentChangeListener.onChangeStrengthSetting();
			break;
		case R.id.setting_mode:
			mFragmentChangeListener.onChangeModeSetting();
			break;
		default:
			break;
		}
	}

	@Override
	public void onValueChanged(int value) {
		// TODO Auto-generated method stub
		final int time = value;
		currentTime = value;
		if(BluetoothServiceProxy.isconnect()){
			Handler intenthandler = new Handler();
			intenthandler.postDelayed(new Runnable() {
				@Override
				public void run() {
                    if(!BluetoothServiceProxy.sendCommandToDevice((short)(BluetoothServiceProxy.TIME_TAG +time))){
                        ((BTBaseActivity)getActivity()).btIndicatorOff();
                        Toast.makeText(getActivity(), getString(R.string.disconnectbluetooth),Toast.LENGTH_SHORT).show();
                    }
				}
			}, 0L);
		}else{
			Toast.makeText(getActivity(), getString(R.string.disconnectedstate), Toast.LENGTH_SHORT).show();
		}
	}
	
	
}
