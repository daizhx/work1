package com.hengxuan.eht.massager.Music;

import java.io.IOException;
import java.util.List;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.hengxuan.eht.logger.Log;


public class MusicService extends Service implements OnCompletionListener{
    private static final String TAG = "music";
	String[] mCursorCols = new String[]{
			"audio._id AS _id",
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION
	};
	
	private MediaPlayer mMediaPlayer;
	private Cursor mCursor;
    //播放位置
	private int mPlayPosition = 0;
	public static final String PLAY_ACTION = "com.hengxuan.eht.service.PLAY_ACTION";
	public static final String PAUSE_ACTION = "com.hengxuan.eht.service.PAUSE_ACTION";
	public static final String NEXT_ACTION = "ccom.hengxuan.eht.service.NEXT_ACTION";
	public static final String PREVIOUS_ACTION = "com.hengxuan.eht.service.PREVIOUS_ACTION";
	public static final String PLAY_RAMMUSIC = "com.hengxuan.eht.service.PLAY_RAMMUSIC";
	public static int SENDMESSAGE_TIME_GAP = 500;
	
	private SharedPreferences preferences;
	private int currentPosition;
	public static boolean flag = false;
	
	public static Visualizer mVisualizer;
	protected short averagenum;
	
	private int count = 0;
	
	private Handler timeHandler;
	
	//song play changed
	public interface OnSongChangedListener{
		void onSongChanged(int position, int duration);
	}
	
	private OnSongChangedListener onSongChangedListener;
	
	public interface OnUpdatePlaytime{
		void updatePlaytime(int time);
	}
	
	private OnUpdatePlaytime onUpdatePlaytime;

    //蓝牙通信中断监听处理函数
    public interface OnBTDisconnectListener{
        void onBTDisconnect();
    }
    private OnBTDisconnectListener onBTDisconnectListener;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return new MusicServiceBinder();
	}
	
	public class MusicServiceBinder extends Binder{
		public MusicService getService(){
			return MusicService.this;
		}
	}

    /**
     *判断服务是否在运行
     * @param mContext
     * @return
     */
	public static boolean isServiceRunning(Context mContext) {
        boolean isRunning = false;
        String className = "com.hengxuan.eht.massager.Music.MusicService";
        ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        
        /*if (!(serviceList.size()>0)) {
            return false;
        }*/
        for (int i=0; i<serviceList.size(); i++) {
       	 String strClassName = serviceList.get(i).service.getClassName();
            if (className.equalsIgnoreCase(strClassName)) {
                isRunning = true;
                break;
            }
        }
        Log.d("daizhx", "isRunning="+isRunning);
        if(!isRunning){
        	if(null != mVisualizer){
        		mVisualizer.setEnabled(false);
        		mVisualizer = null;
        	}
        }
        return isRunning;
    }

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// BlueToothInfo.sendCommandToDevice(BlueToothInfo.MODE_TAG_8);
		
		preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
		currentPosition = preferences.getInt("currentPosition", -1);
		if(currentPosition == -1)
			mPlayPosition = 0;
		else
			mPlayPosition = currentPosition;
		mMediaPlayer = new MediaPlayer();
		Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		mCursor = getContentResolver().query(MUSIC_URL, mCursorCols, "duration > 60000", null, null);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);		
		
