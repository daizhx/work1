package com.hengxuan.eht.massager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.hengxuan.eht.bluetooth.BluetoothInterface;
import com.hengxuan.eht.bluetooth.BluetoothServiceProxy;
import com.hengxuan.eht.data.PrefsData;
import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.massager.Music.MusicPlayerActivity;
import com.hengxuan.eht.massager.MyView.CircleButtons;
import com.hengxuan.eht.massager.MyView.TouchTimeView;
import com.hengxuan.eht.user.LoginActivity;
import com.hengxuan.eht.user.User;
import com.hengxuan.eht.utils.CommonUtil;

import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2014/9/12.
 */
public class TreatmentFragment extends Fragment {
    int[] bgs = new int[]{
            R.drawable.hg1,
            R.drawable.tn1,
            R.drawable.cj,
            R.drawable.zj1,
            R.drawable.am11,
            R.drawable.gs1,
            R.drawable.zd2,
            R.drawable.yy1
    };
    int[] selectedBgs = new int[]{
            R.drawable.hg2,
            R.drawable.tn2,
            R.drawable.cj2,
            R.drawable.zj2,
            R.drawable.am12,
            R.drawable.gs2,
            R.drawable.zd1,
            R.drawable.yy2
    };
    //英文图标
    int[] bgs_en = new int[]{
            R.drawable.enhg1,
            R.drawable.entn1,
            R.drawable.encj,
            R.drawable.enzj1,
            R.drawable.enam11,
            R.drawable.engs1,
            R.drawable.enzd2,
            R.drawable.enyy1
    };
    int[] selectedBgs_en = new int[]{
            R.drawable.enhg2,
            R.drawable.entn2,
            R.drawable.encj2,
            R.drawable.enzj2,
            R.drawable.enam12,
            R.drawable.engs2,
            R.drawable.enzd1,
            R.drawable.enyy2
    };


    //圆圈按钮组
    private CircleButtons circleButtons;
    //调节时间控件
    private TouchTimeView timeView;
    private BaseActivity mActivity;

    //调节强度按钮
    private ImageView ivJianStrength;
    private ImageView ivJiaStrength;
    private TextView tvStrengthInd;
    private int currentStrength = 1;//1...16


    //中地频转换开关，默认是低频
    private ImageView ivSwitchFrequence;
    private int currentFrequency = 0;//low-0,mid-1

    //是否已验证
    private boolean isVerified;
    private View verifyView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = null;
        viewRoot = inflater.inflate(R.layout.fragment_treatment, container, false);
        circleButtons = (CircleButtons)viewRoot.findViewById(R.id.circle_btns);
        initCircleBtns(circleButtons);
        initTouchTimeView(viewRoot);
        initImageBtns(viewRoot);

