package com.hengxuan.eht.lens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hengxuan.eht.lens.Utils.FileUtils;
import com.hengxuan.eht.lens.Utils.LenUtils;
import com.hengxuan.eht.lens.View.LensMonitorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LensMainActivity extends Activity{


    private static final String TAG = "Lens";
    private WifiManager mWifiManager;
    private WifiInfo currentWifiInfo;
    //图片保存的路径
    public String savedPath;

    private int currentNetWorkId = -1;
    protected LensMonitorView lensMonitorView;
    //拍照按钮
    private ImageView ivShoot;
    //退出按钮
    private ImageButton cancelBtn;
    //图库按钮
    private ImageButton galleryBtn;
    private static final String SAVE_DIR = "dxlphoto";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //扫描热点结束
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                checkScanResult(mWifiManager.getScanResults());
            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED) {
                    // scan lens ap and connect
                    mWifiManager.startScan();
                }
            }

        }
    };

    /**
     * 处理扫描结果，存在3中情况
     * 1，没有镜头热点
     * 2，一个镜头
     * 3，多个镜头打开
     * @param apList
     */
    private void checkScanResult(List<ScanResult> apList){
        List<ScanResult> lensList = new ArrayList<ScanResult>();
        for(ScanResult scanResult : apList){
            if(scanResult.SSID.equals("EHT")){
                lensList.add(scanResult);
            }
        }
        if(lensList.size() == 0){
            //TODO 没有扫描到镜头
            new AlertDialog.Builder(this).setTitle(R.string.lens_not_open).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        }else if(lensList.size() == 1){
            if(connect2Lens(lensList.get(0))){
                onConnectSuccess();
            }
        }else{
            //TODO 扫描到多个镜头
            int size = lensList.size();
            CharSequence[] entries = new CharSequence[size];
            for(int i=0;i<size;i++){
                entries[i] = lensList.get(i).SSID + "\n" + lensList.get(i).BSSID;
            }
            new AlertDialog.Builder(this).setTitle(R.string.multi_lens).setItems(entries, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        }
    }

    private boolean connect2Lens(ScanResult scanResult){
        List<WifiConfiguration> list = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : list) {
            Log.d(TAG, "ssid:" + wifiConfiguration.SSID);
            if (wifiConfiguration.SSID != null
                    && wifiConfiguration.SSID.equals("\"" + "EHT"
                    + "\"")) {
                currentWifiInfo = mWifiManager.getConnectionInfo();
                setCurrentNetWorkId(currentWifiInfo
                        .getNetworkId());
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(wifiConfiguration.networkId,
                        true);
                mWifiManager.reconnect();
                return true;
            }
        }

        // add conf
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + "EHT" + "\"";
        conf.preSharedKey = "\"" + "12345678" + "\"";
        mWifiManager.addNetwork(conf);
        for (WifiConfiguration wifiConfiguration : list) {
            if (wifiConfiguration.SSID != null
                    && wifiConfiguration.SSID.equals("\"" + "EHT"
                    + "\"")) {
                currentWifiInfo = mWifiManager.getConnectionInfo();
                setCurrentNetWorkId(currentWifiInfo
                        .getNetworkId());
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(wifiConfiguration.networkId,
                        true);
                mWifiManager.reconnect();
                return true;
            }
        }
        // if found EHT AP,mostly return true,should not return
        // false,there must be something wrong
        return false;
    }
    private boolean connect2Lens() {
        List<ScanResult> APList = mWifiManager.getScanResults();
        for (ScanResult scanResult : APList) {
            if (scanResult.SSID.equals("EHT")) {
                List<WifiConfiguration> list = mWifiManager
                        .getConfiguredNetworks();
                for (WifiConfiguration wifiConfiguration : list) {
                    Log.d(TAG, "ssid:" + wifiConfiguration.SSID);
                    if (wifiConfiguration.SSID != null
                            && wifiConfiguration.SSID.equals("\"" + "EHT"
                            + "\"")) {
                        currentWifiInfo = mWifiManager.getConnectionInfo();
                        setCurrentNetWorkId(currentWifiInfo
                                .getNetworkId());
                        mWifiManager.disconnect();
                        mWifiManager.enableNetwork(wifiConfiguration.networkId,
                                true);
                        mWifiManager.reconnect();
                        return true;
                    }
                }

                // add conf
                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + "EHT" + "\"";
                conf.preSharedKey = "\"" + "12345678" + "\"";
                mWifiManager.addNetwork(conf);
                for (WifiConfiguration wifiConfiguration : list) {
                    if (wifiConfiguration.SSID != null
                            && wifiConfiguration.SSID.equals("\"" + "EHT"
                            + "\"")) {
                        currentWifiInfo = mWifiManager.getConnectionInfo();
                        setCurrentNetWorkId(currentWifiInfo
                                .getNetworkId());
                        mWifiManager.disconnect();
                        mWifiManager.enableNetwork(wifiConfiguration.networkId,
                                true);
                        mWifiManager.reconnect();
                        return true;
                    }
                }
                // if found EHT AP,mostly return true,should not return
                // false,there must be something wrong
                return false;
            }
        }
        return false;
    }


    /**
     * 连上镜头，调用此方法
     */
    public void onConnectSuccess() {
        Log.d(TAG, "onConnectSuccess");
        unregisterReceiver(mBroadcastReceiver);
        /*
        switch (index) {
            case LensConstant.INDEX_IRIS:
                try {
                    DatagramSocket s = new DatagramSocket();
                    InetAddress local = InetAddress.getByName("10.10.10.254");
                    byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
                            0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
                            0x3D, 0x30, 0x30, 0x30, 0x30, (byte) 0xD2 };
                    DatagramPacket p = new DatagramPacket(message, message.length,
                            local, 8080);
                    s.send(p);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case LensConstant.INDEX_HAIR:
                try {
                    DatagramSocket s = new DatagramSocket();
                    InetAddress local = InetAddress.getByName("10.10.10.254");
                    byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
                            0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
                            0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
                    DatagramPacket p = new DatagramPacket(message, message.length,
                            local, 8080);
                    s.send(p);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case LensConstant.INDEX_SKIN:
                try {
                    DatagramSocket s = new DatagramSocket();
                    InetAddress local = InetAddress.getByName("10.10.10.254");
                    byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
                            0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
                            0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
                    DatagramPacket p = new DatagramPacket(message, message.length,
                            local, 8080);
                    s.send(p);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case LensConstant.INDEX_NAEVUS:
                try {
                    DatagramSocket s = new DatagramSocket();
                    InetAddress local = InetAddress.getByName("10.10.10.254");
                    byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
                            0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
                            0x3D, 0x30, 0x30, 0x30, 0x35, (byte) 0xCD };
                    DatagramPacket p = new DatagramPacket(message, message.length,
                            local, 8080);
                    s.send(p);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        */

//        TextView indicator = (TextView)findViewById(R.id.text_indicate_progress);
//        indicator.setText(R.string.get_image);
        lensMonitorView.start();
    }

    public void onConnectFail() {
        Log.d(TAG, "onConnectFail");
        unregisterReceiver(mBroadcastReceiver);
        // TODO Auto-generated method stub
        AlertDialog alertDialog = new AlertDialog.Builder(LensMainActivity.this).create();
        alertDialog.setMessage(getString(R.string.open_lens));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
//						restoreWifiInfo();
                        finish();
                    }
                });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub
                if(arg2.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    finish();
                    return true;
                }
                return false;
            }
        });
        alertDialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LensMonitorView", "lens main activity onCreate");
        if(!LenUtils.isConnectEHT(this)){
            Log.d("LensMonitorView", "xxxxxxxxxxxxxx");
            finish();
            return;
        }
        setContentView(R.layout.activity_lens_main);
        initView();

        if(LenUtils.isConnectEHT(this)){
//            TextView indicator = (TextView)findViewById(R.id.text_indicate_progress);
//            indicator.setText(R.string.get_image);
            try {
                DatagramSocket s = new DatagramSocket();
                InetAddress local = InetAddress.getByName("10.10.10.254");
                byte[] message = { (byte) 0xD3, 0x5A, 0x6F, 0x6F, 0x6D, 0x2C,
                        0x20, 0x41, 0x62, 0x73, 0x6F, 0x6C, 0x75, 0x74, 0x65,
                        0x3D, 0x30, 0x30, 0x30, 0x30, (byte) 0xD2 };
                DatagramPacket p = new DatagramPacket(message, message.length,
                        local, 8080);
                s.send(p);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            lensMonitorView.setCmPara(lensMonitorView.initParam());
            lensMonitorView.start();
        }else{
            if(mWifiManager.isWifiEnabled()){
                registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mWifiManager.startScan();
                //镜头未连接
//                TextView indicator = (TextView)findViewById(R.id.text_indicate_progress);
//                if(indicator != null){
//                    indicator.setText(R.string.scan_lens);
//                }
            }else{
                registerReceiver(mBroadcastReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
                mWifiManager.setWifiEnabled(true);
            }
        }

    }

    private void initView(){
        lensMonitorView = (LensMonitorView)findViewById(R.id.lens_monitor);
        ivShoot = (ImageView)findViewById(R.id.btn_capture);
        ivShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = new ProgressDialog(LensMainActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage(getResources().getString(R.string.save_image));
                pd.show();
                String fileName = System.currentTimeMillis() + ".png";
                savePhoto(fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(FileUtils.getFileAbsolutePath(LensMainActivity.this, SAVE_DIR + File.separator + fileName))), "image/*");
                startActivity(intent);
                pd.dismiss();
            }
        });
        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        galleryBtn = (ImageButton)findViewById(R.id.btn_gallery);
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LensMainActivity.this, GalleryActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LensMonitorView", "lens main activity onResume");
        if(!LenUtils.isConnectEHT(this)){
            Log.d("LensMonitorView", "2xxxxxxxxxxxxxxxx");
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        lensMonitorView.stop();
        // 断开镜头wifi
//        if(isConnectLens()){
//            restoreWifiInfo();
//        }
        super.onDestroy();
    }



    public void restoreWifiInfo() {
        Log.d(TAG, "restoreWifiInfo");
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.disconnect();
        int networkId = getCurrentNetWorkId();
        if (networkId >= 0) {
            wifiManager.enableNetwork(networkId, true);
            wifiManager.reconnect();
        } else {
            // 3G
            wifiManager.setWifiEnabled(false);
        }

    }

    public void setCurrentNetWorkId(int i) {
        currentNetWorkId = i;
    }

    public int getCurrentNetWorkId() {
        return currentNetWorkId;
    }

    private void saveBitmapWithPrompt(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_entry_text,null);
        final EditText et = (EditText) view.findViewById(R.id.et);
        et.setText(System.currentTimeMillis() + ".png");
        new AlertDialog.Builder(LensMainActivity.this).setView(view).setTitle(R.string.save_pic_name).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!et.getText().toString().isEmpty()) {
                    savePhoto(et.getText().toString());
                }else{
                    et.setError(getString(R.string.not_null));
                }
            }
        }).setNegativeButton(R.string.cance,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
    }
    /**
     *
     * @param name 文件名
     */
    protected void savePhoto(String name){
        String filePath = null;
        Bitmap bmp = lensMonitorView.getCaptureImage();
        if (bmp != null) {
            FileOutputStream fos;
            String filename = name;
            filePath = SAVE_DIR + File.separator + filename;
            try {
                fos = FileUtils.openFile(LensMainActivity.this,filePath,false);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
