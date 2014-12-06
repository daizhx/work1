package com.hengxuan.eht.massager2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.hengxuan.eht.massager2.logger.Log;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;


public class MusicMassageActivity extends MyAciontBarActivity implements View.OnClickListener{
    private ImageView mPrevious;
    private ImageView mPlay;
    private ImageView mNext;
    private TextView tvPlaytime;
    private TextView tvDuration;
    private SeekBar mSeekBar;

    private MusicService musicService;
    private ComponentName component;

    private static final int PLAYING = 1;
    private static final int PAUSING = 0;
    private int playStatus = PAUSING;


    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicServiceBinder)service).getService();
            musicService.setSongChangedListener(new MusicService.OnSongChangedListener() {

                @Override
                public void onSongChanged(int position, int duration) {
//                    HashMap<String, String> infomap = datalist.get(position);
//                    String musicname = infomap.get(getResources().getString(R.string.musicname));
//					tvSong.setText(musicname);
//                    String artist = infomap.get(getResources().getString(R.string.musicartist));
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
                    stopService(new Intent(MusicMassageActivity.this, MusicService.class));
                    finish();
//                    musicService.stopSelf();
                }
            });
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_massage);
        mPrevious = (ImageView) findViewById(R.id.previous);
        mPlay = (ImageView) findViewById(R.id.play);
        mNext = (ImageView) findViewById(R.id.next);
        mPrevious.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mNext.setOnClickListener(this);
        tvDuration = (TextView)findViewById(R.id.duration);

        if (!MusicService.isServiceRunning(MusicMassageActivity.this)) {
            try {
                BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_8);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        component = new ComponentName(this, MusicService.class);
        //bind MusicService
        Intent intent = new Intent();
        intent.setComponent(component);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onClick(View v) {
        if(v == mPrevious){
            musicService.previous();
            mPlay.setImageResource(R.drawable.ic_music_pause);
            if(playStatus != PLAYING) {
                playStatus = PLAYING;
            }
        }else if(v == mPlay && playStatus == PLAYING){
            musicService.pause();
            mPlay.setImageResource(R.drawable.ic_play);
            playStatus = PAUSING;
        }else if(v == mPlay && playStatus != PLAYING){
            musicService.play();
            mPlay.setImageResource(R.drawable.ic_music_pause);
            if(playStatus != PLAYING) {
                playStatus = PLAYING;
            }
        }else if(v == mNext){
            musicService.next();
            mPlay.setImageResource(R.drawable.ic_music_pause);
            if(playStatus != PLAYING) {
                playStatus = PLAYING;
            }
        }
    }
}
