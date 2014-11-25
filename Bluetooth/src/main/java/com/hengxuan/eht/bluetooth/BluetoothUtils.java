package com.hengxuan.eht.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class BluetoothUtils {
	
	private static BluetoothAdapter mAdapter;
	private static final int REQUEST_ENABLE_BT = 2;
	

	public static void open() {
		if (mAdapter == null)
			mAdapter = BluetoothAdapter.getDefaultAdapter();// ��ȡ���õ�����������
		if (mAdapter == null)
			return;
		if (!mAdapter.isEnabled()) { // �ж������Ƿ񱻴�
			mAdapter.enable();
		}
	}
	
	public static void openWithIntent(Activity context){
		if (mAdapter == null)
			mAdapter = BluetoothAdapter.getDefaultAdapter();// ��ȡ���õ�����������
		if (mAdapter == null)
			return;
		if (!mAdapter.isEnabled()) { // �ж������Ƿ񱻴�
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			context.startActivityForResult(intent, REQUEST_ENABLE_BT);
		}		
	}
	
	public static void connectTargetDevice(String deviceName){
		
	}

}
