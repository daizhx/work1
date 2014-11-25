package com.jiuzhansoft.ehealthtec.lens.hair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

public class HairReportActivity extends BaseActivity {
	private static final String TAG = "hair";
	private String getStartDate, getEndDate;

	private ArrayList<HashMap<String, String>> datelist;
	private ArrayList<HashMap<String, String>> contentlist;
	private String getUserPin;
	private ListView expandListView;
	private TextView isEmptytv;
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.hair_report);
		setContentView(R.layout.simple_list);
		datelist = new ArrayList<HashMap<String, String>>();
		contentlist = new ArrayList<HashMap<String, String>>();
		getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date(System.currentTimeMillis());
		String dateStr = sdf.format(currentDate);

		String getYear = dateStr.substring(0, 4);
		String getMonth = dateStr.substring(5, 7);
		int yearToInt = Integer.parseInt(getYear);
		int monthToInt = Integer.parseInt(getMonth);
		int getDay = Integer.parseInt(dateStr.substring(8, 10));

		Date startDate = new Date(yearToInt - 1 - 1900, monthToInt, getDay);
		String sDateStr = sdf.format(startDate);
		getStartDate = sDateStr;
		getEndDate = dateStr;

		// ...............
		expandListView = (ListView) findViewById(R.id.expandlist);
		isEmptytv = (TextView) findViewById(R.id.isempty);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					expandListView.setAdapter(new SimpleAdapter(
							HairReportActivity.this, datelist,
							R.layout.date_list, new String[] { "retDate" },
							new int[] { R.id.getcurrentdate }));
					expandListView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									TextView dateview = (TextView) arg1
											.findViewById(R.id.getcurrentdate);
									Intent intent = new Intent();
									intent.setClass(HairReportActivity.this,
											HairReportDetailActivity.class);
//									intent.putExtra("currentdate", dateview
//											.getText().toString());
									HashMap<String, String> content = contentlist.get(arg2);
									intent.putExtra("content", contentlist.get(arg2));
									startActivity(intent);

									if (Integer
											.valueOf(android.os.Build.VERSION.SDK) >= 5)
										overridePendingTransition(
												R.anim.in_from_left_animation,
												R.anim.out_to_right_animation);
								}
							});
					break;
				case 2:
					expandListView.setVisibility(View.GONE);
					isEmptytv.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		showHistory();
	}

	private void dateToList(JSONArrayPoxy poxy) {
		int i = 0;
		while (i < poxy.length()) {
			JSONObjectProxy objectproxy;
			try {
				objectproxy = poxy.getJSONObject(i);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				Date date = new Date(Long.parseLong(objectproxy.getStringOrNull("createOn")));
				String dateStr = sdf.format(date);
				HashMap<String, String> dateMap = new HashMap<String, String>();
				dateMap.put("retDate", dateStr);
				datelist.add(dateMap);
				
				HashMap<String, String> dataMap = new HashMap<String, String>();
				dataMap.put("name", objectproxy.getJSONObjectOrNull("skinHairInfo").getStringOrNull("name"));
				dataMap.put("status", objectproxy.getJSONObjectOrNull("skinHairInfo").getStringOrNull("status"));
				dataMap.put("reason", objectproxy.getJSONObjectOrNull("skinHairInfo").getStringOrNull("reason"));
				dataMap.put("suggestion", objectproxy.getJSONObjectOrNull("skinHairInfo").getStringOrNull("suggestion"));
				contentlist.add(dataMap);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

	private void showHistory() {
		datelist = null;
		datelist = new ArrayList<HashMap<String, String>>();
		expandListView.setVisibility(View.VISIBLE);
		isEmptytv.setVisibility(View.GONE);
		HttpSetting httpsetting = new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.HAREDATELIST);
		httpsetting.setRequestMethod("GET");
		httpsetting.addArrayListParam(CommonUtil.getLocalLauguage(this)+"");
		httpsetting.addArrayListParam(getUserPin);
		httpsetting.addArrayListParam("1");
		Log.d(TAG, "showHistory()---getStartDate="+getStartDate+",getEndDate="+getEndDate);
		httpsetting.addArrayListParam(getStartDate);
		httpsetting.addArrayListParam(getEndDate);
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
					String msg = json.getString("msg");
					JSONArrayPoxy object = json.getJSONArrayOrNull("object");
					Message message = new Message();
					if(code == 1 && object != null){
						dateToList(object);
						message.what = 1;
						handler.sendMessage(message);
					}else{
						message.what = 2;
						handler.sendMessage(message);
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

			}
		});
		httpsetting.setNotifyUser(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
}
