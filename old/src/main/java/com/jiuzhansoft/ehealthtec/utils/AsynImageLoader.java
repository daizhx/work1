package com.jiuzhansoft.ehealthtec.utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;


public class AsynImageLoader {
	private static final String TAG = "AsynImageLoader";
	public static final String CACHE_DIR = "eht_pic";
	private Map<String, SoftReference<Bitmap>> caches;
	private List<Task> taskQueue;
	private boolean isRunning = false;
	private boolean isCacheDisk = true;
	private Context mContext;
	
	public AsynImageLoader(){
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		isRunning = true;
		new Thread(runnable).start();
	}
	
	public AsynImageLoader(boolean istoCacheDisk){
		isCacheDisk = istoCacheDisk;
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		isRunning = true;
		new Thread(runnable).start();
	}
	
	public void setCacheDisk(boolean b){
		isCacheDisk = b;
	}

	public void showImageAsyn(Context c,ImageView imageView, String url, int resId){
		mContext = c;
		imageView.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView));
		
		if(bitmap == null){
			imageView.setImageResource(resId);
		}else{
			imageView.setImageBitmap(bitmap);
//			fillBitmap(imageView, bitmap);
		}

	}
	
	private void fillBitmap(ImageView iv, Bitmap bitmap){
		if(bitmap == null || iv == null){
			return;
		}
		
		int w = iv.getWidth();
		int h = iv.getHeight();
		
		int w1 = bitmap.getWidth();
		int h1 = bitmap.getHeight();
		
		Matrix m = new Matrix();
		m.setScale(w/w1, h/h1);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, w1, h1, m, false);
		iv.setImageBitmap(newBmp);
	}
	
	public Bitmap loadImageAsyn(String path, ImageCallback callback){
		if(caches.containsKey(path)){
			// ȡ��������
			SoftReference<Bitmap> rf = caches.get(path);
			// ͨ�������ã���ȡͼƬ
			Bitmap bitmap = rf.get();
			// �����ͼƬ�Ѿ����ͷţ��򽫸�path��Ӧ�ļ���Map���Ƴ���
			if(bitmap == null){
				caches.remove(path);
			}else{
				// ���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ
				Log.i(TAG, "return image in cache" + path);
				return bitmap;
			}
		}else{
			//�ж��Ƿ���������disk��
			if(isCacheDisk){
				
			File cacheFile = FileUtil.getDiskCacheFile(mContext, null, FileUtil.getFileName(path));
			if(cacheFile.length() > 0){
				try {
					Bitmap bitmap = BitmapFactory.decodeFile(cacheFile.getCanonicalPath());
					return bitmap;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			}
			// ��������в����ڸ�ͼƬ���򴴽�ͼƬ��������
			Task task = new Task();
			task.path = path;
			task.callback = callback;
			Log.i(TAG, "new Task ," + path);
			if(!taskQueue.contains(task)){
				taskQueue.add(task);
				// �����������ض���
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}
		
		// ������û��ͼƬ�򷵻�null
		return null;
	}
	
	/**
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageView imageView){
		return new ImageCallback() {
			
			@Override
			public void loadImage(String path, Bitmap bitmap) {
				Log.d("daizhx", "loadImage="+bitmap);
				if(path.equals(imageView.getTag().toString()) && bitmap != null){
					imageView.setImageBitmap(bitmap);
//					fillBitmap(imageView, bitmap);
				}
			}
		};
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// ���߳��з��ص�������ɵ�����
			Task task = (Task)msg.obj;
			// ����callback�����loadImage����������ͼƬ·����ͼƬ�ش���adapter
			task.callback.loadImage(task.path, task.bitmap);
		}
		
	};
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			while(isRunning){
				// �������л���δ���������ʱ��ִ����������
				Log.d(TAG, "runing");
				while(taskQueue.size() > 0){
					// ��ȡ��һ�����񣬲���֮�����������ɾ��
					Task task = taskQueue.remove(0);
					
					// �����ص�ͼƬ��ӵ�����
					if(isCacheDisk){
						task.bitmap = PicUtil.getbitmapAndwrite(mContext, task.path);
					}else{
						task.bitmap = PicUtil.getbitmap(task.path);
					}

					caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
					
					if(handler != null){
						// ������Ϣ���󣬲�����ɵ�������ӵ���Ϣ������
						Message msg = handler.obtainMessage();
						msg.obj = task;
						// ������Ϣ�����߳�
						handler.sendMessage(msg);
					}
				}
				
				//�������Ϊ��,�����̵߳ȴ�
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	//�ص��ӿ�
	public interface ImageCallback{
		void loadImage(String path, Bitmap bitmap);
	}
	
	class Task{
		// �������������·��
		String path;
		// ���ص�ͼƬ
		Bitmap bitmap;
		// �ص�����
		ImageCallback callback;
		
		@Override
		public boolean equals(Object o) {
			Task task = (Task)o;
			return task.path.equals(path);
		}
	}
}