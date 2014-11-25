package com.hengxuan.eht.massager;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;

import com.hengxuan.eht.utils.CommonUtil;

/**
 * Created by Administrator on 2014/9/12.
 */
public class TreatmentToolsFragment extends Fragment {
    private ListView list;
    private int listWidth;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;
        rootView = inflater.inflate(R.layout.image_list,container,false);
        list = (ListView)rootView.findViewById(R.id.list);
        final ViewTreeObserver vto = list.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean hasMeasured = false;
            @Override
            public void onGlobalLayout() {
                if (hasMeasured == false) {
                    listWidth = list.getMeasuredWidth();
                    if (listWidth != 0) {
                        hasMeasured = true;
                        if(Build.VERSION.SDK_INT >= 16) {
                            list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        if(CommonUtil.getLocalLauguage(getActivity()) == 1) {
                            list.setAdapter(new ImageListAdapter(getActivity(), listWidth, "tool"));
                        }else{
                            list.setAdapter(new ImageListAdapter(getActivity(), listWidth, "toolen"));
                        }
                    }
                }
            }
        });
        return  rootView;
    }
}
