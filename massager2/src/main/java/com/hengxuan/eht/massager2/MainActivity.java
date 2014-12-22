package com.hengxuan.eht.massager2;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class MainActivity extends MyAciontBarActivity implements Tab1.OnFragmentInteractionListener, Tab2.OnFragmentInteractionListener,
        Tab3.OnFragmentInteractionListener, Tab4.OnFragmentInteractionListener, SlideMenuFragment.OnFragmentInteractionListener{
    private Tab1 mTab1;
    private Tab1 mTab2;
    private Tab2 mTab3;
    private Tab1 mTab4;
    private SlideMenuFragment mPersionCenterFragment;
    SlidingMenu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTab();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mTab3,"tab3")
                    .commit();
            ((RadioButton)findViewById(R.id.tab3)).setChecked(true);
            setConnectResultListener(mTab3);
        }
        RadioGroup menu = (RadioGroup) findViewById(R.id.bottom_menu);
        menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id){
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab1).commit();
                        break;
                    case R.id.tab2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab2).commit();
                        break;
                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab3,"tab3").commit();
                        break;
                    case R.id.tab4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mTab4).commit();
                        break;
                    default:
                        break;
                }
            }
        });

        initSlideMenu();
        if (savedInstanceState == null) {
            mPersionCenterFragment = new SlideMenuFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, mPersionCenterFragment).commitAllowingStateLoss();
        } else {
            mPersionCenterFragment = (SlideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }
        actionBarLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSlideMenu();
            }
        });

        actionBarRightIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                scanBTDevices();
//                Tab3 tab3 = (Tab3) getSupportFragmentManager().findFragmentByTag("tab3");
                mTab3.waitForConnect();
            }
        });
    }

    private void initSlideMenu(){
        // configure the SlidingMenu
        menu = new SlidingMenu(this);
        //		 menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        //		 menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
        //		 menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        //slide window or content
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu_frame);
        menu.setMode(SlidingMenu.LEFT);
        menu.setBehindScrollScale(0.333f);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        menu.setBehindWidth(display.getWidth() * 4 / 5);
        int width = (int) (75 * (float) menu.getWidth());
        menu.setShadowWidth(width);
    }

    private void toggleSlideMenu(){
        menu.toggle();
    }

    private void initTab() {
        mTab1 = Tab1.newInstance(2,new String[]{"asx", "tool"}, new String[]{getString(R.string.asx),getString(R.string.music_massager)});
        String[] dirs = new String[]{"jzb","jzy","yt","fsxgjy","tf","tt","yzjptc"};
        String[] deseases = new String[]{getString(R.string.desease_1),getString(R.string.desease_2),getString(R.string.desease_3),getString(R.string.desease_4),getString(R.string.desease_5),
                getString(R.string.desease_6),getString(R.string.desease_7)};
        mTab2 = Tab1.newInstance(7,dirs,deseases);
        mTab3 = Tab2.newInstance();
        mTab4 = Tab1.newInstance(7,new String[]{"xw1","xw2","xw3","xw4","xw5","xw6","xw7"},deseases);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
