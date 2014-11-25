package com.hengxuan.eht.update;

import java.io.File;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 更新新版本服务
 */
public class OnClickNotificationService extends Service {
	private String filePath;

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		filePath = intent.getStringExtra("filePath");
		Intent i = intent.getParcelableExtra("intent");
		boolean done = intent.getBooleanExtra("done", false);

		if (done) {
            // 执行安装操作
			Intent it = new Intent(Intent.ACTION_VIEW);
			it.setDataAndType(Uri.fromFile(new File(filePath)),
					"application/vnd.android.package-archive");
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(it);

			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(2);
			stopService(i);
		} else {
			Toast.makeText(this, "请稍等，任务下载中。。。", Toast.LENGTH_SHORT).show();
		}
		stopService(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
