package com.hengxuan.eht.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
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

/**
 * Created by Administrator on 2014/11/27.
 */
public class BTBaseActivity extends ActionBarActivity{

    private static final String TAG = "BTBaseActivity";
    //devices found
    private List<BluetoothDevice> mBTDevices = new ArrayList<BluetoothDevice>();
    //devices which connect to
    private List<BluetoothDevice> mTargetDevices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;
//    private ImageView btIndicator;
    private BTIndicator mBTIndicator;
    private static final String TARGET_DEVICE_NAME1 = "Ehealthtec";
    private static final String TARGET_DEVICE_NAME2 = "EHT";

    public static final int CONNECT_SUCCESS = 1;
    public static final int CONNECT_FAIL = 0;
    //handle the connectThread`s msg
    private Handler connectHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code){
                case CONNECT_SUCCESS:
                    btIndicatorOn();
                    Toast.makeText(BTBaseActivity.this, getString(R.string.bt_connect_success),Toast.LENGTH_SHORT);
                    break;
                case CONNECT_FAIL:
                    btIndicatorOff();
                    Toast.makeText(BTBaseActivity.this, getString(R.string.bt_connect_fail),Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }
    };

    public interface ConnectResultListener{
        public void onConnectResult(int i);
    }
    public ConnectResultListener mConnectResultListener;
    public void setConnectResultListener(ConnectResultListener l){
        mConnectResultListener = l;
    }


    private BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED
                    .equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(
                                BTBaseActivity.this,
                                getString(R.string.pairing),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Toast.makeText(
                                BTBaseActivity.this,
                                getString(R.string.paired),
                                Toast.LENGTH_SHORT).show();
                        String name = device.getName();
                        if(name.equals(TARGET_DEVICE_NAME1) || name.equals(TARGET_DEVICE_NAME2)) {
                            connectBluetooth(device);
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Toast.makeText(
                                BTBaseActivity.this,
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
                    Toast.makeText(BTBaseActivity.this, getString(R.string.no_device_found), Toast.LENGTH_SHORT).show();
                }else{
                    for(BluetoothDevice device : mBTDevices){
                        String name = device.getName();
                        if(name.equals(TARGET_DEVICE_NAME1) || name.equals(TARGET_DEVICE_NAME2)){
                            mTargetDevices.add(device);
                        }
                    }
                    if(mTargetDevices.isEmpty()){
                        btIndicatorOff();
                        Toast.makeText(BTBaseActivity.this,getString(R.string.not_found_device),Toast.LENGTH_LONG).show();
                    }else if(mTargetDevices.size() == 1){
                        //found one device,connect it
                        BluetoothDevice device = mTargetDevices.get(0);
                        if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                            try {
                                Method createBondMethod = BluetoothDevice.class
                                        .getMethod("createBond");
                                Toast.makeText(
                                        BTBaseActivity.this,
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
                    mBluetoothAdapter.startDiscovery();
                }else if(state == BluetoothAdapter.STATE_OFF){
                    btIndicatorOff();
                    BluetoothServiceProxy.disconnectBluetooth();
                }

            }

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        registerBTBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBIBroadcastReceiver();
    }

    public void openBT(){
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }
    protected void setIndicator(BTIndicator indicator){
        mBTIndicator = indicator;
    }


    protected void scanBTDevices() {
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
            return;
        }
        if(!mBluetoothAdapter.isDiscovering()) {
            btIndicatorTwinkle();
            mBluetoothAdapter.startDiscovery();
        }
    }

    public void btIndicatorOn(){
        if(mBTIndicator != null) {
            mBTIndicator.on();
        }
        if(mConnectResultListener != null) {
            mConnectResultListener.onConnectResult(1);
        }
    }

    public void btIndicatorOff(){
        if(mBTIndicator != null) {
            mBTIndicator.off();
        }
        if(mConnectResultListener != null) {
            mConnectResultListener.onConnectResult(0);
        }
    }

    public void btIndicatorTwinkle(){
        if(mBTIndicator != null) {
            mBTIndicator.twinkle();
        }
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
//        manualConnect();
        //TODO create socket fail.
    }

    private void manualConnect() {
        //TODO
        AlertDialog alertDialog = new AlertDialog.Builder(BTBaseActivity.this).create();
        alertDialog.setTitle(getString(R.string.select_device));
        ListView listView = new ListView(BTBaseActivity.this);
        alertDialog.setView(listView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mTargetDevices.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View v = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,null,false);
                TextView name = (TextView) v.findViewById(android.R.id.text1);
                TextView addr = (TextView) v.findViewById(android.R.id.text2);
                name.setText(mTargetDevices.get(i).getName());
                addr.setText(mTargetDevices.get(i).getAddress());
                return v;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = mTargetDevices.get(i);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    connectBluetooth(device);
                }else{
                    try {
                        Method createBondMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        Toast.makeText(
                                BTBaseActivity.this,
                                getString(R.string.startpair),
                                Toast.LENGTH_SHORT).show();
                        createBondMethod.invoke(device);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                btIndicatorOff();
            }
        });
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
