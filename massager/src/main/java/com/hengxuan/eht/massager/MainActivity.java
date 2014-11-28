package com.hengxuan.eht.massager;

import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.update.UpdateManager;
import com.hengxuan.eht.user.User;
import com.hengxuan.eht.utils.CommonUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;


public class MainActivity extends BaseActivity implements View.OnClickListener,SlideMenuFragment.OnFragmentInteractionListener, ViewPager.OnPageChangeListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private int currentPagerIndex;

    SlidingMenu menu;
    private SlideMenuFragment mPersionCenterFragment;
    public int viewPagerWidth;
    public int viewPagerHeight;

    private ImageView ivPlay;
    //左上上角文字
    private TextView text1;
    private TextView text2;

//    private View tvIndicator;

    private int displayWidth;
    private int displayHeight;

    private int actionBarHeight;
    private int statusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
        initSlideMenu();
        actionBarLeftIcon.setImageResource(R.drawable.actionbar_person_center);
        actionBarLeftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSlideMenu();
            }
        });

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text1.setText(getText(titleIds[0]).toString()+ "·" +getText(R.string.massage_therapy));
        text2.setText(getText(R.string.prefix).toString()+getText(titleIds[0]).toString());
//        tvIndicator = findViewById(R.id.tv_index);
        /*
        tvIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView listView = (ListView) getLayoutInflater().inflate(R.layout.tab_menu_list, null);
                String[] ss = new String[titleIds.length];
                for(int i=0;i<titleIds.length;i++){
                    ss[i] = getString(titleIds[i]);
                }
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, ss));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mViewPager.setCurrentItem(i);
                    }
                });
                PopupWindow popupWindow = new PopupWindow(listView,displayWidth/3, FrameLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(view);
            }
        });
        */

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        ViewTreeObserver vto = mViewPager.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw() {
//                int w = mViewPager.getMeasuredWidth();
//                int h = mViewPager.getMeasuredHeight();
//                int w1 = mViewPager.getWidth();
//                int h1 = mViewPager.getHeight();
//                Log.d("daizhx", "viewpager onPreDraw:"+w+"x"+h+",,"+w1 + "x" +h1);
                mViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
//                int w = mViewPager.getMeasuredWidth();
//                int h = mViewPager.getMeasuredHeight();
                viewPagerWidth = mViewPager.getWidth();
                viewPagerHeight = mViewPager.getHeight();
//                Log.d("daizhx", "viewpager onGlobalLayout:"+w+"x"+h+",,"+w1 + "x" +h1);
                if(Build.VERSION.SDK_INT >= 16) {
                    mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else {
                    mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

            }
        });
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabPageIndicator pageIndicator = (TabPageIndicator)findViewById(R.id.indicator);
        pageIndicator.setViewPager(mViewPager);
        pageIndicator.setOnPageChangeListener(this);

        ImageButton iv1 = (ImageButton)findViewById(R.id.fragment_mainnavigation_details_tv);
        if(CommonUtil.getLocalLauguage(this) != 1 && CommonUtil.getLocalLauguage(this) != 2){
            iv1.setImageResource(R.drawable.treat_label_en);
        }
        iv1.setOnClickListener(this);
        TextView tv1 = (TextView)findViewById(R.id.fragment_mainnavigation_therapy_tv);
        tv1.setOnClickListener(this);
        TextView tv2 = (TextView)findViewById(R.id.fragment_mainnavigation_treament_tv);
        tv2.setOnClickListener(this);

        if (savedInstanceState == null) {
            mPersionCenterFragment = new SlideMenuFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, mPersionCenterFragment).commitAllowingStateLoss();
        } else {
            mPersionCenterFragment = (SlideMenuFragment) getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }

        ivPlay = (ImageView)findViewById(R.id.play_btn);
        if(!getResources().getBoolean(R.bool.hasVideo)){
            ivPlay.setVisibility(View.GONE);
        }else {
            ivPlay.setOnClickListener(this);
        }
        //检查更新--需间隔24小时以上
//        SharedPreferences sp = getSharedPreferences(Constants.SP_FILE, Context.MODE_PRIVATE);
//        long lastTime = sp.getLong("lastCheckTime", 0);
//        if((System.currentTimeMillis() - lastTime) > Constants.CHECK_UPDATE_INTERVAl) {
//            UpdateManager updateManager = UpdateManager.getUpdateManager(this);
//            try {
//                int versionCode = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
//                updateManager.checkUpdate(getPackageName(), ""+versionCode);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//            sp.edit().putLong("lastCheckTime", System.currentTimeMillis()).commit();
//        }
        UpdateManager updateManager = UpdateManager.getUpdateManager(this);
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
            updateManager.checkUpdate(getPackageName(), ""+versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setRightIcon(R.drawable.home_menu,new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ListView listView = (ListView) getLayoutInflater().inflate(R.layout.tab_menu_list, null);
                String[] ss = new String[]{
                    getString(R.string.disease),getString(R.string.therapy_method),getString(R.string.asx),getString(R.string.massage_therapy)
                };
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, ss));
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view2, int i, long l) {
                        switch (i){
                            case 0:
                                ListView listView2 = (ListView) getLayoutInflater().inflate(R.layout.tab_menu_list, null);
                                String[] ss = new String[titleIds.length];
                                for(int j=0;j<titleIds.length;j++){
                                    ss[j] = getString(titleIds[j]);
                                }
                                listView2.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, ss));
                                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view2, int i, long l) {
//                                        mViewPager.setCurrentItem(i);
                                        Intent intent = new Intent(MainActivity.this,TreatmentActivity.class);
//                                        intent.setAction("massager.intent.action.DESEASE");
                                        intent.putExtra(TreatmentActivity.DISEASE_ID, i);
                                        intent.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX,3);
                                        startActivity(intent);
                                    }
                                });
                                PopupWindow popupWindow = new PopupWindow(listView2,displayWidth/3, FrameLayout.LayoutParams.WRAP_CONTENT);
                                popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                                popupWindow.setOutsideTouchable(true);
                                popupWindow.setTouchable(true);
                                popupWindow.setFocusable(true);
