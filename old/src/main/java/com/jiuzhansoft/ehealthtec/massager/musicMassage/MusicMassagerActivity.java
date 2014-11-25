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
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.bluetooth.BluetoothServiceProxy;
import com.jiuzhansoft.ehealthtec.massager.MassagerActivity;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService.MusicServiceBinder;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService.OnSongChangedListener;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService.OnUpdatePlaytime;
import com.jiuzhansoft.ehealthtec.user.UserLoginActivity;


public class MusicMassagerActivity extends SlidingActivity implements
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
	private String[] musicTitle;
	private String[] musicArtist;
	private ListView songList;
	private List<HashMap<String, String>> datalist;
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
	
	private ImageView btIndicator;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> mBTDevices = new ArrayList<BluetoothDevice>();
    private List<BluetoothDevice> mTargetDevices = new ArrayList<BluetoothDevice>();

    private static final String TARGET_DEVICE_NAME1 = "Ehealthtec";
    private static final String TARGET_DEVICE_NAME2 = "EHT";

    private BroadcastReceiver mBTBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("BlueTooth","action="+action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BlueTooth","find device="+device.getName());
                mBTDevices.add(device);
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED
                    .equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(
                                MusicMassagerActivity.this,
                                getString(R.string.pairing),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Toast.makeText(
                                MusicMassagerActivity.this,
                                getString(R.string.paired),
                                Toast.LENGTH_SHORT).show();
                        String name = device.getName();
                        if(name.equals(TARGET_DEVICE_NAME1) || name.equals(TARGET_DEVICE_NAME2)) {
                            connectBluetooth(device);
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Toast.makeText(
                                MusicMassagerActivity.this,
                                getString(R.string.cancelpair),
                                Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            } else if (action
                    .equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                BluetoothDevice btDevice = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String strPsw = "1234";
                try {
                    setPin(btDevice.getClass(), btDevice, strPsw);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                if(mBTDevices.isEmpty()){
                    btIndicatorOff();
                    Toast.makeText(MusicMassagerActivity.this, getString(R.string.no_device_found), Toast.LENGTH_SHORT).show();
                }else{
                    for(BluetoothDevice device : mBTDevices){
                        String name = device.getName();
                        if(name.equals(TARGET_DEVICE_NAME1) || name.equals(TARGET_DEVICE_NAME2)){
                            mTargetDevices.add(device);
                        }
                    }
                    if(mTargetDevices.isEmpty()){
                        btIndicatorOff();
                        Toast.makeText(MusicMassagerActivity.this,getString(R.string.not_open_device),Toast.LENGTH_LONG).show();
                    }else if(mTargetDevices.size() == 1){
                        //found one device,connect it
                        BluetoothDevice device = mTargetDevices.get(0);
                        if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                            try {
                                Method createBondMethod = BluetoothDevice.class
                                        .getMethod("createBond");
                                Toast.makeText(
                                        MusicMassagerActivity.this,
                                        getString(R.string.startpair),
                                        Toast.LENGTH_SHORT).show();
                                createBondMethod.invoke(device);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }else{
                            //if paired
                            connectBluetooth(device);
                        }
                    }else{
                        //one more target device opened
                        manualConnect();
                    }

                }
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                if(state == BluetoothAdapter.STATE_ON){
                    btIndicatorTwinkle();
                    scanBTDevices();
                }

            }

        }
    };

    private void manualConnect() {
        //TODO
       AlertDialog alertDialog = new AlertDialog.Builder(MusicMassagerActivity.this).create();
        alertDialog.setMessage(getString(R.string.select_device));
        ListView listView = alertDialog.getListView();
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mTargetDevices.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View v = getLayoutInflater().inflate(android.R.layout.simple_list_item_2,null,false);
                TextView name = (TextView) v.findViewById(android.R.id.text1);
                TextView addr = (TextView) v.findViewById(android.R.id.text2);
                name.setText(mTargetDevices.get(i).getName());
                addr.setText(mTargetDevices.get(i).getAddress());
                return v;
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice device = mTargetDevices.get(i);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    connectBluetooth(device);
                }else{
                    try {
                        Method createBondMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        Toast.makeText(
                                MusicMassagerActivity.this,
                                getString(R.string.startpair),
                                Toast.LENGTH_SHORT).show();
                        createBondMethod.invoke(device);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                btIndicatorOff();
            }
        });
    }

    private ConnectThread connectThread;

    private void btIndicatorOn(){
        btIndicator.setImageResource(R.drawable.bt_on);
        btIndicator.setEnabled(false);
    }

    private void btIndicatorOff(){
        btIndicator.setImageResource(R.drawable.bt_off);
        btIndicator.setEnabled(true);
    }

    private void btIndicatorTwinkle(){
        btIndicator.setImageResource(R.drawable.bt_connectting_indicate);
        AnimationDrawable animationDrawable = (AnimationDrawable)btIndicator.getDrawable();
        animationDrawable.start();
        btIndicator.setEnabled(false);
    }

    private void scanBTDevices() {
        if(!mBluetoothAdapter.isDiscovering()) {
            btIndicatorTwinkle();
            mBluetoothAdapter.startDiscovery();
        }
    }


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		component = new ComponentName(this, MusicService.class);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mDisplayWidth = displayMetrics.widthPixels;
		setContentView(R.layout.actvity_music_massage);
		setBehindContentView(R.layout.song_list_board);
		mSlideMenu = getSlidingMenu();
		mSlideMenu.setBehindOffset(mDisplayWidth / 4);
		mSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		mSlideMenu.setMode(SlidingMenu.RIGHT);
		setUp();

        if (!MusicService.isServiceRunning(MusicMassagerActivity.this,"com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicService")) {
            bindMusicService();
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }else{
            scanBTDevices();
        }
        registerBTBroadcastReceiver();
    }
    @Override
    protected void onDestroy() {
        SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("currentPosition", currentPosition);
        editor.commit();
        unbindMusicService();
        unregisterBIBroadcastReceiver();
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
				public void onSongChanged(int position, int duration) {
					HashMap<String, String> infomap = datalist.get(position);
					String musicname = infomap.get(getResources().getString(R.string.musicname));
					tvSong.setText(musicname);
					String artist = infomap.get(getResources().getString(R.string.musicartist));
					tvArtist.setText(artist);
					SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
					String time = sdf.format(duration);
					tvDuration.setText(time);
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

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(view, lp);
		ImageView leftIcon = (ImageView)view.findViewById(R.id.left_icon);
		leftIcon.setImageResource(R.drawable.ic_action_back);
		leftIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		((TextView)view.findViewById(R.id.title)).setText(R.string.music_massage);
		btIndicator = (ImageView)view.findViewById(R.id.right_icon);
        btIndicator.setImageResource(R.drawable.bt_off);
		btIndicator.setVisibility(View.VISIBLE);
        btIndicator.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBTDevices();
            }
        });
	}

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

		musicTitle = new String[cursor.getCount()];
		musicArtist = new String[cursor.getCount()];

		songList = (ListView) findViewById(R.id.list);
		datalist = getInfoArray();
		SimpleAdapter adapter = new SimpleAdapter(MusicMassagerActivity.this,
				datalist, R.layout.musiclistview, new String[] { getResources()
						.getString(R.string.musicname) },
				new int[] { R.id.musicid });
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
		if(!datalist.isEmpty()){
			if(lastPosition == -1){
				HashMap<String, String> infomap = datalist.get(0);
				String musicname = infomap.get(getResources().getString(R.string.musicname));
				tvSong.setText(musicname);
				String artist = infomap.get(getResources().getString(R.string.musicartist));
				tvArtist.setText(artist);
			}else{
				HashMap<String, String> infomap = datalist.get(lastPosition);
				String musicname = infomap.get(getResources().getString(R.string.musicname));
				tvSong.setText(musicname);
				String artist = infomap.get(getResources().getString(R.string.musicartist));
				tvArtist.setText(artist);
			}
		}
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
		// TODO Auto-generated method stub
		if(v == mPrevious){
			mPlay.setImageResource(R.drawable.pause);
			playStatus = PLAYING;
//			Intent intent = new Intent(MusicService.PREVIOUS_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.previous();
		}else if(v == mPlay && playStatus == PLAYING){
			mPlay.setImageResource(R.drawable.play);
//			Intent intent = new Intent(MusicService.PLAY_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.pause();
			playStatus = PAUSING;
		}else if(v == mPlay && playStatus != PLAYING){
			mPlay.setImageResource(R.drawable.pause);
//			Intent intent = new Intent(MusicService.PAUSE_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.play();
			playStatus = PLAYING;
		}else if(v == mNext){
			mPlay.setImageResource(R.drawable.pause);
			playStatus = PLAYING;
//			Intent intent = new Intent(MusicService.NEXT_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.next();
		}else if(v == icList){
			showMenu();
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
					MusicService.flag = true;
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

    private void registerBTBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTBroadcastReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBTBroadcastReceiver, filter);
        filter = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        registerReceiver(mBTBroadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBTBroadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBTBroadcastReceiver, filter);
    }
    private void unregisterBIBroadcastReceiver(){
        unregisterReceiver(mBTBroadcastReceiver);
    }

    private void connectBluetooth(BluetoothDevice device){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            if (BluetoothServiceProxy.btSocket != null) {
                if (BluetoothServiceProxy.outStream != null) {
                    try {
                        BluetoothServiceProxy.outStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    BluetoothServiceProxy.btSocket.close();
                    BluetoothServiceProxy.btSocket = null;
                    BluetoothServiceProxy.mac = null;
                    BluetoothServiceProxy.name = null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            BluetoothServiceProxy.btSocket = (BluetoothSocket) m.invoke(device, 1);

            connectThread = new ConnectThread(device,BluetoothServiceProxy.btSocket);
            connectThread.start();
            return;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        manualConnect();
    }

    private class ConnectThread extends Thread{
        BluetoothDevice currentdevice;
        BluetoothSocket btSocket;
        public ConnectThread(BluetoothDevice device,BluetoothSocket s) {
            currentdevice = device;
            btSocket = s;
        }
        @Override
        public void run() {
            try {
                btSocket.connect();
                BluetoothServiceProxy.name = currentdevice.getName();
                BluetoothServiceProxy.mac = currentdevice.getAddress();
                //set the rightIcon image
                Message msg = Message.obtain();
                msg.what = 1;
                connectHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    BluetoothServiceProxy.btSocket.close();
                    BluetoothServiceProxy.btSocket = null;

                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = 0;
                connectHandler.sendMessage(msg);
            }
        }
    }

    private static final int CONNECT_SUCCESS = 1;
    private static final int CONNECT_FAIL = 0;
    private Handler connectHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code){
                case CONNECT_SUCCESS:
                    btIndicatorOn();
                    try {
                        BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_8);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CONNECT_FAIL:
                    btIndicatorOff();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str)
            throws Exception {

        try {

            Method removeBondMethod = btClass.getDeclaredMethod("setPin",

                    new Class[] { byte[].class });

            Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice,

                    new Object[] { str.getBytes() });

            Log.e("returnValue", "" + returnValue);

        } catch (SecurityException e) {

            e.printStackTrace();

        } catch (IllegalArgumentException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        }

        return true;

    }
}
