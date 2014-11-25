package com.hengxuan.eht.lens;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengxuan.eht.lens.Utils.FileUtils;
import com.hengxuan.eht.lens.Utils.MyAsynImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GalleryActivity extends ActionBarActivity {
    private GridView mGridView;
    private List imageList = new ArrayList();
    private static final String IMAGE_DIR = "dxlphoto";
    MyAsynImageLoader myAsynImageLoader;
    private static final int GRID_HEIGHT = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(getLayoutInflater().inflate(R.layout.action_bar, null));
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(R.string.title_activity_gallery);
        ImageView leftIcon = (ImageView) findViewById(R.id.left_icon);
        leftIcon.setImageResource(R.drawable.up_btn);
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mGridView = (GridView) findViewById(R.id.grid_view);
        mGridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return imageList.size();
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
                if(view == null){
                    view = new ImageView(GalleryActivity.this);
                }
                view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,GRID_HEIGHT));
                ((ImageView)view).setScaleType(ImageView.ScaleType.FIT_XY);
                myAsynImageLoader.loadBitmap((String)imageList.get(i),(ImageView)view,GRID_HEIGHT,GRID_HEIGHT);
                return view;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.fromFile(new File(imageList.get(i).toString()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
            }
        });
        if(FileUtils.isFileExist(this, IMAGE_DIR)){
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File dir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + IMAGE_DIR);
                File[] files = dir.listFiles();
                for(File file:files){
                    imageList.add(file.toString());
                }
            }else{
                File dir = new File(getFilesDir().toString() + File.separator + IMAGE_DIR);
                File[] files = dir.listFiles();
                for(File file:files){
                    imageList.add(file.toString());
                }
            }
        }
        myAsynImageLoader = new MyAsynImageLoader(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