//                                popupWindow.showAsDropDown(view);
                                popupWindow.showAtLocation(view,Gravity.RIGHT|Gravity.TOP,view2.getWidth(),actionBarHeight+statusBarHeight);
                                break;
                            case 1:
                                Intent intent = new Intent(MainActivity.this, TreatmentActivity.class);
                                intent.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX, 0);
                                startActivity(intent);
                                break;
                            case 2:
                                startActivity(new Intent(MainActivity.this, AsxActivity.class));
                                break;
                            case 3:
                                Intent intent2 = new Intent(MainActivity.this, TreatmentActivity.class);
                                intent2.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX, 2);
                                startActivity(intent2);
                                break;
                            default:
                                break;
                        }
                    }
                });
                PopupWindow popupWindow = new PopupWindow(listView,displayWidth/3, FrameLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(view);
                actionBarHeight = getActionBar().getHeight();
            }
        });
        statusBarHeight = getStatusBarHeight();
    }

    //在onCreate中获取status bar height
    public int getStatusBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0){
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * add a slidemenu and set params
     */
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
    //slidemnenu toggle
    private void toggleSlideMenu(){
        menu.toggle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(this, TreatmentActivity.class);
        int index = mViewPager.getCurrentItem();
        intent.putExtra(TreatmentActivity.DISEASE_ID, index);
        switch (id){
            case R.id.fragment_mainnavigation_treament_tv:
                intent.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX, 2);
                startActivity(intent);
                break;
            case R.id.fragment_mainnavigation_details_tv:
                intent.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX, 0);
                startActivity(intent);
                break;
            case R.id.fragment_mainnavigation_therapy_tv:
                //进入阿是穴界面
                startActivity(new Intent(this, AsxActivity.class));
                break;
            case R.id.play_btn:
                Intent playIntent = new Intent(this, MediaControllerActivity.class);
                String[] videos = new String[]{
                        "toutong.mp4","yaotong.mp4","jianzhouyan.mp4","fenshixingguanjieyan.mp4","fenshixingguanjieyan.mp4","fenshixingguanjieyan.mp4","fenshixingguanjieyan.mp4"
                };
                playIntent.putExtra("video", videos[index]);
                startActivity(playIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        text1.setText(getText(titleIds[i]).toString()+ "·" +getText(R.string.massage_therapy));
        text2.setText(getText(R.string.prefix).toString()+getText(titleIds[i]).toString());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }


    int[] titleIds = new int[]{
            R.string.desease_1,R.string.desease_2,R.string.desease_3,R.string.desease_4,R.string.desease_5,R.string.desease_6,R.string.desease_7
    };
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PagerFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return titleIds.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return getString(titleIds[position]);
        }
    }


    public static class PagerFragment extends Fragment{
        static int[] imagesIds = new int[]{
                R.raw.page1,R.raw.page2,R.raw.page3,R.raw.page4,R.raw.page5,R.raw.page6,R.raw.page7
        };
        //红点在图片中的坐标
//        static int[][] positions = new int[][]{
//                {518,528},{96,439},{305,631},{387,190},{463,181},{357,507},{403,730}
//        };
        static int[][] positions = new int[][]{
                {305,631},{96,439},{403,730},{518,528},{387,190},{463,181},{357,507}
        };
        static int picW = 610;
        static int picH = 836;

        private static final String ARG_SECTION_NUMBER = "section_number";
        public static PagerFragment newInstance(int sectionNumber){
            PagerFragment fragment = new PagerFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PagerFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_disease_pager,container,false);
            Bundle args = getArguments();
            final int i = args.getInt(PagerFragment.ARG_SECTION_NUMBER);
            ImageView iv = (ImageView)rootView.findViewById(R.id.image);
            iv.setImageResource(imagesIds[i-1]);

            final ImageView redDot = new ImageView(getActivity());
            redDot.setImageResource(R.drawable.red_dot_alert);
            redDot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction("massager.intent.action.DESEASE");
                    intent.putExtra(TreatmentActivity.DISEASE_ID, i-1);
                    intent.putExtra(TreatmentActivity.DEFAULT_PAGE_INDEX,3);
                    startActivity(intent);
                }
            });
            ((RelativeLayout)rootView).addView(redDot);
            AnimationDrawable animationDrawable = (AnimationDrawable)redDot.getDrawable();
            animationDrawable.start();
            final ViewTreeObserver vto = redDot.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int h = container.getHeight();
                    int w = container.getWidth();
                    int[] xy = positions[i-1];
                    float ratioX = (float)w/picW;
                    float ratioY = (float)h/picH;
                    redDot.setX(xy[0]*ratioX);
                    redDot.setY(xy[1]*ratioY);
                    redDot.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
            return  rootView;
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            Bundle b = getArguments();
            int i = b.getInt(ARG_SECTION_NUMBER);
            ((TextView)rootView.findViewById(R.id.section_label)).setText("page"+i);
            return rootView;
        }
    }

}