//		if(intent != null){
//			String action = intent.getAction();
//			if(action.equals(PLAY_ACTION)){
//				play();
//				mMediaPlayer.setOnCompletionListener(this);
//			}
//			else if(action.equals(PAUSE_ACTION))
//				pause();
//			else if(action.equals(NEXT_ACTION)){
//				next();
//				mMediaPlayer.setOnCompletionListener(this);
//			}
//			else if(action.equals(PREVIOUS_ACTION)){
//				previous();
//				mMediaPlayer.setOnCompletionListener(this);
//			}
//			else if(action.equals(PLAY_RAMMUSIC)){
//				SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
//				int currentPosition = preferences.getInt("currentPosition", -1);
//				if(currentPosition != -1){
//					playMusic(currentPosition);
//					mMediaPlayer.setOnCompletionListener(this);
//				}
//			}			
//		}
		mMediaPlayer.setOnCompletionListener(this);
	}

	public void play() {
		// TODO Auto-generated method stub
		if(flag){
			mMediaPlayer.start();
			flag = false;
		}else{
			init();			
		}
	}

	public void pause() {
		// TODO Auto-generated method stub
		//stopSelf();
		mMediaPlayer.pause();
		flag = true;
	}

	public void next() {
		// TODO Auto-generated method stub
		if(mPlayPosition == mCursor.getCount() - 1)
			mPlayPosition = 0;
		else
			mPlayPosition ++;
		flag = false;
		init();
	}

	public void previous() {
		// TODO Auto-generated method stub
		if(mPlayPosition == 0)
			mPlayPosition = mCursor.getCount() - 1;
		else
			mPlayPosition -- ;
		flag = false;
		init();
	}
	
	private int  averageAmplitude( byte[] waveform)
    {
    	int sum = 0;
    	if(waveform.length > 0)
    	{
    		for(int i=0;i<waveform.length;i++)
    		{
    			sum = sum + waveform[i];
    		}
    		return sum/waveform.length;
    	}
    	else
    		return 0;
    }
	
	private int transformationCommandFormat(int amplitude)
	{
		/*
		int m = amplitude + 32;
		if(m <0)
			m = 0;
		else if(m > 64)
			m = 64;
		m = (16 * m)/64; 
		*/
		if(amplitude < 0) {
			amplitude = 0;
		} else if(amplitude > 255) {
			amplitude = 255;
		}
		
		return amplitude + 0x1100;
	}
	
	public void setSongChangedListener(OnSongChangedListener listener){
		onSongChangedListener = listener;
	}
	
	public void setOnUpdatePlaytime(OnUpdatePlaytime obj){
		onUpdatePlaytime = obj;
	}

    public void setOnBTDisconnectListener(OnBTDisconnectListener obj){
        onBTDisconnectListener = obj;
    }

    /**
     * Mediaplayer播放mPlayPosition对应的音乐
     */
	public void init(){
        //fix bug,no songs will cause crash
        if(mCursor.getCount() == 0){
            return;
        }
        //end
		mMediaPlayer.reset();
		String dataSource = getDateByPositon(mCursor, mPlayPosition);
		String info = getInfoByPostion(mCursor, mPlayPosition);
		Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
		
		try{
			mMediaPlayer.setDataSource(dataSource);
			mMediaPlayer.prepare();
			
			int duration = mMediaPlayer.getDuration();
			if(onSongChangedListener != null){
				onSongChangedListener.onSongChanged(mPlayPosition, duration);
			}
			
			if(mVisualizer != null){
				mVisualizer.setEnabled(false);
				mVisualizer = null;
			}
			mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
			mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
			mVisualizer.setDataCaptureListener(new OnDataCaptureListener(){
				@Override
	            public void onWaveFormDataCapture(Visualizer visualizer,
	                    byte[] waveform, int samplingRate){
					if(flag || !MusicService.isServiceRunning(MusicService.this)) {
					    Log.e(TAG, "onWaveFormDataCapture return");
						return;
					}
					// TODO Auto-generated method stub
					if(Log.E) {
						StringBuilder strBuilder = new StringBuilder("len = ").append(waveform.length); 
    					Log.e(TAG, "OnWaveFormDataCature : " + strBuilder.toString());
    				}
					/*int dataArr[] = new int[17];
					for(int i = 0; i < waveform.length; i++) {
						int data = (waveform[i] + 128) / 16;
						dataArr[data]++;
					}
					
					int max = dataArr[0];  
					int index = 0;
			        for (int j = 1; j < dataArr.length; j++) {
			            if (dataArr[j] > max) {  
			                max = dataArr[j];  
			                index = j;  
			            }  
			        }
			        
			        final short message = (short)transformationCommandFormat(index);
        			BlueToothInfo.sendCommandToDevice(message);
        			*/
			        
					
					// averagenum = (short)averageAmplitude(waveform);
					//int total = 0;
					//int average = 0;
					count = 0;
	            	//for(int i = 1; i < waveform.length; i++) {
	            	//	if(i % SENDMESSAGE_TIME_GAP == 0) {
	            			// average = total / SENDMESSAGE_TIME_GAP;
	            			count ++;
	            			if(Log.E) {
								StringBuilder strBuilder = new StringBuilder("value = ").append(waveform[0] + 128);
		    					Log.e(TAG, strBuilder.toString());
		    				}
	            			
	            			final short message = (short)transformationCommandFormat(waveform[0] + 128);
	            			// BlueToothInfo.sendCommandToDevice(message);
	            			
	            			Handler handler = new Handler();
	        				handler.postDelayed(new Runnable() {
	        					@Override
	        					public void run() {
									if(!BluetoothServiceProxy.sendCommandToDevice(message)){
                                        onBTDisconnectListener.onBTDisconnect();
                                    }
	        					}
	        				}, 0); // (100 * SENDMESSAGE_TIME_GAP / waveform.length) * count);
	            		//}
	            	//}
	            	count = 0;
	            }
	 
	            @Override
	            public void onFftDataCapture(Visualizer visualizer, byte[] fft,
	                    int samplingRate){
	                // TODO Auto-generated method stub
	 
	            }
	        }, Visualizer.getMaxCaptureRate() / 2, true, false);
			mMediaPlayer.start();
			mVisualizer.setEnabled(true);
			//start timer
			timeHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == 1) {
                        try {
                            int currentTime = mMediaPlayer.getCurrentPosition();
                            onUpdatePlaytime.updatePlaytime(currentTime);
                        }catch (IllegalStateException e){
                            //mediaPlayer release了退出
                            return;
                        }


					}
					timeHandler.sendEmptyMessageDelayed(1, 600);
				}
			};
			timeHandler.sendEmptyMessage(1);
		}catch(IllegalArgumentException e1){
			e1.printStackTrace();
		}catch(IllegalStateException e1){
			e1.printStackTrace();
		}catch(IOException e1){
			e1.printStackTrace();
		}
	}
	
	public void playMusic(int position){
		mPlayPosition = position;
		flag = false;
		init();
	}

	private String getDateByPositon(Cursor c, int position) {
		// TODO Auto-generated method stub
		c.moveToPosition(position);
		int dataColumn = c.getColumnIndex(MediaStore.Audio.Media.DATA);
		String data = c.getString(dataColumn);
		return data;
	}

	private String getInfoByPostion(Cursor c, int position) {
		// TODO Auto-generated method stub
		c.moveToPosition(position);
		int titleColumn = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int artistColumn = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
		String info = c.getString(artistColumn)+" "+c.getString(titleColumn);
		//musicArtist = c.getString(artistColumn);
		//musicTitle = c.getString(titleColumn);
		return info;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMediaPlayer.release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mPlayPosition = mPlayPosition + 1;
		if(mPlayPosition == mCursor.getCount() - 1)
			mPlayPosition = 0;
		init();
	}
}
