package com.hengxuan.eht.bluetooth;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BTConnectService extends Service {
    private static final String TAG = "BTConnectService";
    private static final String DEVICE_NAME_DEFAULT = "EHT";
    //devices which can connect to
    private ArrayList<String> devicesNameList;
    //devices found
    private List<BluetoothDevice> mBTDevices = new ArrayList<BluetoothDevice>();
    //devices which connect to
    private List<BluetoothDevice> mTargetDevices = new ArrayList<BluetoothDevice>();
    public static final String NAME_ARRAY = "names";
    private BluetoothAdapter mBluetoothAdapter;

    private static final int CONNECT_SUCCESS = 1;
    private static final int CONNECT_FAIL = 0;
    private ImageView btIndicator;

    //handle the connectThread`s msg
    private Handler connectHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code){
                case CONNECT_SUCCESS:
//                    btIndicatorOn();
                    Toast.makeText(BTConnectService.this, getString(R.string.bt_connect_success), Toast.LENGTH_SHORT);
                    break;
                case CONNECT_FAIL:
//                    btIndicatorOff();
                    Toast.makeText(BTConnectService.this, getString(R.string.bt_connect_fail),Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }
    };
    public BTConnectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerBTBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        devicesNameList = intent.getStringArrayListExtra(NAME_ARRAY);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void unregisterBIBroadcastReceiver(){
        unregisterReceiver(mBTBroadcastReceiver);
    }

    private BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.d(TAG, "BT action=" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Log.d(TAG,"find device="+device.getName());
                mBTDevices.add(device);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED
                    .equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(
                                BTConnectService.this,
                                getString(R.string.pairing),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Toast.makeText(
                                BTConnectService.this,
                                getString(R.string.paired),
                                Toast.LENGTH_SHORT).show();
                        String name = device.getName();
                        if(devicesNameList != null){
                            if(devicesNameList.contains(name)){
                                connectBluetooth(device);
                            }
                        }else{
                            if(name.equals(DEVICE_NAME_DEFAULT)){
                                connectBluetooth(device);
                            }
                        }

                        break;
                    case BluetoothDevice.BOND_NONE:
                        Toast.makeText(
                                BTConnectService.this,
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

                if(mBTDevices.isEmpty()){
                    btIndicatorOff();
                    Toast.makeText(BTConnectService.this, getString(R.string.no_device_found), Toast.LENGTH_SHORT).show();
                }else{
                    for(BluetoothDevice device : mBTDevices){
                        String name = device.getName();
                        if(devicesNameList != null){
                            if(devicesNameList.contains(name)){
                                mTargetDevices.add(device);
                            }
                        }else{
                            if(name.equals(DEVICE_NAME_DEFAULT)){
                                mTargetDevices.add(device);
                            }
                        }
                    }
                    if(mTargetDevices.isEmpty()){
                        btIndicatorOff();
                        Toast.makeText(BTConnectService.this,getString(R.string.not_found_device),Toast.LENGTH_LONG).show();
                    }else if(mTargetDevices.size() == 1){
                        //found one device,connect it
                        BluetoothDevice device = mTargetDevices.get(0);
                        if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                            try {
                                Method createBondMethod = BluetoothDevice.class
                                        .getMethod("createBond");
                                Toast.makeText(
                                        BTConnectService.this,
                                        getString(R.string.startpair),
                                        Toast.LENGTH_SHORT).show();
                                createBondMethod.invoke(device);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }else{
                            //if paired
                            connectBluetooth(device);
                        }
                    }else{
                        //one more target device opened
                        manualConnect();
                    }

                }
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if(state == BluetoothAdapter.STATE_ON){
                    btIndicatorTwinkle();
                    scanBTDevices();
                }else if(state == BluetoothAdapter.STATE_DISCONNECTING){
                    btIndicatorTwinkle();
                }else if(state == BluetoothAdapter.STATE_DISCONNECTED){
                    btIndicatorOff();
                }

            }

        }
    };

    private void scanBTDevices() {
        if(!mBluetoothAdapter.isDiscovering()) {
            btIndicatorTwinkle();
            mBluetoothAdapter.startDiscovery();
        }
    }

    public void btIndicatorOn(){
        if(btIndicator != null) {
        }
    }

    public void btIndicatorOff(){
        if(btIndicator != null) {
        }
    }

    public void btIndicatorTwinkle(){
        if(btIndicator != null) {

        }
    }

    private void manualConnect() {
        //TODO
    }

    public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str)
            throws Exception {

        try {

            Method removeBondMethod = btClass.getDeclaredMethod("setPin",

                    new Class[] { byte[].class });

            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,

                    new Object[] { str.getBytes() });

        } catch (SecurityException e) {

            e.printStackTrace();

        } catch (IllegalArgumentException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return true;

    }

    /**
     * open bt socket and connect in a thread,
     * if failed,call manualConnect()
     * @param device
     */
    private void connectBluetooth(BluetoothDevice device){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            if (BluetoothServiceProxy.btSocket != null) {
                if (BluetoothServiceProxy.outStream != null) {
                    try {
                        BluetoothServiceProxy.outStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    BluetoothServiceProxy.btSocket.close();
                    BluetoothServiceProxy.btSocket = null;
                    BluetoothServiceProxy.mac = null;
                    BluetoothServiceProxy.name = null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            BluetoothServiceProxy.btSocket = (BluetoothSocket) m.invoke(device, 1);

            ConnectThread connectThread = new ConnectThread(device,BluetoothServiceProxy.btSocket);
            connectThread.start();
            return;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //TODO
    }

    private class ConnectThread extends Thread{
        BluetoothDevice currentdevice;
        BluetoothSocket btSocket;
        public ConnectThread(BluetoothDevice device,BluetoothSocket s) {
            currentdevice = device;
            btSocket = s;
        }
        @Override
        public void run() {
            try {
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }
                btSocket.connect();
                BluetoothServiceProxy.name = currentdevice.getName();
                BluetoothServiceProxy.mac = currentdevice.getAddress();
                //set the rightIcon image
                Message msg = Message.obtain();
                msg.what = 1;
                connectHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    BluetoothServiceProxy.btSocket.close();
                    BluetoothServiceProxy.btSocket = null;

                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = 0;
                connectHandler.sendMessage(msg);
            }
        }
    }
}
