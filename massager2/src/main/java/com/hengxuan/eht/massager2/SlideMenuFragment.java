package com.hengxuan.eht.massager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hengxuan.eht.massager2.user.LoginActivity;
import com.hengxuan.eht.massager2.user.User;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SlideMenuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SlideMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SlideMenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Activity myActivity;
    //用户名，未登录时可点击进入登录界面
    private TextView tvUserName;
    //头像
    private ImageView ivProfile;
    //分享菜单
    private View shareMenu;
    //关于菜单
    private View aboutMenu;
    //网上商城
    private View shopMenu;
    //设置按钮
    private TextView tvSettings;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SlideMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SlideMenuFragment newInstance(String param1, String param2) {
        SlideMenuFragment fragment = new SlideMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SlideMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot;
        viewRoot = inflater.inflate(R.layout.fragment_slide_menu, container, false);
        tvUserName = (TextView)viewRoot.findViewById(R.id.fragment_persion_button_login);
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!User.isLogin){
                    startActivity(new Intent(myActivity, LoginActivity.class));
                }else{
                    //TODO
                }
            }
        });
        if(User.isLogin){
            tvUserName.setText(User.userName);
        }
        shareMenu = viewRoot.findViewById(R.id.fragment_persion_share_rt);
        shareMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 分享
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources()
                        .getString(R.string.share));
                intent.putExtra(Intent.EXTRA_TEXT, getResources()
                        .getString(R.string.sharecontent));
                startActivity(Intent.createChooser(intent, getActivity().getTitle()));
            }
        });

        aboutMenu = viewRoot.findViewById(R.id.fragment_persion_about_rt);
        aboutMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        shopMenu = viewRoot.findViewById(R.id.fragment_center_consume_rt);
        shopMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://182.254.137.149/b2c/mall/index.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
        ivProfile = (ImageView) viewRoot.findViewById(R.id.fragment_persion_portrait_iw);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!User.isLogin){
                    startActivity(new Intent(myActivity, LoginActivity.class));
                }else{
                    //TODO
                }
            }
        });
        tvSettings = (TextView) viewRoot.findViewById(R.id.fragment_persion_setting_iw);
        tvSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        return viewRoot;
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
        myActivity = activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!User.isLogin){
            tvUserName.setText(R.string.login);
        }else{
            tvUserName.setText(User.userName);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
