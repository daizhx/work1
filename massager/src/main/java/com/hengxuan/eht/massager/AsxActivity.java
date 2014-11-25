package com.hengxuan.eht.massager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ListView;

import com.hengxuan.eht.utils.CommonUtil;

/**
 * Created by Administrator on 2014/9/16.
 */
public class AsxActivity extends BaseActivity{
    private ListView list;
    public int listWidth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        WebView webView = new WebView(this);
//        WebSettings webSettings = webView.getSettings();
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.loadUrl("file:///android_asset/asx.html");
//        setContentView(webView, new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        setContentView(R.layout.image_list);
        list = (ListView)findViewById(R.id.list);
        ViewTreeObserver vto = list.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean hasMeasured = false;
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                if(hasMeasured == false){
                    listWidth = list.getMeasuredWidth();
                    if(listWidth != 0){
                        hasMeasured = true;
                        if(CommonUtil.getLocalLauguage(AsxActivity.this) == 1) {
                            list.setAdapter(new ImageListAdapter(AsxActivity.this, listWidth, "asx"));
                        }else{
                            list.setAdapter(new ImageListAdapter(AsxActivity.this, listWidth, "asxen"));
                        }
                        if(Build.VERSION.SDK_INT >= 16) {
                            list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                }
            }
        });

    }
}
