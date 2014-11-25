package com.hengxuan.eht.massager;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;


public class MediaControllerActivity extends Activity {
    private VideoView vv;
    private MediaController mMediaController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_controller);
        String resId = getIntent().getStringExtra("video");
        vv = (VideoView)findViewById(R.id.video_view);
        mMediaController = new MediaController(this);
        mMediaController.setMediaPlayer(vv);
        vv.setMediaController(mMediaController);
        if(resId != null){
//            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
            String path = "http://182.254.137.149/video/"+resId;
            Uri uri1 = Uri.parse(path);
            vv.setVideoURI(uri1);
            vv.start();
            vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.media_controller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
