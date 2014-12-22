package com.hengxuan.eht.massager2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.eht.bluetooth.BTBaseActivity;
import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.hengxuan.eht.massager2.logger.Log;
import com.viewpagerindicator.TabPageIndicator;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab2#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Tab2 extends Fragment implements BTBaseActivity.ConnectResultListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String[] mPageTitles;
    private String[] modes;
    private Drawable[] modeIcons;
    private static final int REQUEST_CONNECT = 110;
    private Intent servicIntent;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicServiceBinder)service).getService();
            musicService.setSongChangedListener(new MusicService.OnSongChangedListener() {

                @Override
                public void onSongChanged(String title,String artist, int duration) {
                    SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
                    String time = sdf.format(duration);
                    tvDuration.setText(time);
                    mSeekBar.setMax(duration);

                }
            });
            musicService.setOnUpdatePlaytime(new MusicService.OnUpdatePlaytime() {

                @Override
                public void updatePlaytime(int time) {
                    SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
                    String stime = sdf.format(time);
                    tvPlaytime.setText(stime);
                    mSeekBar.setProgress(time);
                }
            });
//            musicService.setOnBTDisconnectListener(new MusicService.OnBTDisconnectListener() {
//                @Override
//                public void onBTDisconnect() {
//                    ((MainActivity)getActivity()).btIndicatorOff();
//                }
//            });
        }
    };
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab2.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab2 newInstance(String param1, String param2) {
        Tab2 fragment = new Tab2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Tab2 newInstance(){
        Tab2 frgment = new Tab2();
        return frgment;
    }

    public Tab2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mPageTitles = new String[]{getString(R.string.massager),getString(R.string.music_massage)};
        modes = new String[]{getString(R.string.massage_machine_modle1),getString(R.string.massage_machine_modle2),getString(R.string.massage_machine_modle3)
                ,getString(R.string.massage_machine_modle4),getString(R.string.massage_machine_modle5)
                ,getString(R.string.massage_machine_modle6),getString(R.string.massage_machine_modle7),getString(R.string.massage_machine_modle8)};
        modeIcons = new Drawable[]{getResources().getDrawable(R.drawable.ic_mode1),getResources().getDrawable(R.drawable.ic_mode2),
                getResources().getDrawable(R.drawable.ic_mode3),getResources().getDrawable(R.drawable.ic_mode4),
                getResources().getDrawable(R.drawable.ic_mode5),getResources().getDrawable(R.drawable.ic_mode6),
                getResources().getDrawable(R.drawable.ic_mode7),getResources().getDrawable(R.drawable.ic_mode8)};

        servicIntent = new Intent(getActivity(), MusicService.class);
        getActivity().startService(servicIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter());
        TabPageIndicator pageIndicator = (TabPageIndicator)rootView.findViewById(R.id.indicator);
        pageIndicator.setViewPager(viewPager);
        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if(i == 1){
                    getActivity().bindService(servicIntent,conn,Context.BIND_AUTO_CREATE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnectResult(int i) {
        //TODO
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    class ViewPagerAdapter extends PagerAdapter {
        private GridView mGridView;
        private ListView mListView;
        int time;
        int strength;
        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles[position];
        }

        @Override
        public int getCount() {
            return mPageTitles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if(position == 0){
                View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_tab3,null);
                mGridView = (GridView) rootView.findViewById(R.id.mode_grid);
                mListView = (ListView)rootView.findViewById(R.id.setting_list);
                mGridView.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 8;
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
                    public View getView(int i, View convertView, ViewGroup viewGroup) {
                        TextView itemView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.grid_item,null);
                        itemView.setText(modes[i]);
                        itemView.setCompoundDrawablesWithIntrinsicBounds(null,modeIcons[i],null,null);
                        return itemView;
                    }
                });
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    short[] command = new short[]{BluetoothServiceProxy.MODE_TAG_3,BluetoothServiceProxy.MODE_TAG_4
                    ,BluetoothServiceProxy.MODE_TAG_2,BluetoothServiceProxy.MODE_TAG_1
                    ,BluetoothServiceProxy.MODE_TAG_5,BluetoothServiceProxy.MODE_TAG_6
                    ,BluetoothServiceProxy.MODE_TAG_7,BluetoothServiceProxy.MODE_TAG_8};
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(!BluetoothServiceProxy.isconnect()){
                            toConnectMassager(command[i]);
                            return;
                        }
                        boolean ret = BluetoothServiceProxy.sendCommandToDevice(command[i]);
                        if(ret){
                            view.setSelected(true);
                        }

                    }
                });
                mListView = (ListView) rootView.findViewById(R.id.setting_list);
                final TextView settingText = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.list_text_head,null);
                settingText.setText(R.string.settings);
                mListView.addHeaderView(settingText);
