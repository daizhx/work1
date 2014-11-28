package com.jiuzhansoft.ehealthtec;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.eht.Http.HttpError;
import com.hengxuan.eht.Http.HttpGroup;
import com.hengxuan.eht.Http.HttpGroupSetting;
import com.hengxuan.eht.Http.HttpGroupaAsynPool;
import com.hengxuan.eht.Http.HttpResponse;
import com.hengxuan.eht.Http.HttpSetting;
import com.hengxuan.eht.Http.constant.ConstFuncId;
import com.hengxuan.eht.Http.constant.ConstHttpProp;
import com.hengxuan.eht.Http.json.JSONArrayPoxy;
import com.hengxuan.eht.Http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.activity.HealthArticleActivity;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.utils.AsynImageLoader;

import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HealthTipsFragment extends Fragment {
	private static final String TAG = "MainActivity";
	private Context mContext;
	private List<Map<String, String>> datalist = new ArrayList<Map<String, String>>();
	private ListView listView;
	private ListViewAdapter listViewAdapter;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// listView.invalidate();
				listViewAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}

		};
	};


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mContext = getActivity();
		((MainActivity)mContext).leftIcon.setImageResource(R.drawable.ic_action_back);
		((MainActivity) getActivity()).setFragmentFlage(true);
		View view = inflater.inflate(R.layout.scroll_list, null, false);
		listView = (ListView) view.findViewById(R.id.list_view);
		listViewAdapter = new ListViewAdapter();
		listView.setAdapter(listViewAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String, String> map = new HashMap<String, String>();
				map = datalist.get(position);
				
				Intent intent = new Intent(mContext, HealthArticleActivity.class);
				intent.putExtra("title", ((String)map.get("title")));
				intent.putExtra("content", ((String)map.get("content")));
				mContext.startActivity(intent);
			}
		});
		getData();
		return view;
	}

	class ListViewAdapter extends BaseAdapter {
		AsynImageLoader asynImageLoader = new AsynImageLoader(false);
		@Override
		public int getCount() {
			return datalist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				holder = new Holder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.list_item_3, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.content = (TextView) convertView
						.findViewById(R.id.content);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			Map<String, String> map = new HashMap<String, String>();
			map = datalist.get(position);
			Log.d(TAG, "Position=" + position + ",map=" + map);
			
			asynImageLoader.setCacheDisk(false);
			asynImageLoader.showImageAsyn(getActivity(),holder.icon, "http://182.254.137.149/"+(String) map.get("url"),
					0);
			
			holder.title.setText((String) map.get("title"));
			String shortContent =  (String) map.get("content");
			if(shortContent.length() > 40){
				shortContent = shortContent.substring(0, 30);
				shortContent = shortContent.concat("...");
			}
			
			holder.content.setText(shortContent);

			return convertView;
		}

		class Holder {
			public ImageView icon;
			public TextView title;
			public TextView content;
		}

	}

	private void getData() {

		HttpSetting httpSetting = new HttpSetting();
		httpSetting.setFunctionId(ConstFuncId.HEALTH_TIPS);
		httpSetting.setRequestMethod("GET");
		httpSetting.addArrayListParam("1");
		httpSetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onProgress");
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onError:"+httpError.getMessage());
			}

			@Override
			public void onEnd(HttpResponse response) {
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				
				// TODO Auto-generated method stub
				try {
					int code = json.getInt("code");
					String msg = json.getString("msg");
					JSONObject object = json.getJSONObjectOrNull("object");
					
					if (code == 1 && object != null) {
						JSONArrayPoxy jsonArray = (JSONArrayPoxy) object.getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = (JSONObject) jsonArray
									.get(i);
							String content = jsonObject.getString("content");
							String title = jsonObject.getString("title");
							String url = jsonObject.getString("url");
							Log.d(TAG, "content=" + content + ",title=" + title
									+ ",url=" + url);
							Map<String, String> map = new HashMap<String, String>();
							map.put("title", title);
							map.put("content", content);
							map.put("url", url);
							datalist.add(map);
						}
//						mHandler.sendEmptyMessage(1);
						listViewAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				Log.d(TAG, "onStart");
			}
		});
		httpSetting.setNotifyUser(true);
		HttpGroupSetting localHttpGroupSetting = new HttpGroupSetting();
		localHttpGroupSetting.setMyActivity(getActivity());
		localHttpGroupSetting.setType(ConstHttpProp.TYPE_JSONARRAY);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(getActivity()).add(httpSetting);

	}
}
