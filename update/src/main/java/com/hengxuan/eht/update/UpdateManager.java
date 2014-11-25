package com.hengxuan.eht.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.loopj.android_async_http.http.JsonHttpResponseHandler;
import com.loopj.android_async_http.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ehtRestClient.EhtRestClient;

/**
 * Created by Administrator on 2014/10/9.
 * android应用包升级管理
 */
public class UpdateManager {
    private static final String TAG = "UpdateManager";
    private static UpdateManager updateManager = null;
    private Context mContext = null;
    //可更新的版本号
    private String versionCode;
    //可更新的标志,1-可选更新，2-强制更新
    private int updateFlag;
    //下载包url
    private String updateUrl;
    //更新详情信息
    private String updateDetail;

    private UpdateManager (Context context){
        mContext = context;
    }

    public static synchronized UpdateManager getUpdateManager(Context context){
        updateManager = new UpdateManager(context);
        return updateManager;
    }

    /**
     * 检查更新，通过该方法来更新应用包
     * @param packageName
     * @param versionCode
     */
//    public void checkUpdate(String packageName,String versionCode){
//        HttpSetting httpSetting = new HttpSetting();
//        httpSetting.setRequestMethod("GET");
//        httpSetting.setReadTimeout(600);
//        httpSetting.setFunctionModal("soft");
//        httpSetting.setFunctionId("version_update");
//        //参数
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("packageStr", packageName);
//            jsonObject.put("versionCode", versionCode);
//            jsonObject.put("osSystem", "1");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        httpSetting.setJsonParams(jsonObject);
//        httpSetting.setListener(new HttpGroup.OnAllListener(){
//
//            @Override
//            public void onEnd(HttpResponse response) {
//                JSONObject json = response.getJSONObject();
//                Log.d("daizhx", "onEnd:json="+json);
//                if(json != null){
//                    try {
//                        String code = json.getString("code");
//                        String msg = json.getString("msg");
//                        JSONObject object = json.getJSONObject("object");
//                        Log.d("daizhx", "code="+code+",msg="+msg+",object="+object);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onError(HttpError httpError) {
//                Log.d("daizhx", "onError");
//            }
//
//            @Override
//            public void onProgress(int i, int j) {
//                Log.d("daizhx", "onProgress");
//            }
//
//            @Override
//            public void onStart() {
//                Log.d("daizhx", "onStart");
//            }
//        });
//        httpSetting.setNotifyUser(true);
//        HttpGroupaAsynPool.getHttpGroupaAsynPool((Activity)mContext).add(httpSetting);
//
//    }

    public void checkUpdate(String packageName,String versionCode){
        Log.d(TAG, "check update:package="+packageName+",versionCode="+versionCode);
        List params = new ArrayList();
        params.add(packageName);
        params.add(versionCode);
        params.add("2");//android
        EhtRestClient.get("soft/version_update", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getInt("code") == 1){
                        //可更新-弹出提示框
                        JSONObject object = response.getJSONObject("object");
                        Log.d(TAG, "object="+object);
                        getUpdateInfo(object);
                        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setTitle(R.string.app_update);
                        alertDialog.setMessage(updateDetail);
                        if(updateFlag == 1){
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //启动下载更新服务
                                    Intent intent = new Intent(mContext, UpdateVersionService.class);
                                    intent.putExtra("apkPath", updateUrl);
                                    mContext.startService(intent);
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getString(R.string.not_update),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    alertDialog.dismiss();
                                }
                            });
                        }else{
                            //必须更新
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //启动下载更新服务
                                    Intent intent = new Intent(mContext, UpdateVersionService.class);
                                    intent.putExtra("apkPath", updateUrl);
                                    mContext.startService(intent);
                                    alertDialog.dismiss();
                                    //退出主页
                                    if(mContext instanceof Activity){
                                        ((Activity)mContext).finish();
                                    }
                                }
                            });
                            alertDialog.setCanceledOnTouchOutside(false);
                        }
                        alertDialog.show();
                    }else{
                        //没有更新
                        //TODO should remove after test
//                        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                        alertDialog.setTitle(R.string.app_update);
//                        alertDialog.setMessage("test update");
//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, mContext.getString(R.string.update), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //启动下载更新服务
//                                Intent intent = new Intent(mContext, UpdateVersionService.class);
//                                intent.putExtra("apkPath", "http://eht.hk/eht.apk");
//                                mContext.startService(intent);
//                                alertDialog.dismiss();
//                            }
//                        });
//                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "onFailure1---statusCode=" + statusCode + ",response=" + responseString + ",throwable=" + throwable);
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "onFailure2");
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getUpdateInfo(JSONObject object) {
        try {
            versionCode = object.getString("versionCode");
            updateFlag = object.getInt("updateFlag");
            updateUrl = object.getString("softUrl");
            updateDetail = object.getString("updateDetail");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
