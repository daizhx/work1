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
import android.widget.ImageView;
import android.widget.Toast;

public class StrengthSettingFragment extends Fragment implements View.OnClickListener,CircleSettingView.OnValueChangeListener {

	private FragmentChangeListener mFragmentChangeListener;
	private View mViewGroup;
	private CircleSettingView circleSettingView;
	private ImageView frequencyButton;
	private int currentStrength;
	private int currentFrequency;//low-0,mid-1

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mViewGroup = inflater.inflate(R.layout.fragment_strength_setting, null, false);
		Button btn1 = (Button) mViewGroup.findViewById(R.id.setting_time);
		btn1.setOnClickListener(this);
		Button btn2 = (Button) mViewGroup.findViewById(R.id.setting_mode);
		btn2.setOnClickListener(this);
		circleSettingView = (CircleSettingView)mViewGroup.findViewById(R.id.circle_setting);
		circleSettingView.setCurrentValue(currentStrength);
		circleSettingView.setOnValueChangeListener(this);
		frequencyButton = (ImageView)mViewGroup.findViewById(R.id.frequency_button);
		frequencyButton.setOnClickListener(this);
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
		case R.id.setting_time:
			mFragmentChangeListener.onChangeTimeSetting();
			break;
		case R.id.setting_mode:
			mFragmentChangeListener.onChangeModeSetting();
			break;
		case R.id.frequency_button:
			if(currentFrequency == 0){
				currentFrequency = 1;
				frequencyButton.setImageResource(R.drawable.frequence_high);
			}else{
				currentFrequency = 0;
				frequencyButton.setImageResource(R.drawable.frequence_low);
			}
			setFrequency(currentFrequency);
		default:
			break;
		}
	}
	
	private void setFrequency(int mode){
		Handler handler = new Handler();
		if(mode == 1){
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
                    if(!BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.L_FR_TAG)){
                        ((BTBaseActivity)getActivity()).btIndicatorOff();
                        Toast.makeText(getActivity(), getString(R.string.disconnectbluetooth),Toast.LENGTH_SHORT).show();
                    }
				}
				
			}, 0L);
		}else{
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
                    if(!BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.H_FR_TAG)){
                        ((BTBaseActivity)getActivity()).btIndicatorOff();
                        Toast.makeText(getActivity(), getString(R.string.disconnectbluetooth),Toast.LENGTH_SHORT).show();
                    }
				}
				
			}, 0L);
		}
	}

	@Override
	public void onValueChanged(int value) {
		// TODO Auto-generated method stub
		final int strength = value;
		currentStrength = value;
		if(BluetoothServiceProxy.isconnect()){
			Handler handler = new Handler();
			handler.postAtTime(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
                    if(!BluetoothServiceProxy.sendCommandToDevice((short)(BluetoothServiceProxy.STRENGTH_TAG+strength))){
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
