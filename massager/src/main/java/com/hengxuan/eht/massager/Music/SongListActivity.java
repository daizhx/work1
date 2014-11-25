package com.hengxuan.eht.massager.Music;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.hengxuan.eht.massager.BaseActivity;
import com.hengxuan.eht.massager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2014/11/12.
 */
public class SongListActivity extends BaseActivity{
    private Cursor cursor;
    String[] mCursorCols = new String[] { "audio._id AS _id",
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION };
    private String[] musicTitle;
    private String[] musicArtist;
    //本地歌曲列表
    private ListView songList;
    private List<HashMap<String, String>> datalist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        cursor = getContentResolver().query(MUSIC_URL, mCursorCols,
                "duration > 60000", null, null);
        musicTitle = new String[cursor.getCount()];
        musicArtist = new String[cursor.getCount()];
//        datalist = getInfoArray();

       setContentView(R.layout.song_list);
        setActionBarTitle(R.string.local_music);
        songList = (ListView) findViewById(R.id.list);
        songList.setAdapter(new SimpleCursorAdapter(SongListActivity.this, R.layout.song_list_item,cursor,new String[]{MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.TITLE},new int[]{R.id.text,R.id.text2}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
//        songList.setAdapter(new SimpleAdapter(SongListActivity.this,datalist, R.layout.song_list_item,new String[] { getResources()
//                .getString(R.string.musicname)},new int[]{R.id.text}));
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setResult(i);
                finish();
            }
        });
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
        return aList;
    }
}
