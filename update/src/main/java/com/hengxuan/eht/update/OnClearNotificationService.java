package com.hengxuan.eht.update;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** 
 * ���ڷ���
 */
public class OnClearNotificationService extends Service {

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Intent i = intent.getParcelableExtra("intent");
		boolean done = intent.getBooleanExtra("done", false);

		if (done) {// ������ɺ�������ť�Ĳ���
			stopService(i);
		} else {// �����е������ť�Ĳ���
			// Toast.makeText(this, "����Ϊ�����أ����Եȣ�", Toast.LENGTH_SHORT).show();
		}
		stopService(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
