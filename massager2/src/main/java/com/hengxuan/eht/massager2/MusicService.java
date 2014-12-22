package com.hengxuan.eht.massager2;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.Toast;

import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.hengxuan.eht.massager2.logger.Log;

import java.io.IOException;
import java.util.List;


public class MusicService extends Service implements OnCompletionListener{

	String[] mCursorCols = new String[]{
			"audio._id AS _id",
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION
	};
	
	private MediaPlayer mMediaPlayer;
	private Cursor mCursor;
	private int mPlayPosition = 0;
    //current song
    private String mTitle;
    //current song artist
    private String mArtist;
	public static final String PLAY_ACTION = "com.jiuzhansoft.ehealthtec.service.PLAY_ACTION";
	public static final String PAUSE_ACTION = "com.jiuzhansoft.ehealthtec.service.PAUSE_ACTION";
	public static final String NEXT_ACTION = "ccom.hengxuan.ehealthplatform.service.NEXT_ACTION";
	public static final String PREVIOUS_ACTION = "com.jiuzhansoft.ehealthtec.service.PREVIOUS_ACTION";
	public static final String PLAY_RAMMUSIC = "com.jiuzhansoft.ehealthtec.service.PLAY_RAMMUSIC";
	public static int SENDMESSAGE_TIME_GAP = 500;

	public static boolean isPause = false;
	
	public static Visualizer mVisualizer;
	protected short averagenum;
	
	private Handler timeHandler;
    //define play mode
    private int mMode;
    private static final int  SEQUENCY= 0;
    private static final int LOOP = 1;
    private static final int REPEAT = 2;
	
	//song play changed

	public interface OnSongChangedListener{
		void onSongChanged(String title, String artist, int duration);
	}
	
	private OnSongChangedListener onSongChangedListener;

	public interface OnUpdatePlaytime{
		void updatePlaytime(int time);
	}
	
	private OnUpdatePlaytime onUpdatePlaytime;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return new MusicServiceBinder();
	}
	
	public class MusicServiceBinder extends Binder{
		public MusicService getService(){
			return MusicService.this;
		}
	}
	
	public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (int i=0; i<serviceList.size(); i++) {
       	 String strClassName = serviceList.get(i).service.getClassName();
            if (className.equalsIgnoreCase(strClassName)) {
                isRunning = true;
                break;
            }
        }
//        if(!isRunning){
//        	if(null != mVisualizer){
//        		mVisualizer.setEnabled(false);
//        		mVisualizer = null;
//        	}
//        }
        return isRunning;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		mMediaPlayer = new MediaPlayer();
		Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		mCursor = getContentResolver().query(MUSIC_URL, mCursorCols, "duration > 60000", null, null);
        mMediaPlayer.setOnCompletionListener(this);
        Log.d("daizhx","on create");
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("daizhx", "on StartC");

        return START_NOT_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void play() {
		if(isPause){
			mMediaPlayer.start();
            if(onUpdatePlaytime != null) {
                int time = mMediaPlayer.getCurrentPosition();
                onUpdatePlaytime.updatePlaytime(time);
            }
            if(onSongChangedListener != null){
                int duration = mMediaPlayer.getDuration();
                onSongChangedListener.onSongChanged(mTitle,mArtist,duration);
            }

			isPause = false;
            mVisualizer.setEnabled(true);
		}else{
			init();			
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void pause() {
		mMediaPlayer.pause();
		isPause = true;
        mVisualizer.setEnabled(false);
	}

	public void next() {
		if(mPlayPosition == mCursor.getCount() - 1) {
            mPlayPosition = 0;
        }else {
            mPlayPosition++;
        }
		init();
	}

	public void previous() {
		if(mPlayPosition == 0)
			mPlayPosition = mCursor.getCount() - 1;
		else
			mPlayPosition -- ;
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

    /**
     * play music
     */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void init(){
        if(mCursor.getCount() == 0){
            Toast.makeText(MusicService.this,getString(R.string.no_song),Toast.LENGTH_SHORT).show();
            return;
        }
		mMediaPlayer.reset();
		String dataSource = getDateByPositon(mCursor, mPlayPosition);
		getMusicInfo(mCursor, mPlayPosition);
		
		try{
			mMediaPlayer.setDataSource(dataSource);
			mMediaPlayer.prepare();
			
			int duration = mMediaPlayer.getDuration();
			if(onSongChangedListener != null){
				onSongChangedListener.onSongChanged(mTitle,mArtist, duration);
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
					if(Log.E) {
						StringBuilder strBuilder = new StringBuilder("len = ").append(waveform.length); 
    					Log.e("OnWaveFormDataCature : ", strBuilder.toString());
    				}
	            			if(Log.E) {
								StringBuilder strBuilder = new StringBuilder("value = ").append(waveform[0] + 128); 
		    					Log.e(" ", strBuilder.toString());
		    				}
	            			
	            			final short message = (short)transformationCommandFormat(waveform[0] + 128);
	            			// BlueToothInfo.sendCommandToDevice(message);
                            if(!BluetoothServiceProxy.sendCommandToDevice(message)){
                                Toast.makeText(MusicService.this,getString(R.string.disconnectbluetooth),Toast.LENGTH_SHORT).show();
                            }
//	            			Handler handler = new Handler();
//	        				handler.postDelayed(new Runnable() {
//
//	        					@Override
//	        					public void run() {
//									if(!BluetoothServiceProxy.sendCommandToDevice(message)){
//                                        Toast.makeText(MusicService.this,getString(R.string.disconnectbluetooth),Toast.LENGTH_SHORT).show();
//                                    }
//
//	        					}
//	        				}, 0);
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
						try{
						if(mMediaPlayer.isPlaying()){
							int currentTime = mMediaPlayer.getCurrentPosition();
                            if(onUpdatePlaytime != null) {
                                onUpdatePlaytime.updatePlaytime(currentTime);
                            }
						}
						}catch(IllegalStateException e){
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
		isPause = false;
		init();
	}

	private String getDateByPositon(Cursor c, int position) {
		c.moveToPosition(position);
		int dataColumn = c.getColumnIndex(MediaStore.Audio.Media.DATA);
		String data = c.getString(dataColumn);
		return data;
	}

	private void getMusicInfo(Cursor c, int position) {
		c.moveToPosition(position);
		int titleColumn = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
		int artistColumn = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        mTitle = c.getString(titleColumn);
        mArtist = c.getString(artistColumn);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMediaPlayer.release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
        switch (mMode){
            case SEQUENCY:
                if(mPlayPosition < mCursor.getCount() - 1){
                    mPlayPosition = mPlayPosition + 1;
                    init();
                }
                break;
            case LOOP:
                if(mPlayPosition < mCursor.getCount() - 1){
                    mPlayPosition = mPlayPosition + 1;
                    init();
                }else{
                    mPlayPosition = 0;
                    init();
                }
            case REPEAT:
                init();
                break;
            default:
                break;
        }
	}
}