//        mListView.addHeaderView(settingText,null, false);
                mListView.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return 3;
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
                        switch (i){
                            case 0:
                                view = LayoutInflater.from(getActivity()).inflate(R.layout.set_time_bar, null);
                                SeekBar timerSeek = (SeekBar) view.findViewById(R.id.seek_bar);
                                timerSeek.setMax(12);
                                timerSeek.setProgress(6);
                                timerSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                        time = i;
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                        if(!BluetoothServiceProxy.isconnect()){
                                            toConnectMassager();
                                        }
                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        if(BluetoothServiceProxy.sendCommandToDevice((short) (BluetoothServiceProxy.TIME_TAG + time))){
                                            //TODO
                                        }
                                    }
                                });
                                break;
                            case 1:
                                view = LayoutInflater.from(getActivity()).inflate(R.layout.set_time_bar, null);
                                TextView textView = (TextView) view.findViewById(R.id.title);
                                textView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_strength),null,null,null);
                                textView.setText(R.string.strength);
                                TextView label2 = (TextView) view.findViewById(R.id.label2);
                                label2.setText("8");
                                TextView label3 = (TextView) view.findViewById(R.id.label3);
                                label3.setText("16");
                                SeekBar strengthSeek = (SeekBar) view.findViewById(R.id.seek_bar);
                                strengthSeek.setMax(16);
                                strengthSeek.setProgress(8);
                                strengthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                        strength = i;
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                        if(!BluetoothServiceProxy.isconnect()){
                                            toConnectMassager();
                                        }
                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                        if(!BluetoothServiceProxy.sendCommandToDevice((short) (BluetoothServiceProxy.STRENGTH_TAG + strength))){
                                            //TODO
                                        }
                                    }
                                });
                                break;
                            case 2:
                                view = LayoutInflater.from(getActivity()).inflate(R.layout.set_frequence_bar, null);
                                final ImageView setFrequence = (ImageView) view.findViewById(R.id.toggle_frequence);
                                setFrequence.setOnClickListener(new View.OnClickListener() {
                                    boolean b = false;
                                    @Override
                                    public void onClick(View view) {
                                        if(!BluetoothServiceProxy.isconnect()){
                                            toConnectMassager();
                                            return;
                                        }
                                        if(!b){
                                            setFrequence.setImageResource(R.drawable.high_frequence);
                                            b = true;
                                            if(!BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.H_FR_TAG)){
                                                //TODO
                                            }
                                        }else{
                                            setFrequence.setImageResource(R.drawable.low_frequence);
                                            b = false;
                                            if(!BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.L_FR_TAG)){
                                                //TODO
                                            }
                                        }
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                        return view;
                    }



                    @Override
                    public boolean isEnabled(int position) {
                        return false;
                    }
                });
                container.addView(rootView);
                return rootView;
            }else if(position == 1){
                View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_music_massage,null);
                mPrevious = (ImageView) rootView.findViewById(R.id.previous);
                mPlay = (ImageView) rootView.findViewById(R.id.play);
                mNext = (ImageView) rootView.findViewById(R.id.next);
                musicPlayerClickListener listener = new musicPlayerClickListener();
                mPrevious.setOnClickListener(listener);
                mPlay.setOnClickListener(listener);
                mNext.setOnClickListener(listener);
                tvDuration = (TextView)rootView.findViewById(R.id.duration);
                mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
                Log.d("daizhx", "aaaaaaaaaaaaaaaaaa-"+mSeekBar);
                tvPlaytime = (TextView) rootView.findViewById(R.id.playtime);
                container.addView(rootView);
                return rootView;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

    }

    private ImageView mPrevious;
    private ImageView mPlay;
    private ImageView mNext;
    private TextView tvPlaytime;
    private TextView tvDuration;
    private SeekBar mSeekBar;

    private MusicService musicService;

    private static final int PLAYING = 1;
    private static final int PAUSING = 0;
    private int playStatus = PAUSING;
    class musicPlayerClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(!BluetoothServiceProxy.isconnect()){
                startActivity(new Intent(getActivity(),BluetoothInterface.class));
                return;
            }

            if(v == mPrevious){
                musicService.previous();
                mPlay.setImageResource(R.drawable.ic_music_pause);
                if(playStatus != PLAYING) {
                    playStatus = PLAYING;
                }
            }else if(v == mPlay && playStatus == PLAYING){
                musicService.pause();
                mPlay.setImageResource(R.drawable.ic_play);
                playStatus = PAUSING;
            }else if(v == mPlay && playStatus != PLAYING){
                musicService.play();
                mPlay.setImageResource(R.drawable.ic_music_pause);
                if(playStatus != PLAYING) {
                    playStatus = PLAYING;
                }
            }else if(v == mNext){
                musicService.next();
                mPlay.setImageResource(R.drawable.ic_music_pause);
                if(playStatus != PLAYING) {
                    playStatus = PLAYING;
                }
            }
        }
    }
    public void waitForConnect(){
        //TODO
//        ProgressBar pb = (ProgressBar) maskLayer.findViewById(R.id.progress);
//        pb.setVisibility(View.VISIBLE);
    }

    private void toConnectMassager(short command){
        Intent intent = new Intent(getActivity(),BluetoothInterface.class);
        intent.putExtra("command", command);
        startActivityForResult(intent,REQUEST_CONNECT);
    }
    private void toConnectMassager(){
        Intent intent = new Intent(getActivity(),BluetoothInterface.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("daizhx", "requestcode="+requestCode+",resultCode="+resultCode+",data="+data);
        if(REQUEST_CONNECT == requestCode){
            if(Activity.RESULT_OK == resultCode) {
                ((MainActivity) getActivity()).btIndicatorOn();
            }
        }
    }

}
