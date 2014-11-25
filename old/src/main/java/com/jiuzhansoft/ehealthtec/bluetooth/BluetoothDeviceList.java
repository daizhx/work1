package com.jiuzhansoft.ehealthtec.bluetooth;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;

public class BluetoothDeviceList extends BaseActivity {
	
	private ListView bluetoothdeviceInfoList;
	private List<Map<String, String>> mDevices;
	private SimpleAdapter myArrayAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		bluetoothdeviceInfoList = new ListView(this);
		setContentView(bluetoothdeviceInfoList);
		setTitle(R.string.massager);
		String as[] = { "name", "mac" };
		int ai[] = { R.id.bluetooth_item_name2, R.id.bluetooth_item_mac2 };
		myArrayAdapter = new SimpleAdapter(this, mDevices, R.layout.bluetoothitem, as, ai){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				return super.getView(position, convertView, parent);
			}
		};
	}
}
