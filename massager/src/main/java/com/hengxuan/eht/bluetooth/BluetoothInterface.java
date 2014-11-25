package com.hengxuan.eht.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.eht.massager.BaseActivity;
import com.hengxuan.eht.massager.R;
import com.hengxuan.eht.massager.TreatmentActivity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打开搜索蓝牙设备页面
 */
public class BluetoothInterface extends BaseActivity {

	final int MSG_CONNECT_OK = 0;
	final int MSG_CONNECT_ERR = 1;

	private static final String TAG = "";
	private BluetoothAdapter mAdapter = null;
	private Map<String, String> blueToothInfo = null;
	private Map<String, Boolean> BlueToothDeviceConnectState;
	private Map<String, Boolean> BlueToothDeviceBoundState;
	private List<Map<String, String>> NewDevices;
	public BroadcastReceiver mReceiver;
	int CurrentFindDevicesCount;
	private ListView bluetoothdeviceInfoList;
	private SimpleAdapter myArrayAdapter;
	private final int REQUEST_ENABLE_BT = 2;

	private int getPosition;
	private String ishome = "";
	private boolean flag = false;

	private boolean isClick = false;

	Handler handler;
    //连接成功返回
    public static final int RESULT_OK = 1;
    //没连接成功返回
    public static final int RESULT_NULL = 0;

	private Boolean checkexist(List<Map<String, String>> NewDevices, Map<String, String> blueToothInfo) {
		for (int i = 0; i < NewDevices.size(); i++) {
			if (NewDevices.get(i).get("mac").equals(blueToothInfo.get("mac"))) {
				return true;
			}
		}

		return false;
	}

