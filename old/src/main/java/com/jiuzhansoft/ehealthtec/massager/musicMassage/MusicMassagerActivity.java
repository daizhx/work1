package com.jiuzhansoft.ehealthtec.massager.musicMassage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseExtBTActivity;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService.MusicServiceBinder;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService.OnSongChangedListener;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService.OnUpdatePlaytime;
import com.jiuzhansoft.ehealthtec.user.UserLoginActivity;


public class MusicMassagerActivity extends BaseExtBTActivity implements
		OnClickListener {
	String[] mCursorCols = new String[] { "audio._id AS _id",
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION };
	private Cursor cursor;
	private ImageView mPrevious;
	private ImageView mPlay;
	private ImageView mNext;
	private int mDisplayWidth;
	private Object maxVolume;
	private AudioManager audioMgr;
	private ListView songList;
	private SlidingMenu mSlideMenu;
	private int playStatus;
	private static final int PLAYING = 1;
	private static final int PAUSING = 2;
	private boolean isring = false;
	private ComponentName component;
	
	private ImageView icList;
	
	//show the play song and artist
	private TextView tvSong;
	private TextView tvArtist;
	
	//inference to musicService
	private MusicService musicService;
	private int currentPosition;
	private TextView tvPlaytime;
	private TextView tvDuration;
	private SeekBar mSeekBar;



    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(R.string.music_massage);
		component = new ComponentName(this, MusicService.class);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mDisplayWidth = displayMetrics.widthPixels;
		setContentView(R.layout.actvity_music_massage);
		initSlideMenu();
		setUp();
        startMusicService();
        bindMusicService();
    }

    private void initSlideMenu(){
        mSlideMenu = new SlidingMenu(this);
        mSlideMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        mSlideMenu.setBehindOffset(mDisplayWidth / 4);
        mSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mSlideMenu.setMode(SlidingMenu.RIGHT);
        mSlideMenu.setMenu(R.layout.song_list_board);
    }

    private void toggleSlideMenu(){
        mSlideMenu.toggle();
    }
    @Override
    protected void onDestroy() {
//        SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("currentPosition", currentPosition);
//        editor.commit();
        unbindMusicService();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startMusicService(){
        Intent intent = new Intent();
        intent.setComponent(component);
        startService(intent);
    }
    private void bindMusicService(){
        //bind MusicService
        Intent intent = new Intent();
        intent.setComponent(component);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private void unbindMusicService(){
        unbindService(conn);
    }

	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicService = ((MusicServiceBinder)service).getService();
			musicService.setSongChangedListener(new OnSongChangedListener() {
				
				@Override
				public void onSongChanged(String title,String artist, int duration) {
                    tvSong.setText(title);
                    tvArtist.setText(artist);
                    SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
                    String str = sdf.format(duration);
                    tvDuration.setText(str);
                    mSeekBar.setMax(duration);
				}
			});
			musicService.setOnUpdatePlaytime(new OnUpdatePlaytime() {
				
				@Override
				public void updatePlaytime(int time) {
					SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
					String stime = sdf.format(time);
					tvPlaytime.setText(stime);
					mSeekBar.setProgress(time);
				}
			});
		}
	};


	private void setUp() {
		Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		cursor = getContentResolver().query(MUSIC_URL, mCursorCols,
				"duration > 60000", null, null);
		mPrevious = (ImageView) findViewById(R.id.previous);
		mPlay = (ImageView) findViewById(R.id.play);
		mNext = (ImageView) findViewById(R.id.next);
		mPrevious.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mNext.setOnClickListener(this);
		
		tvSong = (TextView)findViewById(R.id.song);
		tvArtist = (TextView)findViewById(R.id.artist);
		tvDuration = (TextView)findViewById(R.id.duration);

		audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		songList = (ListView) mSlideMenu.findViewById(R.id.list);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(MusicMassagerActivity.this, R.layout.musiclistview,cursor,
                new String[] { MediaStore.Audio.Media.TITLE },
                new int[] { R.id.musicid },0);
		songList.setAdapter(adapter);
		songList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mPlay.setImageResource(R.drawable.pause);
				musicService.playMusic(position);
				currentPosition = position;
			}			
		});
		
		icList = (ImageView)findViewById(R.id.song_list);
		icList.setOnClickListener(this);
		
		//get the last play song info, or display the 1th song info of datalist
		SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
		int lastPosition = preferences.getInt("currentPosition", -1);

		mSeekBar = (SeekBar)findViewById(R.id.progress_bar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				//play();

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
//				pause();

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
//				if (fromUser) {
//					seekbar_change(progress);
//				}

			}
		});
		tvPlaytime = (TextView)findViewById(R.id.playtime);
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mPrevious){
			mPlay.setImageResource(R.drawable.pause);
			playStatus = PLAYING;
			musicService.previous();
		}else if(v == mPlay && playStatus == PLAYING){
			mPlay.setImageResource(R.drawable.play);
			musicService.pause();
			playStatus = PAUSING;
		}else if(v == mPlay && playStatus != PLAYING){
			mPlay.setImageResource(R.drawable.pause);
			musicService.play();
			playStatus = PLAYING;
		}else if(v == mNext){
			mPlay.setImageResource(R.drawable.pause);
			playStatus = PLAYING;
			musicService.next();
		}else if(v == icList){
			toggleSlideMenu();
		}
	}
	
	private class MobliePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if(MusicService.isServiceRunning(MusicMassagerActivity.this,"com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService")
						&& playStatus == PAUSING
						&& isring){
					isring = false;
					MusicService.isPause = true;
					mPlay.setImageResource(R.drawable.play);
					Intent intent = new Intent(MusicService.PLAY_ACTION);
					intent.setComponent(component);
					startService(intent);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING:
				if(MusicService.isServiceRunning(MusicMassagerActivity.this,"com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService")
						&& playStatus == PLAYING){
					isring = true;
					MusicService.isPause = false;
					mPlay.setImageResource(R.drawable.pause);
					Intent intent = new Intent(MusicService.PAUSE_ACTION);
					intent.setComponent(component);
					startService(intent);
				}
				break;
			default:
				break;
			}
		}
	}
}
