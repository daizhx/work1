package com.hengxuan.eht.Http;


import android.app.Activity;

import com.hengxuan.eht.Http.constant.ConstHttpProp;


public class HttpGroupSetting {
	private Activity myActivity;
	private int priority;
	private int type;

	public HttpGroupSetting() {

	}

	public Activity getMyActivity() {
		return myActivity;
	}

	public int getPriority() {
		return priority;
	}

	public int getType() {
		return type;
	}

	public void setMyActivity(Activity activity) {
		this.myActivity = activity;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setType(int type) {
		this.type = type;
		if (this.priority == 0)
			switch (type) {
			case ConstHttpProp.TYPE_JSON:
				setPriority(ConstHttpProp.PRIORITY_JSON);
				break;
			case ConstHttpProp.TYPE_IMAGE:
				setPriority(ConstHttpProp.PRIORITY_IMAGE);
				break;
			default:
				break;
			}
	}
}