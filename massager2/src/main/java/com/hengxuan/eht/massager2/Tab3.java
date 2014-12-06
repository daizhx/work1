package com.hengxuan.eht.massager2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.hengxuan.eht.bluetooth.BTBaseActivity;
import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab3#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Tab3 extends Fragment implements BTBaseActivity.ConnectResultListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private GridView mGridView;
    private ListView mListView;
    private String[] modes;
    private Drawable[] modeIcons;
    private RelativeLayout maskLayer;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab3.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab3 newInstance(String param1, String param2) {
        Tab3 fragment = new Tab3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Tab3 newInstance(){
        Tab3 frgment = new Tab3();
        return frgment;
    }
    public Tab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        modes = new String[]{getString(R.string.massage_machine_modle1),getString(R.string.massage_machine_modle2),getString(R.string.massage_machine_modle3)
                ,getString(R.string.massage_machine_modle4),getString(R.string.massage_machine_modle5)
                ,getString(R.string.massage_machine_modle6),getString(R.string.massage_machine_modle7),getString(R.string.massage_machine_modle8)};
        modeIcons = new Drawable[]{getResources().getDrawable(R.drawable.ic_mode1),getResources().getDrawable(R.drawable.ic_mode2),
                getResources().getDrawable(R.drawable.ic_mode3),getResources().getDrawable(R.drawable.ic_mode4),
                getResources().getDrawable(R.drawable.ic_mode5),getResources().getDrawable(R.drawable.ic_mode6),
                getResources().getDrawable(R.drawable.ic_mode7),getResources().getDrawable(R.drawable.ic_mode8)};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_tab3, container, false);
        maskLayer = (RelativeLayout) rootView.findViewById(R.id.mask_layer);
        maskLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if(BluetoothServiceProxy.isconnect()){
            maskLayer.setVisibility(View.GONE);
        }
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
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int p = mGridView.getCheckedItemPosition();
                view.setSelected(true);
                boolean ret = true;
                switch (i){
                    case 0:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_3);
                        break;
                    case 1:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_4);
                        break;
                    case 2:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_2);
                        break;
                    case 3:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_1);
                        break;
                    case 4:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_5);
                        break;
                    case 5:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_6);
                        break;
                    case 6:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_7);
                        break;
                    case 7:
                        ret = BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_8);
                        startActivity(new Intent(getActivity(),MusicMassageActivity.class));
                        break;
                    default:
                        break;
                }
                if(!ret){
                    sendCommandFail();
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
                                if(BluetoothServiceProxy.sendCommandToDevice((short) (BluetoothServiceProxy.TIME_TAG + i))){
                                    sendCommandFail();
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                //TODO
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                //TODO
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
                                if(!BluetoothServiceProxy.sendCommandToDevice((short) (BluetoothServiceProxy.STRENGTH_TAG + i))){
                                    sendCommandFail();
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                //TODO
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                //TODO
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
                                if(!b){
                                    setFrequence.setImageResource(R.drawable.high_frequence);
                                    b = true;
                                    if(!BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.H_FR_TAG)){
                                        sendCommandFail();
                                    }
                                }else{
                                    setFrequence.setImageResource(R.drawable.low_frequence);
                                    b = false;
                                    if(!BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.L_FR_TAG)){
                                        sendCommandFail();
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
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BluetoothServiceProxy.isconnect()){
            maskLayer.setVisibility(View.GONE);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void sendCommandFail() {
        //TODO
        maskLayer.setVisibility(View.VISIBLE);
        ((BTBaseActivity)getActivity()).btIndicatorOff();
    }

    public void waitForConnect(){
        ProgressBar pb = (ProgressBar) maskLayer.findViewById(R.id.progress);
        pb.setVisibility(View.VISIBLE);
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
        if(i == BTBaseActivity.CONNECT_SUCCESS){
            maskLayer.setVisibility(View.GONE);
        }else{
            maskLayer.findViewById(R.id.progress).setVisibility(View.GONE);
        }
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

}
