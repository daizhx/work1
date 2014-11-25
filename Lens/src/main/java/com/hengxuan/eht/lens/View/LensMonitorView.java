package com.hengxuan.eht.lens.View;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LensMonitorView extends SurfaceView implements SurfaceHolder.Callback{

	public static final String TAG = "LensMonitorView";
    //绘制图像线程
	private IrisMonitorThread thread;
	private LensMonitorParameter cmPara;
	private Bitmap capture_bitmap = null;
    private float previewRatio = (float) (16.0/9.0);
    private Context mContext;
    //socketCamera，图片源
    private CameraSource cs;
    private SurfaceHolder mSurfaceHolder;

	public LensMonitorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mContext = context;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		Log.d(TAG, "LensMonitorView get Measured size:" + width + "x" + height);
		if(width == 0 || height == 0){
			return;
		}
		if(((float)height/(float)width) > previewRatio){
			height = (int)(((float)width)*previewRatio);
		}else if(((float)height/(float)width) < previewRatio){
			width = (int)(((float)height)/previewRatio);
		}
		setMeasuredDimension(width, height);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		Log.d(TAG, "surfaceChanged:LensMonitorView:"+width+"x"+height);
        cs = new SocketCamera(width, height, true);
        thread.setSurfaceSize(width,height);
        start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "LensMonitorView--Created");
        initParam();
        try {
            URL url = new URL("http://"+cmPara.getIp()+":"+cmPara.getPort());
            thread = new IrisMonitorThread(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        mSurfaceHolder = holder;

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "LensMonitorView--Destroyed");
		if(cs != null){
            cs.close();
        }
		if(capture_bitmap != null) {
			if(!capture_bitmap.isRecycled()){
				capture_bitmap.recycle();
			}
		}
        //关闭工作线程
        if(thread != null){
            thread.mRun = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }
	}

    public LensMonitorParameter getCmPara() {
        return cmPara;
    }

    public void setCmPara(LensMonitorParameter cmPara) {
        this.cmPara = cmPara;
    }

    public void setRunning(boolean b) {
        this.thread.setRunning(b);
    }

    public void start(){
        //thread initialized in the construction
        if(thread != null && !thread.mRun) {
            thread.setRunning(true);
            thread.start();
        }
    }
    public void stop(){
        if(thread != null && thread.mRun){
            this.thread.setRunning(false);
//            this.thread.interrupt();
//            this.thread = null;
        }
    }

    public IrisMonitorThread getThread() {
        return thread;
    }

    public Bitmap getCaptureImage()
    {
        return cs.getCaptureImage();
    }
	
	private LensMonitorParameter initParam()
	{
		LensMonitorParameter param = new LensMonitorParameter();
		param.setId(1);
		param.setConnectType(0);
		param.setIp("10.10.10.254");
		param.setLocal_dir("/sdcard");
		param.setName("192.168.1.102");
		param.setUsername("aaaaa");
		param.setPassword("123456");
		param.setPort(8080);
		param.setTime_out(2000);
		param.setConnectType(mContext.BIND_AUTO_CREATE);
        cmPara = param;
		return param;
	}


	public class IrisMonitorThread extends Thread{
		
//		private SurfaceHolder mSurfaceHolder;

		public boolean mRun = false;

        private int mCanvasHeight = 1;

        private int mCanvasWidth = 1;

//	    private CameraSource cs;
//	    private Canvas canvas = null;
        private URL url = null;
        private HttpURLConnection httpURLconnection;
	    
	    public IrisMonitorThread(URL u) {
			super();
            url = u;
		}
	    
	    public void setRunning(boolean b) {
            mRun = b;
        }

		@Override
		public void run() {
            Log.d(TAG, "work thread start");
            while(mRun){
                try {
                    httpURLconnection = (HttpURLConnection)url.openConnection();
                    httpURLconnection.setRequestMethod("GET");
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                httpURLconnection.setReadTimeout(2*1000);
                Canvas canvas = mSurfaceHolder.lockCanvas(null);
                if(canvas == null){
                    continue;
                }
                if(!captureImage(canvas, httpURLconnection)){
                    Log.d(TAG, "draw Image fail!!!!!");
                }
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                    canvas = null;
                }
            }
		}

        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
            }
        }
		/**
		 * 从cs获取bitmap图像，并画到canvas上
		 * @param httpURLconnection
		 * @return true-success false-fail
		 */
		private boolean captureImage(Canvas c,HttpURLConnection httpURLconnection){
//            cs = new SocketCamera(mCanvasWidth, mCanvasHeight, true);
//	        cs.capture(c, httpURLconnection); //capture the frame onto the canvas
            Log.d(TAG, "capture image");
			if(cs.capture(c, httpURLconnection)){
	            return true;
			}else{
				return false;
			}
		}
		
		public boolean saveImage(){
			
			String now = String.valueOf(System.currentTimeMillis());
			if(cs == null){
				return false;
			}
			cs.saveImage(cmPara.getLocal_dir()+"/ehealthtec/image", now+".PNG");
			
			return true;
		}
		public CameraSource getCameraSource() {
			return cs;
		}
	}
}
