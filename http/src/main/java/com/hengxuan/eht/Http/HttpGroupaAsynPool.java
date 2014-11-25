package com.hengxuan.eht.Http;

import android.app.Activity;
import android.content.Context;

import com.hengxuan.eht.Http.constant.ConstHttpProp;
import com.hengxuan.eht.Http.utils.PooledThread;
import com.hengxuan.eht.Http.utils.ThreadPool;

public class HttpGroupaAsynPool extends HttpGroup {
	private static HttpGroupaAsynPool mHttpGroupaAsynPool;

	private HttpGroupaAsynPool(HttpGroupSetting paramHttpGroupSetting) {
		super(paramHttpGroupSetting);
	}

	public static HttpGroupaAsynPool getHttpGroupaAsynPool() {
		if(mHttpGroupaAsynPool == null){
			HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
			localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSON);
			mHttpGroupaAsynPool = new HttpGroupaAsynPool(localHttpGroupSetting);
		}
		return mHttpGroupaAsynPool;
	}
	
	public static HttpGroupaAsynPool getHttpGroupaAsynPool(Activity activity) {
		if(mHttpGroupaAsynPool == null){
			HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
//			localHttpGroupSetting.setMyActivity(activity);
			localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSON);
			mHttpGroupaAsynPool = new HttpGroupaAsynPool(localHttpGroupSetting);
		}
		HttpGroupSetting httpGroupSetting =  mHttpGroupaAsynPool.getHttpGroupSetting();
		httpGroupSetting.setMyActivity(activity);
		
		return mHttpGroupaAsynPool;
	}

	private static HttpGroup getHttpGroupaAsynPool(int paramInt) {
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
//		localHttpGroupSetting.setMyActivity(this);
		localHttpGroupSetting.setType(paramInt);
		return getHttpGroupaAsynPool(localHttpGroupSetting);
	}

	private static HttpGroup getHttpGroupaAsynPool(
			HttpGroupSetting paramHttpGroupSetting) {
		HttpGroupaAsynPool localHttpGroupaAsynPool = new HttpGroupaAsynPool(paramHttpGroupSetting);
		// addDestroyListener(localHttpGroupaAsynPool);
		return localHttpGroupaAsynPool;
	}

	@Override
	public void execute(final HttpRequest httpRequest) {
		ThreadPool threadpool = PooledThread.getThreadPool();
		int i = httpRequest.getHttpSetting().getPriority();
		threadpool.offerTask(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (httpList.size() < 1)
					onStart();
				httpList.add(httpRequest);
				httpRequest.nextHandler();

			}

		}, i);
	}

}