        verifyView = viewRoot.findViewById(R.id.verify_prompt);
        SharedPreferences sp = getActivity().getSharedPreferences(PrefsData.CONFIG, Context.MODE_PRIVATE);
        isVerified = sp.getBoolean(PrefsData.CONFIG_ISVERIFIED, false);
        if(isVerified){
            verifyView.setVisibility(View.GONE);
        }else{
            final EditText edit = (EditText) verifyView.findViewById(R.id.edit);
            Button btn = (Button) verifyView.findViewById(R.id.btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = edit.getText().toString();
                    if(str.equals("") || str == null){
                        edit.setError(getResources().getString(R.string.input_product_number));
                    }else{
                        if(User.isLogin) {
                            VerifySerialNum(str);
                        }else{
                            new AlertDialog.Builder(getActivity()).setMessage(R.string.login_hint).setPositiveButton(R.string.confirm,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                }
                            }).setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();

                        }
                    }
                }
            });
        }
        return viewRoot;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 初始化频率，强度控制按钮
     */
    private void initImageBtns(View v) {
        ivJianStrength = (ImageView)v.findViewById(R.id.jian);
        ivJiaStrength = (ImageView)v.findViewById(R.id.jia);
        ivSwitchFrequence = (ImageView)v.findViewById(R.id.iv_freq);
        Listener listener = new Listener();
        ivJianStrength.setOnClickListener(listener);
        ivJiaStrength.setOnClickListener(listener);
        ivSwitchFrequence.setOnClickListener(listener);
        tvStrengthInd = (TextView)v.findViewById(R.id.tv_strength_ind);
    }

    class Listener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.jia:
                    if(currentStrength < 16) {
                        increaseStrength(1);
                    }
                    break;
                case R.id.jian:
                    if(currentStrength > 1) {
                        increaseStrength(-1);
                    }
                    break;
                case R.id.iv_freq:
                    if(currentFrequency == 0){
                        currentFrequency = 1;
                        ivSwitchFrequence.setImageResource(R.drawable.high_fre);
                    }else{
                        currentFrequency = 0;
                        ivSwitchFrequence.setImageResource(R.drawable.low_fre);
                    }
                    setFrequency(currentFrequency);
                    break;
            }
        }
    }


    /**
     * 调节强度
     */
    private void increaseStrength(int delta) {
        if(!BluetoothServiceProxy.isconnect()){
            startActivity(new Intent(getActivity(), BluetoothInterface.class));
            return;
        }
        int ind = currentStrength + delta;
        try {
            BluetoothServiceProxy.sendCommandToDevice((short)(BluetoothServiceProxy.STRENGTH_TAG + ind));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.disconnectbluetooth), Toast.LENGTH_SHORT).show();
            return;
        }
        //成功后重置currentStrength
        tvStrengthInd.setText(ind+"/16");
        currentStrength += delta;
    }

    /**
     * 调频率，向蓝牙设备发指令
     * @param frequency
     */
    private void setFrequency(int frequency) {
        short command;
        if(frequency == 1){
            command = BluetoothServiceProxy.H_FR_TAG;
        }else if(frequency == 0){
            command = BluetoothServiceProxy.L_FR_TAG;
        }else{
            //TODO 异常
            return;
        }
        try {
            BluetoothServiceProxy.sendCommandToDevice(command);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.disconnectbluetooth), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BaseActivity)activity;
    }

    private void initCircleBtns(final CircleButtons circleButtons) {
        if(CommonUtil.getLocalLauguage(getActivity()) == 1) {
            circleButtons.setButtonsBg(bgs);
            circleButtons.setSelectedBgs(selectedBgs);
        }else{
            circleButtons.setButtonsBg(bgs_en);
            circleButtons.setSelectedBgs(selectedBgs_en);
        }
        circleButtons.addButtons(true);
        circleButtons.setOnBtnsClickListener(new CircleButtons.onBtnsClickListener() {
            @Override
            public void onBtnsClick(int id) {
                if(!BluetoothServiceProxy.isconnect()){

                    Intent intent = new Intent(getActivity(), BluetoothInterface.class);
                    startActivityForResult(intent,id);
                    circleButtons.resetBtns();
                }else{
                    if(id == 7){
                     //音乐按摩
                     mActivity.startActivity(new Intent(mActivity, MusicPlayerActivity.class));
                     circleButtons.resetBtns();
                    }else {
                        try {
                            BluetoothServiceProxy.sendCommandToDevice(BluetoothServiceProxy.MODES[id]);
                        } catch (Exception e) {
                            circleButtons.resetBtns();
                            Log.e("blue send mode command fail!");
                        }
                    }
                }
            }
        });
    }

    /**
     * 从连接设备返回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == BluetoothInterface.RESULT_OK){
            circleButtons.setChecked(requestCode);
        }
    }

    private void initTouchTimeView(View root){
        FrameLayout container = (FrameLayout)root.findViewById(R.id.container);
        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                // super.handleMessage(msg);
                // if (null != timetv) {
                // timetv.setText(getResources().getString(R.string.massage_machine_time_title)
                // + " " + msg.arg1 +
                // getResources().getString(R.string.minute));
                // }
            }

        };

        float mRadius = circleButtons.getInnerRadius()-20;
//        timeView = new TouchTimeView(mActivity, mRadius, handler);
//        LayoutParams lp = new LayoutParams((int) ((mRadius * 2) + timeView.getmStrokeWidth() * 2.2), (int) ((mRadius * 2) + timeView.getmStrokeWidth() * 2.2));
//        lp.gravity = Gravity.CENTER;
//        container.addView(timeView,lp);
        TouchTimeView timer = (TouchTimeView)root.findViewById(R.id.timer);
        timer.setOnTimeChangeListener(new TouchTimeView.OnTimeChangeListener() {
            @Override
            public void onTimeChange(int time) {
                //time={0,5,10...60}
                if(!BluetoothServiceProxy.isconnect()){
                    Intent intent = new Intent(getActivity(), BluetoothInterface.class);
                    startActivity(intent);
                }else{
                    try {
                        BluetoothServiceProxy.sendCommandToDevice((short)(BluetoothServiceProxy.TIME_TAG + time));
                    } catch (Exception e) {
                        Log.e("bluetooth send timer command fail!");
                    }
                }
            }
        });
    }

    /**
     * 验证设备序列号，验证过了返回true,未验证弹出对话框进行验证
     * @return
     */
    private boolean isVerifySerialNum(){
        Activity activity = getActivity();
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
//        FrameLayout fl = (FrameLayout)alertDialog.findViewById(android.R.id.custom);
        View myView = activity.getLayoutInflater().inflate(R.layout.custom_input_dialog, null, false);
        final EditText et = (EditText)myView.findViewById(R.id.edit);
        alertDialog.setView(myView);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String s = et.getText().toString();
                VerifySerialNum(s);
            }
        });
        alertDialog.show();
        return false;
    }

    protected void VerifySerialNum(String s) {
        HttpSetting httpsetting = new HttpSetting();
        httpsetting.setFunctionModal("serial");
        httpsetting.setFunctionId("validateNum");
        httpsetting.setRequestMethod("POST");
        //设置参数
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userPin", User.userPin);
            jsonObject.put("serialNo", s);
            jsonObject.put("equType", "massager");
            jsonObject.put("supplier", "gz-hx");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpsetting.setJsonParams(jsonObject);
        httpsetting.setListener(new HttpGroup.OnAllListener() {
            @Override
            public void onEnd(HttpResponse response) {
                JSONObjectProxy json = response.getJSONObject();
                if(json != null){
                    Log.d("VerifySerialNum onEnd:"+json);
                    int code = json.getIntOrNull("code");
                    if(code == 1){
                        isVerified = true;
                        getActivity().getSharedPreferences(PrefsData.CONFIG, Context.MODE_PRIVATE).edit().putBoolean(PrefsData.CONFIG_ISVERIFIED, true).commit();
                        verifyView.setVisibility(View.GONE);
                    }else{
                        //TODO
                    }
                }else{
                    Log.e(getResources().getString(R.string.server_unnormal));
                }
            }

            @Override
            public void onError(HttpError httpError) {

            }

            @Override
            public void onProgress(int i, int j) {

            }

            @Override
            public void onStart() {
                Log.d("VerifySerialNum() start");
            }
        });
        httpsetting.setShowProgress(true);
        httpsetting.setNotifyUser(true);
        HttpGroupaAsynPool.getHttpGroupaAsynPool(getActivity()).add(httpsetting);
    }
}
