package com.jiuzhansoft.ehealthtec.massager;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.bluetooth.BluetoothInterface;
import com.jiuzhansoft.ehealthtec.bluetooth.BluetoothServiceProxy;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MassagerActivity extends BaseActivity implements FragmentChangeListener {

	private static final String TAG = "BlueTooth";
	protected TextView timetv;
	
	private FragmentManager mFrManager;
	private ModeSettingFragment mModeSettingFragment = null;
	private StrengthSettingFragment mStrengthSettingFragment = null;
	private TimeSettingFragment mTimeSettingFragment = null;
	
	private BluetoothAdapter mBluetoothAdapter;
//	private BroadcastReceiver mBTBroadcastReceiver;
	private static final String DEVICE_NAME2 = "Ehealthtec";
	private static final String DEVICE_NAME = "EHT";
	private int state;//0-idle,1-connecting,2-connencted;
	private static final int IDLE = 0;
	private static final int CONNECTING = 1;
	private static final int CONNECTED = 2;
	private static final int REQUEST_MANAL_CONNECT = 123; 

	private List<BluetoothDevice> targetDevices = new ArrayList<BluetoothDevice>();

	private Handler handlerIndicate = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				rightIcon.setImageResource(R.drawable.bt_off);
				rightIcon.setEnabled(true);
				Toast.makeText(MassagerActivity.this, getResources().getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
				state = IDLE;
				openManualConnectActivity();
				break;
			case 1:
				rightIcon.setImageResource(R.drawable.bt_on);
				rightIcon.setEnabled(false);
				Toast.makeText(MassagerActivity.this, getResources().getString(R.string.device_connected), Toast.LENGTH_LONG).show();
				state = CONNECTED;
				break;
			}
		}
	};
	
	private BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("BlueTooth","action="+action);
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d("BlueTooth","find device="+device.getName());
				if(device.getName().equals(DEVICE_NAME) || device.getName().equals(DEVICE_NAME2)){
					targetDevices.add(device);
				}
				
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED
					.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					Toast.makeText(
							MassagerActivity.this,
							getString(R.string.pairing),
							Toast.LENGTH_SHORT).show();
					break;
				case BluetoothDevice.BOND_BONDED:
					Toast.makeText(
							MassagerActivity.this,
							getString(R.string.paired),
							Toast.LENGTH_SHORT).show();
					connectBluetooth(device);
					break;
				case BluetoothDevice.BOND_NONE:
					Toast.makeText(
							MassagerActivity.this,
							getString(R.string.cancelpair),
							Toast.LENGTH_SHORT).show();
				default:
					break;
				}
			} else if (action
					.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
				BluetoothDevice btDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String strPsw = "1234";
				try {
					setPin(btDevice.getClass(), btDevice, strPsw);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				
				if(targetDevices.isEmpty()){
					btIndicatorOff();
					Toast.makeText(MassagerActivity.this, getString(R.string.no_device_found), Toast.LENGTH_SHORT).show();
				}else if(targetDevices.size() == 1){
					BluetoothDevice device = targetDevices.get(0);
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						try {
							Method createBondMethod = BluetoothDevice.class
									.getMethod("createBond");
							Toast.makeText(
									MassagerActivity.this,
									getString(R.string.startpair),
									Toast.LENGTH_SHORT).show();
							createBondMethod.invoke(device);

						} catch (Exception e) {
							e.printStackTrace();
						}
						return;	
					}
					//if paired
					connectBluetooth(device);
				}else{
					openManualConnectActivity();
				}
				
			}else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
				if(state == BluetoothAdapter.STATE_ON){
					btIndicatorTwinkle();
					findBluetooth(DEVICE_NAME);
				}

			}

		}
	};
	
	public void setBTDisconnect(){
		rightIcon.setImageResource(R.drawable.bt_off);
		rightIcon.setEnabled(true);
		state = IDLE;
	}
	
	private void registerBTBroadcastReceiver() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mBTBroadcastReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(mBTBroadcastReceiver, filter);
		filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
		registerReceiver(mBTBroadcastReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mBTBroadcastReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mBTBroadcastReceiver, filter);
	}
	
	public boolean isConnected(){
		if(state == CONNECTED){
			return true;
		}
		return false;
	}
	public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str)
			throws Exception {

		try {

			Method removeBondMethod = btClass.getDeclaredMethod("setPin",

			new Class[] { byte[].class });

			Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,

			new Object[] { str.getBytes() });

			Log.e("returnValue", "" + returnValue);

		} catch (SecurityException e) {

			e.printStackTrace();

		} catch (IllegalArgumentException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return true;

	}
	
	private void btIndicatorTwinkle(){
		rightIcon.setImageResource(R.drawable.bt_connectting_indicate);
		AnimationDrawable animationDrawable = (AnimationDrawable)rightIcon.getDrawable();
		animationDrawable.start();
		rightIcon.setEnabled(false);
	}
	
	private void btIndicatorOn(){
		rightIcon.setImageResource(R.drawable.bt_on);
		rightIcon.setEnabled(false);
	}
	
	private void btIndicatorOff(){
		rightIcon.setImageResource(R.drawable.bt_off);
		rightIcon.setEnabled(true);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		rightIcon.setImageResource(R.drawable.bt_connectting_indicate);
		rightIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(state == IDLE){
					state = CONNECTING;
					btIndicatorTwinkle();
					findBluetooth(DEVICE_NAME);
				}
			}
		});
		setContentView(R.layout.activity_massager);
		if(savedInstanceState != null){
			return;
		}
		if(mModeSettingFragment == null){
			mModeSettingFragment = new ModeSettingFragment();
		}
		Bundle args = new Bundle();
