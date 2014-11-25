package com.hengxuan.eht.massager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstFuncId;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.hengxuan.eht.user.RegisterActivity;
import com.hengxuan.eht.user.User;
import com.hengxuan.eht.utils.CommonUtil;

import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2014/11/10.
 */
public class ResetPWActivity extends BaseActivity{

    private String password;
    private String newPassword;

    private EditText etPW;
    private EditText etNPW;
    private EditText etNPW2;

    private TextView tvSubmit;
    private TextView tvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);
        setTitle(R.string.reset_password);
        initView();
    }

    private void initView() {
        etPW = (EditText) findViewById(R.id.et_pw);
        etNPW = (EditText)findViewById(R.id.et_new_pw);
        etNPW2 = (EditText)findViewById(R.id.et_new_pw2);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void submit() {
        password = etPW.getText().toString();
        newPassword = etNPW.getText().toString();
        String s = etNPW2.getText().toString();
        if(!CommonUtil.checkPassword(password,6,20)){
            etPW.setError(getResources().getString(R.string.login_user_password_hint));
            return;
        }
        if(!CommonUtil.checkPassword(newPassword,6,20)){
            etNPW.setError(getResources().getString(R.string.user_password_hint));
            return;
        }
        if(!s.equals(newPassword)){
            etNPW2.setError(getResources().getString(R.string.password_not_same));
            return;
        }

        HttpSetting httpSetting = new HttpSetting();
        httpSetting.setFunctionId(ConstFuncId.RESETPW);
        httpSetting.setRequestMethod("POST");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", User.USER_NAME);
            jsonObject.put("oldPwd", password);
            jsonObject.put("newPwd", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpSetting.setJsonParams(jsonObject);
        httpSetting.setListener(new HttpGroup.OnAllListener() {
            @Override
            public void onEnd(HttpResponse response) {
                JSONObjectProxy json = response.getJSONObject();
                if(json != null){
                    int code = json.getIntOrNull("code");
                    String msg = json.getStringOrNull("msg");
                    JSONObjectProxy object = json.getJSONObjectOrNull("object");
                    if(code == 1){
                        startActivity(new Intent(ResetPWActivity.this, MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(ResetPWActivity.this, getString(R.string.reset_password_faile) + ":" + msg,Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //TODO
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

            }
        });

    }
}
