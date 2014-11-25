package com.hengxuan.eht.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


/**
 * 下载新版本服务
 */
public class UpdateVersionService extends Service {
    private static String TAG = "UpdateVersionService";
	private String fileDir, filePath;
	private HttpGet get;
	private long downloadLenght = 0;
	private int progress = 0;
	private String newVersionName; //版本名称
	private String apkPath; //下载路径
	private NotificationManager manager;
	private Notification nf;
	private RemoteViews rvs;
	/** 是否已下载*/
	private boolean done = false;
	private Intent onClickIntent; //下载中点击
	private Intent onClearIntent; //下载完成后点击

	// SD卡状态
	private int hasSD = 1; // 1.SD卡 2.手机内存
	private final IBinder binder = new MyBinder();
	public boolean updateFlag = true;

    public static final String SERVER_ROOT = "http://182.254.137.149/";
	@Override
	public void onCreate() {
		super.onCreate();
        Log.d(TAG, "UpdateVersionService onCreate!");
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nf = new Notification();
		rvs = new RemoteViews(getPackageName(), R.layout.notification_view);
		onClickIntent = new Intent(this, OnClickNotificationService.class);
		onClearIntent = new Intent(this, OnClearNotificationService.class);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //拼接地址
        apkPath = SERVER_ROOT + intent.getStringExtra("apkPath");
//		String version = intent.getStringExtra("version");
        if(apkPath == null){
            return START_NOT_STICKY;
        }
        newVersionName = apkPath.substring(apkPath.lastIndexOf("/") + 1);
//		System.out.println("=============" + newVersionName);

        String sdcardState = Environment.getExternalStorageState();
        if (sdcardState.equals("mounted")) {// SD是否挂载
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            fileDir = sdcardPath + "/eht";
            filePath = fileDir + "/" + newVersionName;
            onClickIntent.putExtra("filePath", filePath);
            onClickIntent.putExtra("intent", intent);
            onClearIntent.putExtra("intent", intent);
            onClickIntent.putExtra("done", done);
            onClearIntent.putExtra("done", done);

            rvs.setTextViewText(R.id.tv_new_version_name, newVersionName);
            rvs.setTextViewText(R.id.tv_progress, progress + "%");
            rvs.setProgressBar(R.id.pb_download_progress, 100, progress, false);

            nf.icon = R.drawable.ic_launcher;
            nf.tickerText = "正在下载新版本";
            nf.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
            nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //通知栏内的布局view
            nf.contentView = rvs;
            // 当点击通知栏的通知时就会跳转到依赖的服务(UpdateNewVersionService)
            nf.contentIntent = PendingIntent.getService(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 当清除通知栏的通知时就会跳转到依赖的服务(EndService)
            nf.deleteIntent = PendingIntent.getService(this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (downloadLenght == 0) {
                if (!done) {
                    manager.notify(1, nf);
                    new DownloadNewApkTask().execute();
                } else {
                    Toast.makeText(this, "已经完成下载", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            hasSD = 2;
            //没有SD卡
            String sdcardPath = Environment.getDataDirectory().toString();
            fileDir = getCacheDir().getAbsolutePath();
            // File cache = getCacheDir() ;
            filePath = fileDir + "/" + newVersionName;
            onClickIntent.putExtra("filePath", filePath);
            onClickIntent.putExtra("intent", intent);
            onClearIntent.putExtra("intent", intent);
            onClickIntent.putExtra("done", done);
            onClearIntent.putExtra("done", done);

            rvs.setTextViewText(R.id.tv_new_version_name, newVersionName);
            rvs.setTextViewText(R.id.tv_progress, progress + "%");
            rvs.setProgressBar(R.id.pb_download_progress, 100, progress, false);

            nf.icon = R.drawable.ic_launcher;
            nf.tickerText = "正在下载新版本";
            nf.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
            nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            nf.contentView = rvs;
            nf.contentIntent = PendingIntent.getService(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            nf.deleteIntent = PendingIntent.getService(this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (downloadLenght == 0) {
                if (!done) {
                    manager.notify(1, nf);
                    new DownloadNewApkTask().execute();
                } else {
                    Toast.makeText(this, "已经完成下载", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return START_NOT_STICKY;
    }


    /** 下载新版本任务线程 */
	private class DownloadNewApkTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			updateFlag = false;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				HttpClient client = new DefaultHttpClient();
				/*
				 * get = new HttpGet(Config.UPDATE_SERVER +
				 * Config.UPDATE_APKNAME);
				 */
				get = new HttpGet(apkPath);
				HttpResponse response = client.execute(get);
				long totalLenght = response.getEntity().getContentLength();
				InputStream inputStream = response.getEntity().getContent();

				File file = new File(fileDir);
				if (!file.exists()) {
					file.mkdirs();
				}
				FileOutputStream outputStream = new FileOutputStream(filePath);

				long ctm = 0;
				int lenght = -1;
				byte[] buffer = new byte[1024];
				while ((lenght = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, lenght);
					downloadLenght += lenght;

					double percent = 100 * downloadLenght / totalLenght;
					String percent2 = String.valueOf(percent);
					if (percent2.contains(".")) {
						percent2 = percent2.substring(0, percent2.indexOf("."));
					}
					progress = Integer.valueOf(percent2);
					if ((System.currentTimeMillis() - ctm) >= 1000) {//每一秒更新一次进度条
						publishProgress(0);
						ctm = System.currentTimeMillis();
					}

					if (downloadLenght == totalLenght) {// 下载成功
						publishProgress(1);
					}
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// 更新通知栏
			rvs.setTextViewText(R.id.tv_new_version_name, newVersionName);
			rvs.setTextViewText(R.id.tv_progress, progress + "%");
			rvs.setProgressBar(R.id.pb_download_progress, 100, progress, false);

			manager.notify(1, nf);

			if (values[0] == 1) {

				updateFlag = true;

				progress = 0;
				downloadLenght = 0;

				done = true;
				onClickIntent.putExtra("done", done);
				onClearIntent.putExtra("done", done);

				manager.cancel(1);

//				nf.icon = R.drawable.ic_launcher;
//				rvs.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
//				nf.tickerText = "下载成功";
//				nf.defaults = Notification.DEFAULT_LIGHTS;
//				nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//				nf.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.finish);
//				nf.contentIntent = PendingIntent.getService(UpdateVersionService.this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//				nf.deleteIntent = PendingIntent.getService(UpdateVersionService.this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//				manager.notify(2, nf);
				// 清除图片缓存 （异步操作）
				// new RemoveCache().execute();

				if (hasSD == 1) {
					Intent it = new Intent(Intent.ACTION_VIEW);
					it.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(it);
				} else { // hasSD = 2 ;
					String localfile = "";
					localfile = getCacheDir() + "/" + newVersionName;
                    // chmod 755 /* 755 权限是对apk自身应用具有所有权限， 对组和其他用户具有读和执行权限 */
					String cmd = "chmod 777 " + localfile;
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						e.printStackTrace();
					}

					Intent it = new Intent(Intent.ACTION_VIEW);
					File file = new File(localfile);
					it.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(it);
				}
			}
		}
	}

	/*
	 * private class RemoveCache extends AsyncTask<Void, Integer, Void> { String
	 * cachePath = "" ;
	 * 
	 * @Override protected Void doInBackground(Void... params) {
	 * if(Tools.isSDcard()) { cachePath =
	 * Environment.getExternalStorageDirectory() .getPath() +"/" +Tools.CACHDIR;
	 * }else { cachePath = Environment.getDataDirectory().getPath() +"/"
	 * +Tools.CACHDIR; } removeCache(cachePath) ; return null; }
	 * 
	 * }
	 * 
	 * private boolean removeCache(String dirPath) { File dir = new
	 * File(dirPath); File[] files = dir.listFiles(); if (files == null) {
	 * return true; } if
	 * (!android.os.Environment.getExternalStorageState().equals(
	 * android.os.Environment.MEDIAreturn UpdateVersionService.this;_MOUNTED)) { return false; }
	 * 
	 * if(files.length >0){
	 * 
	 * for (int i = 0; i < files.length; i++) {
	 * 
	 * if (files[i].getName().contains(Tools.WHOLESALE_CONV)) {
	 * files[i].delete(); } } } return true; }
	 */

	public class MyBinder extends Binder {
		public UpdateVersionService getService() {
			return UpdateVersionService.this;
		}
	}

}
