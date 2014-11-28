package com.hengxuan.eht.massager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.utils.CommonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2014/9/12.
 */
public class PointLocationFragment extends Fragment {

    private ListView list;
    public int listWidth;
    //病症Id
    private int id;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = null;
        v = inflater.inflate(R.layout.fragment_point_location,container, false);
        list = (ListView) v.findViewById(R.id.list);
        ViewTreeObserver vto = list.getViewTreeObserver();
//		vto.addOnPreDrawListener(new OnPreDrawListener() {
//			boolean hasMeasured = false;
//			@Override
//			public boolean onPreDraw() {
//				// TODO Auto-generated method stub
//				if(hasMeasured == false){
//					listWidth = list.getMeasuredWidth();
//					Log.d("daizhx", "listWidth 22222="+listWidth);
//					if(listWidth != 0){
//						hasMeasured = true;
//					}
//				}
//				return true;
//			}
//		});
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean hasMeasured = false;
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                if(hasMeasured == false){
                    listWidth = list.getMeasuredWidth();
                    if(listWidth != 0){
                        if(Build.VERSION.SDK_INT >= 16) {
                            list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        if(CommonUtil.getLocalLauguage(getActivity()) == 1){
                            list.setAdapter(new ImageListAdapter(getActivity(),listWidth,"xw" + id));
                        }else if(CommonUtil.getLocalLauguage(getActivity()) == 2){
                            list.setAdapter(new ImageListAdapter(getActivity(),listWidth,"xwtw" + id));
                        }else{
                            list.setAdapter(new ImageListAdapter(getActivity(),listWidth,"xwen" + id));
                        }
                        hasMeasured = true;
                    }
                }
            }
        });
        Bundle b = getArguments();
        id = b.getInt(TreatmentActivity.DISEASE_ID);
        id += 1;
        return v;
    }

}