//		args.putInt("currentMode", 0);
		mModeSettingFragment.setArguments(args);
		mFrManager = getFragmentManager();
		mFrManager.beginTransaction().add(R.id.fragment_container, (Fragment)mModeSettingFragment).commit();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.enable();
		}else{
			btIndicatorTwinkle();
			findBluetooth(DEVICE_NAME);
		}
		registerBTBroadcastReceiver();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerBTBroadcastReceiver();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
		mBluetoothAdapter.disable();
		if(connectThread != null){
			
			try {
				connectThread.interrupt();
				connectThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		unregisterReceiver(mBTBroadcastReceiver);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_MANAL_CONNECT){
			if(resultCode == RESULT_OK){
				state = CONNECTED;
				btIndicatorOn();
			}else{
				state = IDLE;
				btIndicatorOff();
			}
		}
	}

	
	private void findBluetooth(String deviceName) {
		//query paired devices list
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if(device.getName().equals(DEVICE_NAME) || device.getName().equals(DEVICE_NAME2)){
					connectBluetooth(device);
					return;
				}
			}
		}
		
		//discovery new devices
		if(mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
		mBluetoothAdapter.startDiscovery();
	}
	
	private ConnectThread connectThread;
	private void connectBluetooth(BluetoothDevice device){
		if(mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.cancelDiscovery();
		}
		try {
			if (BluetoothServiceProxy.btSocket != null) {
				if (BluetoothServiceProxy.outStream != null) {
					try {
						BluetoothServiceProxy.outStream
						.flush();
					} catch (IOException e) {
						Log.e(TAG,
								"ON PAUSE: Couldn't flush output stream.",
								e);
					}
				}
				try {
					BluetoothServiceProxy.btSocket
					.close();
					BluetoothServiceProxy.btSocket = null;
					BluetoothServiceProxy.mac = null;
					BluetoothServiceProxy.name = null;
				} catch (IOException e2) {
					Log.e(TAG,"ON PAUSE: Unable to close socket.",e2);
				}
			}
			
			Method m = device
			.getClass()
			.getMethod(
					"createRfcommSocket",
					new Class[] { int.class });
			BluetoothServiceProxy.btSocket = (BluetoothSocket) m
			.invoke(device, 1);
			
			connectThread = new ConnectThread(device); 
			connectThread.start();
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
			Toast.makeText(
					MassagerActivity.this,
					getString(R.string.not_open_device),
					Toast.LENGTH_SHORT).show();
			btIndicatorOff();
		} catch (NoSuchMethodException e) {										
			e.printStackTrace();
			Toast.makeText(
					MassagerActivity.this,
					getString(R.string.not_open_device),
					Toast.LENGTH_SHORT).show();
			btIndicatorOff();
		} catch (IllegalArgumentException e) {											
			e.printStackTrace();
			Toast.makeText(
					MassagerActivity.this,
					getString(R.string.not_open_device),
					Toast.LENGTH_SHORT).show();
			btIndicatorOff();
		} catch (IllegalAccessException e) {											
			e.printStackTrace();
			Toast.makeText(
					MassagerActivity.this,
					getString(R.string.not_open_device),
					Toast.LENGTH_SHORT).show();
			btIndicatorOff();
		} catch (InvocationTargetException e) {											
			e.printStackTrace();
			Toast.makeText(
					MassagerActivity.this,
					getString(R.string.not_open_device),
					Toast.LENGTH_SHORT).show();
			btIndicatorOff();
		}
		openManualConnectActivity();
	}
	
	private void openManualConnectActivity(){
		startActivityForResult(new Intent(this, BluetoothInterface.class), REQUEST_MANAL_CONNECT);
		unregisterReceiver(mBTBroadcastReceiver);
	}
	
	class ConnectThread extends Thread{
		BluetoothDevice currentdevice;
		public ConnectThread(BluetoothDevice device) {
			// TODO Auto-generated constructor stub
			currentdevice = device;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				Log.d("daizhx", "BluetoothServiceProxy.btSocket="+BluetoothServiceProxy.btSocket);
				BluetoothServiceProxy.btSocket.connect();
				BluetoothServiceProxy.name = currentdevice
				.getName();
				BluetoothServiceProxy.mac = currentdevice
				.getAddress();
				//set the rightIcon image
				Message msg = Message.obtain();
				msg.what = 1;
				handlerIndicate.sendMessage(msg);
				Log.e(TAG,
						"ON RESUME: BT connection established, data transfer link open.");
				
			} catch (IOException e) {
				
				try {
					Log.d("daizhx", "BluetoothServiceProxy.btSocket="+BluetoothServiceProxy.btSocket);
					BluetoothServiceProxy.btSocket.close();
					BluetoothServiceProxy.btSocket = null;
				
				} catch (IOException e2) {
					Log.e(TAG,
							"ON RESUME: Unable to close socket during connection failure",
							e2);
				}
				//set the rightIcon image
				Message msg = Message.obtain();
				msg.what = 0;
				handlerIndicate.sendMessage(msg);
			}

		
		}
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
