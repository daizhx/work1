package com.hengxuan.eht.lens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;


public class GuidActivity extends ActionBarActivity  implements GuidFragment1.OnFragmentInteractionListener{
    private GuidFragment1 guidFragment1 = new GuidFragment1();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guid);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, guidFragment1)
                    .commit();
        }

        mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
        registerReceiver(mBroadcastReceiver ,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }

    @Override
    public void checkWifi() {
        if(mWifiManager.isWifiEnabled()){
            mWifiManager.startScan();
        }else{
            mWifiManager.setWifiEnabled(true);
        }
    }
    WifiManager mWifiManager;
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

    private void checkScanResult(List<ScanResult> scanResults) {
        for(ScanResult scanResult : scanResults){
            Log.d("daizhx", "ssid=" + scanResult.SSID);
            if(scanResult.SSID.equals("EHT")){
                guidFragment1.progressBar.setVisibility(View.GONE);
                guidFragment1.tvLensIsOpen.setText(R.string.lens_available);
                return;
            }else{
                guidFragment1.progressBar.setVisibility(View.GONE);
                guidFragment1.tvLensIsOpen.setText(R.string.lens_not_open);
            }
        }
    }

    public boolean isConnectEHT(){
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if(wifiInfo == null){
            return false;
        }
        if(wifiInfo.getSSID().equals("\"" + "EHT"
                + "\"")){
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
