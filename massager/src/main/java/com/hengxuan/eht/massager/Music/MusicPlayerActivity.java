package com.hengxuan.eht.massager.Music;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.hengxuan.eht.massager.BaseActivity;
import com.hengxuan.eht.massager.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 音乐按摩界面，音乐播放器+按摩
 */
public class MusicPlayerActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "music";
    private static final int PICK_SONG = 110;
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
	private String[] musicTitle;
	private String[] musicArtist;
	private ListView songList;
	private List<HashMap<String, String>> datalist;
	private int playStatus;
	private static final int PLAYING = 1;
	private static final int PAUSING = 0;
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
	//唱盘
	private ImageView ivDisk;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		initActionBar();
		if (!MusicService.isServiceRunning(MusicPlayerActivity.this)) {
			Log.e(TAG, "set music mode");
			try {
				BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_8);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		component = new ComponentName(this, MusicService.class);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mDisplayWidth = displayMetrics.widthPixels;
		setContentView(R.layout.actvity_music_player);
        setActionBarTitle(R.string.music_massager);
        setRightIcon(R.drawable.songs, new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MusicPlayerActivity.this, SongListActivity.class), PICK_SONG);
            }
        });
        //初始化view
		setUp();
		
		//bind MusicService
		Intent intent = new Intent();
		intent.setComponent(component);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_SONG && resultCode != RESULT_CANCELED){
            //resultCode为歌曲列表的位置
            musicService.playMusic(resultCode);
            currentPosition = resultCode;
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			musicService = ((MusicService.MusicServiceBinder)service).getService();
			musicService.setSongChangedListener(new MusicService.OnSongChangedListener() {
				
				@Override
				public void onSongChanged(int position, int duration) {
					// TODO Auto-generated method stub
					HashMap<String, String> infomap = datalist.get(position);
					String musicname = infomap.get(getResources().getString(R.string.musicname));
//					tvSong.setText(musicname);
					String artist = infomap.get(getResources().getString(R.string.musicartist));
//					tvArtist.setText(artist);
					SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
					String time = sdf.format(duration);
					tvDuration.setText(time);
					mSeekBar.setMax(duration);
					
				}
			});
			musicService.setOnUpdatePlaytime(new MusicService.OnUpdatePlaytime() {
				
				@Override
				public void updatePlaytime(int time) {
					// TODO Auto-generated method stub
					SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
					String stime = sdf.format(time);
					tvPlaytime.setText(stime);
					mSeekBar.setProgress(time);
				}
			});
            musicService.setOnBTDisconnectListener(new MusicService.OnBTDisconnectListener() {
                @Override
                public void onBTDisconnect() {
                    stopService(new Intent(MusicPlayerActivity.this, MusicService.class));
                    finish();
//                    musicService.stopSelf();
                }
            });
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//�ر�����  not require, it is binded
//		Intent intent = new Intent();
//		intent.setComponent(component);
//		stopService(intent);
		
		SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("currentPosition", currentPosition);
		editor.commit();
		
		unbindService(conn);
		super.onDestroy();
	}


	private void setUp() {
		// TODO Auto-generated method stub
		Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		cursor = getContentResolver().query(MUSIC_URL, mCursorCols,
				"duration > 60000", null, null);

		mPrevious = (ImageView) findViewById(R.id.previous);
		mPlay = (ImageView) findViewById(R.id.play);
		mNext = (ImageView) findViewById(R.id.next);
		mPrevious.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mNext.setOnClickListener(this);

		tvDuration = (TextView)findViewById(R.id.duration);

		audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		musicTitle = new String[cursor.getCount()];
		musicArtist = new String[cursor.getCount()];

//		songList = (ListView) findViewById(R.id.list);
		datalist = getInfoArray();
		SimpleAdapter adapter = new SimpleAdapter(MusicPlayerActivity.this,
				datalist, R.layout.musiclistview, new String[] { getResources()
						.getString(R.string.musicname) },
				new int[] { R.id.musicid });
//		songList.setAdapter(adapter);
//		songList.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				// TODO Auto-generated method stub
//				mPlay.setImageResource(R.drawable.pause);
//				musicService.playMusic(position);
//				currentPosition = position;
//			}
//		});
		icList = (ImageView)findViewById(R.id.song_list);
		icList.setOnClickListener(this);
		
		//get the last play song info, or display the 1th song info of datalist
		SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
		int lastPosition = preferences.getInt("currentPosition", -1);
		if(!datalist.isEmpty()){
			if(lastPosition == -1){
				HashMap<String, String> infomap = datalist.get(0);
				String musicname = infomap.get(getResources().getString(R.string.musicname));
//				tvSong.setText(musicname);
				String artist = infomap.get(getResources().getString(R.string.musicartist));
//				tvArtist.setText(artist);
			}else{
				HashMap<String, String> infomap = datalist.get(lastPosition);
//				String musicname = infomap.get(getResources().getString(R.string.musicname));
//				tvSong.setText(musicname);
//				String artist = infomap.get(getResources().getString(R.string.musicartist));
//				tvArtist.setText(artist);
			}
		}
		mSeekBar = (SeekBar)findViewById(R.id.seekBar);
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

        ivDisk = (ImageView) findViewById(R.id.disk);
	}

	private List<HashMap<String, String>> getInfoArray() {
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < cursor.getCount(); i++) {
			HashMap<String, String> infoMap = new HashMap<String, String>();
			cursor.moveToPosition(i);
			int titleColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int artistColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			musicArtist[i] = cursor.getString(artistColumn);
			musicTitle[i] = cursor.getString(titleColumn);
			infoMap.put(getResources().getString(R.string.musicname),
					musicTitle[i]);
			infoMap.put(getResources().getString(R.string.musicartist),
					musicArtist[i]);
			aList.add(infoMap);
		}
		Log.i("msg", aList + "");
		return aList;
	}

	@Override
	public void onClick(View v) {
		if(v == mPrevious){
            musicService.previous();
			mPlay.setImageResource(R.drawable.pause);
            if(playStatus != PLAYING) {
                playStatus = PLAYING;
                //唱盘切换至动画
                ivDisk.setImageResource(R.drawable.turn_round_disk);
                AnimationDrawable animationDrawable = (AnimationDrawable) ivDisk.getDrawable();
                animationDrawable.start();
            }
		}else if(v == mPlay && playStatus == PLAYING){
            musicService.pause();
			mPlay.setImageResource(R.drawable.play);
			playStatus = PAUSING;
            ivDisk.setImageResource(R.drawable.music_stop_anim);
		}else if(v == mPlay && playStatus != PLAYING){
            musicService.play();
			mPlay.setImageResource(R.drawable.pause);
            if(playStatus != PLAYING) {
                playStatus = PLAYING;
                //唱盘切换至动画
                ivDisk.setImageResource(R.drawable.turn_round_disk);
                AnimationDrawable animationDrawable = (AnimationDrawable) ivDisk.getDrawable();
                animationDrawable.start();
            }
		}else if(v == mNext){
            musicService.next();
			mPlay.setImageResource(R.drawable.pause);
            if(playStatus != PLAYING) {
                playStatus = PLAYING;
                //唱盘切换至动画
                ivDisk.setImageResource(R.drawable.turn_round_disk);
                AnimationDrawable animationDrawable = (AnimationDrawable) ivDisk.getDrawable();
                animationDrawable.start();
            }
		}else if(v == icList){
			//TODO 查看歌曲列表
		}
	}
	
	private class MobliePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if(MusicService.isServiceRunning(MusicPlayerActivity.this)
						&& playStatus == PAUSING
						&& isring){
					isring = false;
					MusicService.flag = true;
					mPlay.setImageResource(R.drawable.play);
					Intent intent = new Intent(MusicService.PLAY_ACTION);
					intent.setComponent(component);
					startService(intent);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
			case TelephonyManager.CALL_STATE_RINGING:
				if(MusicService.isServiceRunning(MusicPlayerActivity.this)
						&& playStatus == PLAYING){
					isring = true;
					MusicService.flag = false;
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
