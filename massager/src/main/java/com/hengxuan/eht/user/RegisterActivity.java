package com.hengxuan.eht.user;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstFuncId;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.hengxuan.eht.Http.utils.Des3;
import ehtRestClient.Md5Encrypt;
import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.massager.BaseActivity;
import com.hengxuan.eht.massager.MainActivity;
import com.hengxuan.eht.massager.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {
	public static final String TAG = "register";
	private EditText etName;
	private EditText etPW1;
	private EditText etPW2;
	private EditText etEmail;
	private EditText etPhone;

	private String mName;
	private String mPW1;
	private String mPW2;
	private String mStrPW;
	private String mEmail;
	private String mPhone;

	private TextView tvRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
        setActionBarTitle(R.string.register);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		etName = (EditText) findViewById(R.id.et_user_name);
		etPW1 = (EditText) findViewById(R.id.et_pw1);
		etPW2 = (EditText) findViewById(R.id.et_pw2);
		etEmail = (EditText) findViewById(R.id.et_email);
		etPhone = (EditText) findViewById(R.id.et_phone);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				register();
			}
		});
	}

	private void register() {
		getRegisterUserInfo();
		if (!inputCheck())
			return;
		mStrPW = Md5Encrypt.MD5(mPW1);
        try {
            mStrPW = Des3.encode(mStrPW,Des3.DES3_KEY,Des3.DES3_IV);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonobject = new JSONObject();
		try {
			jsonobject.put("username", mName);
			jsonobject.put("password", mStrPW);
			jsonobject.put("email", mEmail);
			jsonobject.put("appId", getString(R.string.app_name));
			if (!TextUtils.isEmpty(mPhone)) {
				jsonobject.put("phone", mPhone);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// jsonobject.put("uuid", StatisticsReportUtil.readDeviceUUID());
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setRequestMethod("POST");
		httpsetting.setReadTimeout(600);
		httpsetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_REGISTER);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			public void onEnd(HttpResponse httpresponse) {
                JSONObjectProxy json = httpresponse.getJSONObject();
				if (json != null) {

                    try {
                        int code = json.getInt("code");
                        String msg = json.getString("msg");
                        JSONObject object = json.getJSONObjectOrNull("object");
                        if (code == 1 && object != null) {
                            String userId = object.getString("userId");
                            User.setUserPin(RegisterActivity.this, userId);
                            User.setLogin(RegisterActivity.this,mName, mPW1, true);
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.register_fail) + ":" + msg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getStackTrace().toString());
                    }

				} else {
					Log.e(TAG, "no get json object!");
				}
			}

			public void onError(HttpError httperror) {
				Log.d(TAG, "-----register onError--------");
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.alert_message_poor_network2), Toast.LENGTH_SHORT).show();
			}

			public void onProgress(int i, int j) {
			}

			public void onStart() {
				Log.d(TAG, "-----register onStart--------");
			}
		});
		httpsetting.setNotifyUser(true);
		httpsetting.setShowProgress(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(RegisterActivity.this).add(
				httpsetting);
	}

	private boolean inputCheck() {
		boolean flag = true;
		if (TextUtils.isEmpty(mName)) {
			etName.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.login_user_name_hint)
					+ "</font>"));
			flag = false;
		} else {
			if (!nameCheck(mName)) {
				etName.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_name_hint)
						+ "</font>"));
				flag = false;
			}
		}
		if (TextUtils.isEmpty(mEmail)) {
			etEmail.setError(Html.fromHtml("<font color=#00ff00>"
					+ getResources().getString(R.string.not_mail_format)
					+ "</font>"));
			flag = false;
		} else {
			if (!mailCheck(mEmail)) {
				etEmail.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.not_mail_format)
						+ "</font>"));
				flag = false;
			}
		}
		if (TextUtils.isEmpty(mPW1)) {
			String s1 = getString(R.string.login_user_password_hint);
			etPW1.setError(Html.fromHtml("<font color=#00ff00>" + s1
					+ "</font>"));
			flag = false;
		} else {
			if (!mPW1.equals(mPW2)) {
				etPW2.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.password_not_same)
						+ "</font>"));
				flag = false;
			}
			if (!passwordCheck(mPW1, 6, 20)) {
				etPW1.setError(Html.fromHtml("<font color=#00ff00>"
						+ getResources().getString(R.string.user_password_hint)
						+ "</font>"));
			}
		}
		return flag;
	}
	private boolean mailCheck(String addr) {
		boolean flag = false;
		String s = addr.trim();
		if (!TextUtils.isEmpty(s)) {
			String suffixs = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			flag = Pattern.compile(suffixs).matcher(s).matches();
		}
		return flag;
	}
	private boolean nameCheck(String name) {
		boolean flag = false;
		String s = name.trim();
		if (!TextUtils.isEmpty(s)) {
			int length = s.length();
			if (4 <= length & length < 20) {
				flag = Pattern.compile("[\\wһ-��\\-a-zA-Z0-9_]+").matcher(s)
						.matches();
			}
		}
		return flag;
	}

	protected boolean passwordCheck(String pw, int i, int j) {
		boolean flag = false;
		String s = pw.trim();
		if (!TextUtils.isEmpty(s)) {
			String ss = (new StringBuilder("[a-zA-Z_0-9\\-]{")).append(i)
					.append(",").append(j).append("}").toString();
			flag = Pattern.compile(ss).matcher(s).matches();
		}
		return flag;
	}

	protected void getRegisterUserInfo() {
		mName = etName.getText().toString().trim();
		mEmail = etEmail.getText().toString().trim();
		// sRegPhone = mRegisterPhone.getText().toString();
		mPW1 = etPW1.getText().toString().trim();
		mPW2 = etPW2.getText().toString().trim();
		mPhone = etPhone.getText().toString().trim();

	}
}
