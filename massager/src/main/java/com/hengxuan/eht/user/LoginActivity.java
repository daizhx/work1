package com.hengxuan.eht.user;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstFuncId;
import com.hengxuan.eht.Http.json.JSONArrayPoxy;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.hengxuan.eht.Http.utils.Des3;
import ehtRestClient.Md5Encrypt;
import com.hengxuan.eht.logger.Log;
import com.hengxuan.eht.massager.BaseActivity;
import com.hengxuan.eht.massager.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "login";
	// user name
	private String mUserName;
	private String mPassword;
	private String mLastUserName;
	private String mLastWrongPW;

	private EditText etName;
	private EditText etPW;
	private TextView tvLogin;
	private TextView tvRegister;
	private TextView tvFindPW;

	private TextView tvMessage;

	private Intent goIntent;
	private static final int LOGIN_FAIL = 1;
	private static final int LOGIN_SUCCESS = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOGIN_FAIL:
				Log.d("daizhx", "handle message = " + msg.what);
				tvMessage.setVisibility(View.INVISIBLE);
				break;
			case LOGIN_SUCCESS:
				if (goIntent != null) {
					startActivity(goIntent);
				}
				finish();
				break;
			default:
				break;
			}
		};
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setActionBarTitle(R.string.log_in);
		initView();
		if (getIntent().getExtras() != null) {
			goIntent = getIntent().getExtras().getParcelable("destIntent");
		}
	}

	private void initView() {
		etName = (EditText) findViewById(R.id.et_user_name);
		etName.setOnClickListener(this);
		etPW = (EditText) findViewById(R.id.et_pw);
		etPW.setOnClickListener(this);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvRegister.setOnClickListener(this);
		tvFindPW = (TextView) findViewById(R.id.tv_find_pw);
		tvFindPW.setOnClickListener(this);

		tvMessage = (TextView) findViewById(R.id.tv_message);
		tvLogin = (TextView) findViewById(R.id.tv_login);
		tvLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.et_user_name:
			break;
		case R.id.et_pw:
			break;
		case R.id.tv_register:
			startActivity(new Intent(this, RegisterActivity.class));
			finish();
			break;
		case R.id.tv_find_pw:
			//TODO 忘记密码
			finish();
			break;
		case R.id.tv_login:
			Login();
			break;
		default:
			break;
		}
	}

	private void Login() {
		((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken()
				,InputMethodManager.HIDE_NOT_ALWAYS);
		mUserName = etName.getText().toString();
		mPassword = etPW.getText().toString();
		if (mUserName.equals("") || mPassword.equals("")) {
			handle(1, getString(R.string.no_name_pw));
		} else {
			HttpSetting httpSetting = new HttpSetting();
			JSONObject jsonParams = new JSONObject();
			try {
                //加密-先MD5加密，然后DES3加密
                mPassword = Md5Encrypt.MD5(mPassword);
                mPassword = Des3.encode(mPassword, Des3.DES3_KEY, Des3.DES3_IV);
				jsonParams.put("password", mPassword);
				jsonParams.put("username", mUserName);
                jsonParams.put("mobileModels", "2");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
                e.printStackTrace();
            }
            httpSetting.setFunctionId(ConstFuncId.FUNCTION_ID_FOR_USER_LOGIN);
            httpSetting.setRequestMethod("POST");
			httpSetting.setJsonParams(jsonParams);
			httpSetting.setListener(new HttpGroup.OnAllListener() {

				@Override
				public void onProgress(int i, int j) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onError(HttpError httpError) {
					Log.d("login", "onError.....");
				}

				@Override
				public void onEnd(HttpResponse httpresponse) {
                    JSONObjectProxy json = httpresponse.getJSONObject();
					if (json != null) {
						try {
							Log.d("login", "Login result:" + json);
                            int code = json.getInt("code");
                            String msg = json.getString("msg");
                            JSONObjectProxy object = json.getJSONObjectOrNull("object");
                            if(code == 1 && object != null){
                                JSONObjectProxy userInfo = object.getJSONObjectOrNull("userInfo");
                                if(userInfo != null) {
                                    String userId = userInfo.getString("userId");
                                    User.setLogin(LoginActivity.this, mUserName, mPassword, true);
                                    User.setUserPin(LoginActivity.this, userId);
                                }else{
                                    //TODO
                                }
                                String bundNo = object.getString("bundNo");
                                if (goIntent != null) {
                                    startActivity(goIntent);
                                }
                                finish();
                            }else{
                                handle(1, getString(R.string.login_data_error));
                            }

						} catch (Exception exception) {
							StringBuilder stringbuilder = new StringBuilder("error message:");
							Log.e(TAG,stringbuilder.append(exception.getMessage()).toString());
						}
					} else {
                        Log.e(TAG, "json = "+null);
						handle(1, getString(R.string.server_unnormal));
					}
				}

				@Override
				public void onStart() {
					Log.d("login", "onStart.....");
				}
			});
			httpSetting.setNotifyUser(true);
			httpSetting.setShowProgress(true);
			HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpSetting);
		}
	}

	private void handle(final int code, String msg) {
		// TODO Auto-generated method stub
		if(code == LOGIN_SUCCESS){

		}

		Animation showAction = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(500);
		showAction.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mHandler.sendEmptyMessage(code);
					}
				}, 2000);
			}
		});
		tvMessage.setAnimation(showAction);
		tvMessage.setText(msg);
		tvMessage.setVisibility(View.VISIBLE);

	}
}
