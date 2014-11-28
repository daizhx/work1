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

import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.utils.CommonUtil;

/**
 * Created by Administrator on 2014/9/12.
 */
public class KnowledgeFragment extends Fragment {
    //对应不同的疾病显示不同的内容
    private int index;
    private ListView list;
    private int listWidth;
    //文件夹名
    private String file = null;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = null;
        viewRoot = inflater.inflate(R.layout.fragment_knowledge, container, false);
        list = (ListView)viewRoot.findViewById(R.id.list);
        index = getArguments().getInt(TreatmentActivity.DISEASE_ID);

        switch (index){
            case 0:
                file = "jzb";
                break;
            case 1:
                file = "jzy";
                break;
            case 2:
                file = "yt";
                break;
            case 3:
                file = "fsxgjy";
                break;
            case 4:
                file = "tf";
                break;
            case 5:
                file = "tt";
                break;
            case 6:
                file = "yzjptc";
                break;
        }

        ViewTreeObserver vto = list.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean hasMeasured = false;
            @Override
            public void onGlobalLayout() {
                if(!hasMeasured){
                    listWidth = list.getMeasuredWidth();
                    if(listWidth != 0){
                        if(Build.VERSION.SDK_INT >= 16) {
                            list.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }else {
                            list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        hasMeasured = true;
                        if(CommonUtil.getLocalLauguage(getActivity()) == 1) {
                            list.setAdapter(new ImageListAdapter(getActivity(), listWidth, file));
                        }else if(CommonUtil.getLocalLauguage(getActivity()) == 2){
                            list.setAdapter(new ImageListAdapter(getActivity(), listWidth, file+"tw"));
                        }else{
                            list.setAdapter(new ImageListAdapter(getActivity(), listWidth, file+"en"));
                        }
                    }
                }
            }
        });
        return viewRoot;
    }
}
