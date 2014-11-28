package com.jiuzhansoft.ehealthtec.weight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstFuncId;
import com.hengxuan.eht.Http.constant.ConstHttpProp;
import com.hengxuan.eht.Http.constant.ConstSysConfig;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ImprovePersonalInformation extends BaseActivity {


    private EditText realNameEdit, phoneNumEdit, addressEdit, ageEdit, heightEdit, weightEdit, emailEdit;
    private Button submit, cancel;
    private RadioGroup sexGroup;
    private SharedPreferences sharedPreferences;
    private String realName, phoneNum, address, email;
    int age, height, weight;
    char sex = 1;

    @Override
    public void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        setContentView(R.layout.improve_personal_information);
        setTitle(R.string.improve_personal_information);
        sharedPreferences = getSharedPreferences(ConstSysConfig.SYS_CUST_CLIENT, 0);
        realNameEdit = (EditText)findViewById(R.id.improve_real_name);
        realNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    realNameCheck();
            }

        });
        phoneNumEdit = (EditText)findViewById(R.id.improve_phone);
        phoneNumEdit.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    phoneNumCheck();
            }

        });
        addressEdit = (EditText)findViewById(R.id.improve_address);
        addressEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    addressCheck();
            }

        });
        ageEdit = (EditText)findViewById(R.id.improve_age);
        ageEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        ageEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    ageCheck();
            }

        });
        heightEdit = (EditText)findViewById(R.id.improve_height);
        heightEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        heightEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    heightCheck();
            }

        });
        weightEdit = (EditText)findViewById(R.id.improve_weight);
        weightEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        weightEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    weightCheck();
            }

        });
        emailEdit = (EditText)findViewById(R.id.improve_email);
        emailEdit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(!hasFocus)
                    emailCheck();
            }

        });
        sexGroup = (RadioGroup)findViewById(R.id.improve_sex);

        submit = (Button)findViewById(R.id.improve_submit);
        cancel = (Button)findViewById(R.id.improve_cancel);

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(realNameCheck() && phoneNumCheck() && addressCheck() && ageCheck() && heightCheck() && weightCheck() && emailCheck()){
                    onSubmit();
                }
            }

        });

        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }

        });

        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if(checkedId == R.id.improve_male){
                    sex = 1;
                }else{
                    sex = 0;
                }
            }

        });
    }
    private boolean realNameCheck(){
        String s = realNameEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            realNameEdit.setText("");
            realName = "";
            return true;
        }
        realName = realNameEdit.getText().toString();
        return true;
    }
    private boolean phoneNumCheck(){
        String s = phoneNumEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            phoneNumEdit.setText("");
            phoneNum = "";
            return true;
        }
        phoneNum = phoneNumEdit.getText().toString();
        return true;
    }
    private boolean addressCheck(){
        String s = addressEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            addressEdit.setText("");
            address = "";
            return true;
        }
        address = addressEdit.getText().toString();
        return true;
    }
    private boolean ageCheck(){
        String s = ageEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            ageEdit.setText("");
            ageEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            + getString(R.string.please_enter_your_age)
                            + "</font>"));
            return false;
        }
        if(s.length() >= 4 || Integer.parseInt(s) > 200){
            ageEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            +getString(R.string.please_enter_your_correct_age)
                            +"</font>"));
            return false;
        }
        age = Integer.parseInt(s);
        return true;
    }
    private boolean heightCheck(){
        String s = heightEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            heightEdit.setText("");
            heightEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            +getString(R.string.please_enter_your_height)
                            +"</font>"));
            return false;
        }
        if(s.length() >= 4 || Integer.parseInt(s) > 250 || Integer.parseInt(s) < 50){
            heightEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            +getString(R.string.please_enter_your_correct_height)
                            +"</font>"));
            return false;
        }
        height = Integer.parseInt(s);
        return true;
    }
    private boolean weightCheck(){
        String s = weightEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            weightEdit.setText("");
            weightEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            +getString(R.string.please_enter_your_weight)
                            +"</font>"));
            return false;
        }
        if(s.length() >= 4 || Integer.parseInt(s) > 1000){
            weightEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            +getString(R.string.please_enter_your_correct_weight)
                            +"</font>"));
            return false;
        }
        weight = Integer.parseInt(s);
        return true;
    }
    private boolean emailCheck(){
        String s = emailEdit.getText().toString().trim();
        if(s.equals("") || s == null){
            emailEdit.setText("");
            email = "";
            return true;
        }
        if (!CommonUtil.checkEmailWithSuffix(s)){
            emailEdit.setError(
                    Html.fromHtml("<font color=#00ff00>"
                            +getResources().getString(R.string.not_mail_format)
                            +"</font>"));
            return false;
        }
        email = emailEdit.getText().toString();
        return true;
    }
    private void onSubmit(){
        JSONObject jsonobject = new JSONObject();

        try {
            jsonobject.put("userPin", Integer.parseInt(getStringFromPreference(ConstHttpProp.USER_PIN)));
            jsonobject.put("realName", realName);
            jsonobject.put("phone", phoneNum);
            jsonobject.put("address", address);
            jsonobject.put("age", age);
            jsonobject.put("height", height);
            jsonobject.put("weight", weight);
            jsonobject.put("email", email);
            jsonobject.put("sex", sex);
        } catch (Exception e) {

        }
        HttpSetting httpsetting = new HttpSetting();
        httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_EDIT);
        httpsetting.setJsonParams(jsonobject);

        httpsetting.setListener(new HttpGroup.OnAllListener() {
            public void onEnd(HttpResponse httpresponse){

                try {
                    if("1".equals(httpresponse.getJSONObject().get("code").toString())){
                        try{
                            post(new Runnable() {
                                public void run(){
                                    final AlertDialog alertDialog = (new AlertDialog.Builder(ImprovePersonalInformation.this)).create();
                                    alertDialog.setMessage(getString(R.string.submit_success));
                                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            if(sex == 1)
                                                editor.putBoolean("sex", true).commit();
                                            else
                                                editor.putBoolean("sex", true).commit();
                                            editor.putInt("age", age).commit();
                                            editor.putInt("height", height).commit();
                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                            });
                        }catch (Exception exception){

                        }
                    }else{
                        String s = getText(R.string.register_err_busy).toString();
                        showDialog(s);
                    }
                } catch (JSONException e) {
                    String s = getText(R.string.register_err_busy).toString();
                    showDialog(s);
                }
            }

            public void onError(HttpError httperror){

                showDialog(getText(R.string.register_err_busy).toString());
            }

            public void onProgress(int i, int j){

            }

            public void onStart(){

            }
        });
        httpsetting.setNotifyUser(true);
        HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
    }
    void showDialog(final String s){
        post(new Runnable() {
            public void run()
            {
                // alertDialogBuilder.show();
                final AlertDialog dialog = (new AlertDialog.Builder(ImprovePersonalInformation.this)).create();
                dialog.setMessage(s);
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL,getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }
}

