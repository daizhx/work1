package com.jiuzhansoft.ehealthtec.lens.skin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstFuncId;
import com.hengxuan.eht.Http.constant.ConstHttpProp;
import com.hengxuan.eht.Http.json.JSONArrayPoxy;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

public class SkinAnalysisResultActivity extends BaseActivity {
	private static final String TAG = "skin";
	private int analysisMode;
	private TextView contentText;
	private TextView content;
	private TextView stateText;
	private TextView state;
	private TextView reason;
	private TextView suggest;
	private JSONArrayPoxy jsonPoxy;
	private JSONObject json;
	private Button addtoReportBtn;
	private String skanContentText="";
	private String sentContent="";
	private String skanContent="";
	private String skanStateText="";
	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.skin_analysis_result);
		setTitle(R.string.skin_result);
		contentText = (TextView)findViewById(R.id.skan_content_text);
		content = (TextView)findViewById(R.id.skan_content);
		stateText = (TextView)findViewById(R.id.skan_state_text);
		state = (TextView)findViewById(R.id.skan_state);
		reason = (TextView)findViewById(R.id.skan_reason);
		suggest = (TextView)findViewById(R.id.skan_suggest);
		addtoReportBtn = (Button) findViewById(R.id.nextbtn);
		addtoReportBtn.setEnabled(false);
		addtoReportBtn.setVisibility(View.VISIBLE);
		addtoReportBtn.setText(getResources().getString(R.string.addto_report));
		addtoReportBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addtoReportBtn.setEnabled(false);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date currentdate = new Date(System.currentTimeMillis());
				String timestr = format.format(currentdate);
				String getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
				addToServer(timestr, getUserPin, analysisMode, sentContent);
			}
			
		});
		analysisMode = getIntent().getExtras().getInt("mode");
		
		int per = 0;
		switch(analysisMode){
			case SkinAnalysisActivity.MSG_OIL:
				skanContentText=getResources().getString(R.string.oil_state);
				if(getIntent().getExtras().getInt("content") <= 5){
					sentContent = "3-5";
				}else if(getIntent().getExtras().getInt("content") == 6){
					sentContent = "6";
				}else if(getIntent().getExtras().getInt("content") <= 8){
					sentContent = "7-8";
				}else if(getIntent().getExtras().getInt("content") <= 24){
					sentContent = "9-24";
				}else{
					sentContent = "25-35";
				}
				per = getIntent().getExtras().getInt("content");
				if(per < 3)
					per = 3;
				else if(per > 35)
					per = 35;
				skanContent = per + "%";
				skanStateText=getResources().getString(R.string.oil_content);
				break;
			case SkinAnalysisActivity.MSG_WATER:
				skanContentText=getResources().getString(R.string.water_state);
				if(getIntent().getExtras().getInt("content") <= 3){
					sentContent = "3";
				}else if(getIntent().getExtras().getInt("content") <= 9){
					sentContent = "4-9";
				}else if(getIntent().getExtras().getInt("content") <= 14){
					sentContent = "10-14";
				}else if(getIntent().getExtras().getInt("content") <= 29){
					sentContent = "15-29";
				}else{
					sentContent = "30-65";
				}
				per = getIntent().getExtras().getInt("content");
				if(per < 3)
					per = 3;
				else if(per > 65)
					per = 65;
				skanContent = per + "%";
				skanStateText=getResources().getString(R.string.water_content);
				break;
			case SkinAnalysisActivity.MSG_PIGMENT:
				skanContentText=getResources().getString(R.string.pigment_state);
				if(getIntent().getExtras().getInt("content") <= 9){
					sentContent = "8-9";
				}else if(getIntent().getExtras().getInt("content") <= 19){
					sentContent = "10-19";
				}else if(getIntent().getExtras().getInt("content") <= 29){
					sentContent = "20-29";
				}else if(getIntent().getExtras().getInt("content") <= 39){
					sentContent = "30-39";
				}else{
					sentContent = "40-75";
				}
				per = getIntent().getExtras().getInt("content");
				if(per < 8)
					per = 8;
				else if(per > 75)
					per = 75;
				skanContent = per + "%";
				skanStateText=getResources().getString(R.string.pigment_content);
				break;
			case SkinAnalysisActivity.MSG_ELASTIC:
				skanContentText="����״����";
				if(getIntent().getExtras().getInt("content") <= 34){
					sentContent = "15-34";
				}else if(getIntent().getExtras().getInt("content") <= 49){
					sentContent = "35-49";
				}else if(getIntent().getExtras().getInt("content") <= 64){
					sentContent = "50-64";
				}else if(getIntent().getExtras().getInt("content") <= 69){
					sentContent = "65-69";
				}else{
					sentContent = "70-71";
				}
				per = getIntent().getExtras().getInt("content");
				if(per < 15)
					per = 15;
				else if(per > 71)
					per = 71;
				skanContent = per + "%";
				skanStateText="�������ԣ�";
				break;
			case SkinAnalysisActivity.MSG_COLLAGEN:
				skanContentText=getResources().getString(R.string.collagen_state);
				if(getIntent().getExtras().getInt("content") <= 49){
					sentContent = "25-49";
				}else if(getIntent().getExtras().getInt("content") <= 64){
					sentContent = "50-64";
				}else if(getIntent().getExtras().getInt("content") <= 79){
					sentContent = "65-79";
				}else if(getIntent().getExtras().getInt("content") <= 84){
					sentContent = "80-84";
				}else{
					sentContent = "85-86";
				}
				per = getIntent().getExtras().getInt("content");
				if(per < 25)
					per = 25;
				else if(per > 86)
					per = 86;
				skanContent = per + "%";
				skanStateText=getResources().getString(R.string.collagen_content);
				break;
			case SkinOtherAnalysisActivity.ANALYSIS_MODE_PORE:
				skanContentText=getResources().getString(R.string.pore_state);
				if(getIntent().getExtras().getFloat("content") <= 0.02){
					sentContent = "<0.02mm";
				}else if(getIntent().getExtras().getFloat("content") <= 0.04){
					sentContent = "0.02mm-0.04mm";
				}else if(getIntent().getExtras().getFloat("content") <= 0.06){
					sentContent = "0.05mm-0.06mm";
				}else if(getIntent().getExtras().getFloat("content") <= 0.11){
					sentContent = "0.07mm-0.11mm";
				}else{ 
					sentContent = ">0.11mm";
				}
				float length = getIntent().getExtras().getFloat("content");
				if(length < 0.01)
					length = 0.01f;
				skanContent = String.format("%.2f", length) + "mm";
				skanStateText=getResources().getString(R.string.pore_radius);
				break;
			case SkinOtherAnalysisActivity.ANALYSIS_MODE_ACNE:
				skanContentText=getResources().getString(R.string.acne_radius);
				sentContent="1";
				skanContent = String.format("%.2f", getIntent().getExtras().getFloat("content")) + "mm";
				break;
			case SkinOtherAnalysisActivity.ANALYSIS_MODE_SENSI_NUM:
				skanContentText=getResources().getString(R.string.blood_streak_num);
				sentContent="1";
				skanContent = getIntent().getExtras().getInt("content") + "";
				break;
			case SkinOtherAnalysisActivity.ANALYSIS_MODE_SENSI_AREA:
				analysisMode = SkinOtherAnalysisActivity.ANALYSIS_MODE_SENSI_NUM;
				skanContentText=getResources().getString(R.string.blood_streak_area);
				sentContent="1";
				skanContent = String.format("%.2f", getIntent().getExtras().getFloat("content")) + "mm^2";
				break;
			default:
				break;
		}
		contentText.setText(skanContentText);
		content.setText(skanContent);
		stateText.setText(skanStateText);
		getSkanResult(sentContent);
	}

	public void getSkanResult(String content){
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.SKANANALYSISRESULT);
		httpsetting.setRequestMethod("GET");
		httpsetting.addArrayListParam(CommonUtil.getLocalLauguage(this)+"");
		httpsetting.addArrayListParam("0");
		httpsetting.addArrayListParam(analysisMode+"");
		httpsetting.addArrayListParam(content);
//		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {
			
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
				JSONObjectProxy data = response.getJSONObject();
				if(json == null)return;
				if(data != null){
					try {
						int code = data.getInt("code");
						String msg = data.getString("msg");
						JSONObject object = data.getJSONObjectOrNull("object");
						if(1 == code && null != object){
							state.setText(object.getString("status"));
							reason.setText(object.getString("reason"));
							suggest.setText(object.getString("suggestion"));	
							addtoReportBtn.setEnabled(true);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onStart() {
				// TODO Auto-generated method stubd
				
			}
		});
		httpsetting.setNotifyUser(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
	
	private void addToServer(String currentDate, String userPin, 
			int type, String content){
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("clientId", "GZ-Hengxuan");
//			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userId", userPin);
			jsonobject.put("type", type);
			jsonobject.put("content", content);
			jsonobject.put("infoType", "0");
			jsonobject.put("imgUrl", "0");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.ADDSKANRESULTTOSERVER);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setRequestMethod("POST");
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				try {
					int code = json.getInt("code");
					if(code == 1){
						Toast.makeText(SkinAnalysisResultActivity.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(SkinAnalysisResultActivity.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}});
		httpsetting.setNotifyUser(true);
		httpsetting.setShowProgress(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}

}
