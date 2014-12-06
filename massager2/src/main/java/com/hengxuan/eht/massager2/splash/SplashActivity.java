package com.hengxuan.eht.massager2.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout.LayoutParams;

import com.hengxuan.eht.massager2.MainActivity;
import com.hengxuan.eht.massager2.R;
import com.hengxuan.eht.massager2.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SplashActivity extends Activity {
    private AssetManager am;
    String[] images;
    String dir = "spash";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        final boolean guidFlag = getIntent().getBooleanExtra("flag", true);
		ViewPager vp = new ViewPager(this);
        am = getAssets();
        try {
            if(CommonUtil.getLocalLauguage(this) == 1) {
                images = am.list(dir);
            }else{
                dir = dir + "en";
                images = am.list(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        vp.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return images.length;
			}
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = getLayoutInflater().inflate(R.layout.activity_splash,null);
				ImageView iv = (ImageView) view.findViewById(R.id.image);
                try {
                    InputStream is = am.open(dir + File.separator + images[position]);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    iv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(position == (images.length - 1)){
                    ImageButton btn = (ImageButton) view.findViewById(R.id.btn);
                    btn.setVisibility(View.VISIBLE);
                    btn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    });
                }
				container.addView(view);
				return view;
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
				container.removeView((View)object);
//				super.destroyItem(container, position, object);
			}
		});
		vp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(vp);
	}
}