	private void openBluetoothDevice() {
		if (mAdapter == null)
			mAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mAdapter == null)
			return;
		if (!mAdapter.isEnabled()) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intent, REQUEST_ENABLE_BT);
		} else {
			initBluetoothDevice();
		}
	}

	private void initBluetoothDevice() {
		if (mAdapter.isDiscovering())
			mAdapter.cancelDiscovery();
		while (true) {
			if (mAdapter.isEnabled())
				break;
		}

		Object[] pairedDevices = mAdapter.getBondedDevices().toArray();
		BluetoothDevice tempdevice;
		for (int i = 0; i < pairedDevices.length; i++) {
			tempdevice = (BluetoothDevice) pairedDevices[i];
			if (tempdevice.getAddress() != null && !tempdevice.getAddress().equals("")) {
				blueToothInfo = new HashMap<String, String>();
				blueToothInfo.put("name", tempdevice.getName());
				blueToothInfo.put("mac", tempdevice.getAddress());
				if (!checkexist(NewDevices, blueToothInfo)) {

					NewDevices.add(blueToothInfo);
					BlueToothDeviceBoundState.put(tempdevice.getAddress(), true);
					if (blueToothInfo.get("mac").equals(BluetoothServiceProxy.mac))
						BlueToothDeviceConnectState.put(tempdevice.getAddress(), true);
					else
						BlueToothDeviceConnectState.put(tempdevice.getAddress(), false);
				}
			}
		}
		myArrayAdapter.notifyDataSetChanged();
		mAdapter.startDiscovery();

	}

    /**
     * list item view,通过广播获取蓝牙状态变化
     */
	private void setViewWithItem() {
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						blueToothInfo = new HashMap<String, String>();
						blueToothInfo.put("name", device.getName());
						blueToothInfo.put("mac", device.getAddress());
						if (!checkexist(NewDevices, blueToothInfo))
						{
							NewDevices.add(blueToothInfo);
							BlueToothDeviceBoundState.put(device.getAddress(), false);
							if (blueToothInfo.get("mac").equals(BluetoothServiceProxy.mac))
								BlueToothDeviceConnectState.put(device.getAddress(), true);
							else
								BlueToothDeviceConnectState.put(device.getAddress(), false);
						}
						Log.v(TAG, "find device:" + device.getName() + device.getAddress());
					}
				} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING:
						Toast.makeText(BluetoothInterface.this, getString(R.string.pairing), Toast.LENGTH_SHORT).show();
						break;
					case BluetoothDevice.BOND_BONDED:
						Toast.makeText(BluetoothInterface.this, getString(R.string.paired), Toast.LENGTH_SHORT).show();
						BlueToothDeviceBoundState.put(device.getAddress(), true);
						break;
					case BluetoothDevice.BOND_NONE:
						Toast.makeText(BluetoothInterface.this, getString(R.string.cancelpair), Toast.LENGTH_SHORT).show();
					default:
						break;
					}
				} else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
					BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String strPsw = "1234";
					try {
						setPin(btDevice.getClass(), btDevice, strPsw);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { // �������
					setActionBarTitle(R.string.search_is_completed);
				}
				myArrayAdapter.notifyDataSetChanged();
			}
		};
	}

	private void setbuletoothdevicelist() {
		// this.runOnUiThread(new Runnable() {

		// @Override
		// public void run() {
		// TODO Auto-generated method stub
		String as[] = { "name", "mac" };
		int ai[] = { R.id.bluetooth_item_name2, R.id.bluetooth_item_mac2 };
		myArrayAdapter = new SimpleAdapter(BluetoothInterface.this, NewDevices, R.layout.bluetoothitem, as, ai) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				String name = NewDevices.get(position).get("name");
				String mac = NewDevices.get(position).get("mac");
				TextView textview = (TextView) view.findViewById(R.id.bluetooth_item_name2);
				TextView textview1 = (TextView) view.findViewById(R.id.bluetooth_item_mac2);
				if (name != null)
					textview.setText(name);
				if (mac != null)
					textview1.setText(mac);
				Button button = (Button) view.findViewById(R.id.connection_item);
				final String constmac = mac;
				// constmac = mac;
				if (BlueToothDeviceBoundState.get(mac)) {
					if (BlueToothDeviceConnectState.get(mac)) {
						button.setText(R.string.disconnect);
						button.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								// �Ͽ�����
								// Toast.makeText(BluetoothInterface.this,
								// getResources().getString(R.string.tryto_connect),
								// Toast.LENGTH_SHORT).show();
								if (BluetoothServiceProxy.btSocket != null) {
									if (BluetoothServiceProxy.outStream != null) {
										try {
											BluetoothServiceProxy.outStream.flush();
										} catch (IOException e) {
											Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
										}

									}

									try {
										BluetoothServiceProxy.btSocket.close();
										BluetoothServiceProxy.btSocket = null;
										BlueToothDeviceConnectState.put(BluetoothServiceProxy.mac, false);
										BluetoothServiceProxy.name = null;
										BluetoothServiceProxy.mac = null;
										notifyDataSetChanged();
										Toast.makeText(BluetoothInterface.this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
									} catch (IOException e2) {
										Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
									}
								} else {
									BlueToothDeviceConnectState.put(BluetoothServiceProxy.mac, false);
									notifyDataSetChanged();
								}
							}

						});
					} else {
						button.setText(R.string.connect);
						button.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								isClick = true;
								mAdapter.cancelDiscovery();
								final BluetoothDevice currentdevice = mAdapter.getRemoteDevice(constmac);
								try {
									if (BluetoothServiceProxy.btSocket != null) {
										if (BluetoothServiceProxy.outStream != null) {
											try {
												BluetoothServiceProxy.outStream.flush();
											} catch (IOException e) {
												Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
											}
										}
										try {
											BluetoothServiceProxy.btSocket.close();
											BluetoothServiceProxy.btSocket = null;
											BlueToothDeviceConnectState.put(BluetoothServiceProxy.mac, false);
											BluetoothServiceProxy.mac = null;
											BluetoothServiceProxy.name = null;
										} catch (IOException e2) {
											Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
										}
									}

									Method m = currentdevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
									BluetoothServiceProxy.btSocket = (BluetoothSocket) m.invoke(currentdevice, 1);

								} catch (SecurityException e) {
									e.printStackTrace();
									Toast.makeText(BluetoothInterface.this, getString(R.string.not_open_device), Toast.LENGTH_SHORT).show();
								} catch (NoSuchMethodException e) {
									e.printStackTrace();
									Toast.makeText(BluetoothInterface.this, getString(R.string.not_open_device), Toast.LENGTH_SHORT).show();
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									Toast.makeText(BluetoothInterface.this, getString(R.string.not_open_device), Toast.LENGTH_SHORT).show();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									Toast.makeText(BluetoothInterface.this, getString(R.string.not_open_device), Toast.LENGTH_SHORT).show();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
									Toast.makeText(BluetoothInterface.this, getString(R.string.not_open_device), Toast.LENGTH_SHORT).show();
								}

								new Thread(new Runnable() {
									@Override
									public void run() {
										try {
											BluetoothServiceProxy.btSocket.connect();

											BlueToothDeviceConnectState.put(currentdevice.getAddress(), true);
											BluetoothServiceProxy.name = currentdevice.getName();
											BluetoothServiceProxy.mac = currentdevice.getAddress();
											// notifyDataSetChanged();
											// if(BluetoothServiceProxy.isconnect())
											handler.sendEmptyMessage(MSG_CONNECT_OK);

											Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
                                            BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.STRENGTH_TAG);
											// getData();
											// Intent intent = new
											// Intent(BluetoothInterface.this,
											// HomeActivity2.class);
											/*Intent intent = null;
											if(ishome.equals("ishome"))
												intent = new Intent(BluetoothInterface.this, HomeActivity2.class);
											else if(ishome.equals("iselectro"))
												intent = new Intent(BluetoothInterface.this, Electrocautery2Activity.class);
											else if(ishome.equals("isgame"))
												intent = new Intent(BluetoothInterface.this, GameList2.class);
											Bundle bundle = new Bundle();
											bundle.putInt("getPosition", getPosition);
											intent.putExtras(bundle);
											// startActivity(intent);
											setResult(Activity.RESULT_OK, intent);*/
                                            setResult(RESULT_OK);
											BluetoothInterface.this.finish();

//											int version = Integer.valueOf(android.os.Build.VERSION.SDK);
//											if (version >= 5)
//												overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);

										} catch (IOException e) {

											try {
                                                if(BluetoothServiceProxy.btSocket != null) {
                                                    BluetoothServiceProxy.btSocket.close();
                                                    BluetoothServiceProxy.btSocket = null;
                                                }
												BlueToothDeviceConnectState.put(currentdevice.getAddress(), false);
												handler.sendEmptyMessage(MSG_CONNECT_ERR);

												flag = true;
												/*getData();
												Intent intent = new Intent(BluetoothInterface.this, HomeActivity.class);
												Bundle bundle = new Bundle();
												bundle.putInt("getPosition", getPosition);
												intent.putExtras(bundle);
												// startActivity(intent);
												setResult(Activity.RESULT_OK, intent);
												BluetoothInterface.this.finish();
												
												int version = Integer.valueOf(android.os.Build.VERSION.SDK);
												if(version >= 5)
													overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);*/
											} catch (IOException e2) {
												Log.e(TAG, "ON RESUME: Unable to close socket during connection failure", e2);
											}

										}

									}
								}).start();
							}
						});
					}
				} else {
					button.setText(R.string.pair);
					button.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// Toast.makeText(BluetoothInterface.this,
							// getResources().getString(R.string.tryto_connect),
							// 0).show();
							mAdapter.cancelDiscovery();
							BluetoothDevice currentdevice = mAdapter.getRemoteDevice(constmac);
							try {
								if (currentdevice.getBondState() == BluetoothDevice.BOND_NONE) {
									Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
									Log.d("BlueToothTestActivity", "��ʼ���");
									Toast.makeText(BluetoothInterface.this, getString(R.string.startpair), Toast.LENGTH_SHORT).show();
									createBondMethod.invoke(currentdevice);
								}
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(BluetoothInterface.this, getString(R.string.not_support_device), Toast.LENGTH_SHORT).show();
							}
						}

					});
				}
				return view;
			}

		};

		bluetoothdeviceInfoList.setAdapter(myArrayAdapter);
		// }

		// });

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        bluetoothdeviceInfoList = new ListView(this);
		setContentView(bluetoothdeviceInfoList);
		setActionBarTitle(R.string.bluetooth_devices);

		NewDevices = new ArrayList<Map<String, String>>();
		BlueToothDeviceConnectState = new HashMap<String, Boolean>();
		BlueToothDeviceBoundState = new HashMap<String, Boolean>();
		setViewWithItem();
		setbuletoothdevicelist();
		openBluetoothDevice();
		// initBluetoothDevice();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");//
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);

		handler = new Handler() {
			public void handleMessage(Message message) {
				switch (message.what) {
				case MSG_CONNECT_OK:
					/*Toast.makeText(
							BluetoothInterface.this,
							getString(R.string.connected),
							0).show();*/
					break;
				case MSG_CONNECT_ERR:
					Toast.makeText(BluetoothInterface.this, getString(R.string.not_open_device), Toast.LENGTH_SHORT).show();
					break;
				}

			}
		};
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		/*
		 * if(BluetoothServiceProxy.btSocket != null) { if (BluetoothServiceProxy.outStream !=
		 * null) { try { BluetoothServiceProxy.outStream.flush();
		 * BluetoothServiceProxy.outStream = null; } catch (IOException e) { Log.e(TAG,
		 * "ON PAUSE: Couldn't flush output stream.", e); }
		 * 
		 * }
		 * 
		 * 
		 * try { BluetoothServiceProxy.btSocket.close();
		 * BlueToothDeviceState.put(BluetoothServiceProxy.mac, false);
		 * BluetoothServiceProxy.btSocket = null; } catch (IOException e2) { Log.e(TAG,
		 * "ON PAUSE: Unable to close socket.", e2); } }
		 */
		super.onDestroy();

	}

	public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str) throws Exception {

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

	@Override
	protected void onActivityResult(int i, int j, Intent intent) {
		if (i == REQUEST_ENABLE_BT) {
			if (j == Activity.RESULT_OK) {
				BluetoothServiceProxy.open_flag = true;
				initBluetoothDevice();
			} else {
				finish();
			}
		}
	}

	private void getData() {
		Intent getIntent = getIntent();
		getPosition = getIntent.getIntExtra("position", -1);
		ishome = getIntent.getStringExtra("ishome");
	}

	private void toIntent() {
		getData();
		Intent intent = null;
		if (ishome.equals("ishome"))
			intent = new Intent(BluetoothInterface.this, TreatmentActivity.class);
		    intent.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX, 2);
		    setResult(Activity.RESULT_OK, intent);
		    BluetoothInterface.this.finish();
//		    int version = Integer.valueOf(android.os.Build.VERSION.SDK);
//		    if (version >= 5)
//			    overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);
	}

    @Override
    public void onBackPressed() {
        setResult(RESULT_NULL);
        super.onBackPressed();
    }

}
