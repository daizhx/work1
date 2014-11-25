package com.jiuzhansoft.ehealthtec.healthclub;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupSetting;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstHttpProp;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.activity.HealthClub;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

public class SymptomDetailActivity extends BaseActivity {
	private static final String GET_SYMPTOM_DETAIL = "disease/getOneDiseaseByPos";
	private TextView tvDescription;
	private TextView tvSymptom;
	private String str1, str2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContent(R.layout.activity_symptom);
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		setTitle(name);
		int id = intent.getIntExtra("id", -1);
		if(id < 0){
			return;
		}
		tvDescription = (TextView)findViewById(R.id.tv_description);
		tvSymptom = (TextView)findViewById(R.id.tv_symptom);
		queryData(id);
	}
	
	private void queryData(int id){
		HttpSetting httpSetting = new HttpSetting();
		httpSetting.setFunctionId(GET_SYMPTOM_DETAIL);
		httpSetting.setRequestMethod("GET");
//		JSONObject jsonObject = new JSONObject();
//		try {
//			jsonObject.put("id", id);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		httpSetting.setJsonParams(jsonObject);
		httpSetting.addArrayListParam(id+"");
		httpSetting.addArrayListParam(HealthClub.gender + "");
		httpSetting.addArrayListParam(CommonUtil.getLocalLauguage(this)+"");
		
		httpSetting.setListener(new HttpGroup.OnAllListener() {
			
			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				JSONObject object = json.getJSONObjectOrNull("object");
				if(object == null)return;
				
				try {
					str1 = object.getString("disease_desc");
					str2 = object.getString("manifestation");
					tvDescription.setText(str1);
					tvSymptom.setText(str2);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}
		});
		httpSetting.setShowProgress(true);
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
		localHttpGroupSetting.setMyActivity(SymptomDetailActivity.this);
		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSONARRAY);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpSetting);
		
	}

}
