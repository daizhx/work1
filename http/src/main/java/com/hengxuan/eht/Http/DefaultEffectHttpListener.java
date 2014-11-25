package com.hengxuan.eht.Http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


public class DefaultEffectHttpListener implements HttpGroup.OnStartListener,
	HttpGroup.OnEndListener, HttpGroup.OnErrorListener{
	
	class State implements Runnable {

		private static final int WAIT_TIME = 500;
		
		private boolean hasThread;
		private RelativeLayout.LayoutParams layoutParams;
		private int missionCount;
		private ViewGroup mModal;
		private Activity myActivity;
		private ProgressBar progressBar;
		private ViewGroup rootFrameLayout;
		private int waitTime;
		private Handler mHandler = new Handler();
		

		private void firstMission()
		{
			if (hasThread)
			{
				waitTime = -1;
				notify();
			} else{
				if(!isShowProgress)return;
				final ViewGroup rootFrameLayout = getRootFrameLayout();
				final ViewGroup modal = getModal();
				newProgressBar();
				
				mHandler.post(new Runnable() {
					public void run(){
						StringBuilder stringbuilder = new StringBuilder("state add modal -->> ");
						String s = stringbuilder.append(modal).toString();
						
						LayoutParams layoutparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						rootFrameLayout.addView(modal, layoutparams);
						rootFrameLayout.invalidate();
					}
				});
			}
		}
		
		private ViewGroup getModal(){
			if (mModal == null){
				mModal = new RelativeLayout(myActivity);
				mModal.setOnTouchListener(new View.OnTouchListener() {
					public boolean onTouch(View view, MotionEvent motionevent)
					{
						return true;
					}
				});
				ColorDrawable colordrawable = new ColorDrawable(Color.BLACK);
				colordrawable.setAlpha(100);
				mModal.setBackgroundDrawable(colordrawable);
			}
			return mModal;
		}

		private ViewGroup getRootFrameLayout()
		{
			if (rootFrameLayout == null)
			{
				rootFrameLayout = (ViewGroup)myActivity.getWindow().peekDecorView();

				if (rootFrameLayout == null)
				{
					try
					{
						Thread.sleep(((long) (50L)));
					} 
					catch (InterruptedException interruptedexception) 
					{
					}
					
					rootFrameLayout = getRootFrameLayout();
				}
			}
			
			return ((ViewGroup) (rootFrameLayout));
		}

		private void lastMission()
		{
			if (hasThread)
			{
				waitTime = WAIT_TIME;
				notify();
			} else
			{
				(new Thread(this)).start();
				hasThread = true;
			}
		}

		private void newProgressBar()
		{
			mHandler.post(new Runnable() {
				public void run()
				{
					mModal.removeView(progressBar);
					progressBar = new ProgressBar(myActivity);
					mModal.addView(progressBar, layoutParams);
				}
			});
		}

		public boolean addMission()
		{
			boolean flag = true;
			
			synchronized(this) {
				int i = missionCount + 1;
				missionCount = i;
				if (missionCount != 1)
				{
					flag = false;
				}
				else
				{
					firstMission();
				}
			}
			
			return flag;
		}

		public boolean removeMission()
		{
			boolean flag = true;
			
			synchronized(this) {
				int i = missionCount - 1;
				missionCount = i;
				if (missionCount >= 1)
				{
					flag = false;
				}
				else
				{
					lastMission();
				}
			}
			
			return flag;
		}

		
		public void run()
		{
			while(hasThread){
			synchronized(this) {
				int i = waitTime;
				
				try {
					if (i != -1)
					{
						int j = waitTime;
						waitTime = 0;
						wait(j);
					}
					else
					{
						wait();
					}
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
				
				 if(waitTime == 0)
				{
					final ViewGroup rootFrameLayout = getRootFrameLayout();
					final ViewGroup modal = getModal();
										
					mHandler.post(new Runnable() {
						public void run(){
							StringBuilder stringbuilder = new StringBuilder("state remove modal -->> ");
							String s = stringbuilder.append(modal).toString();
							rootFrameLayout.removeView(modal);
							rootFrameLayout.invalidate();
						}
					});
					waitTime = WAIT_TIME;
					hasThread = false;
				}
				 else{
                     //TODO
				 }
			}
			}
		}

		public State(Activity myactivity)
		{
			super();
			waitTime = WAIT_TIME;
			RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams = layoutparams;
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			myActivity = myactivity;
		}
		
	}

//	private static final Map stateMap = Collections.synchronizedMap(new HashMap());
	private final Map stateMap = Collections.synchronizedMap(new HashMap());
	private Activity myActivity;
	private HttpGroup.OnEndListener onEndListener;
	private HttpGroup.OnErrorListener onErrorListener;
	private HttpGroup.OnStartListener onStartListener;
	private boolean isShowProgress;

	public DefaultEffectHttpListener(HttpSetting httpsetting, Activity myactivity) {
		if (httpsetting != null) {
			onStartListener = httpsetting.getOnStartListener();
			onEndListener = httpsetting.getOnEndListener();
			onErrorListener = httpsetting.getOnErrorListener();
		}
		
		isShowProgress = httpsetting.isShowProgress();
		if(isShowProgress){
			myActivity = myactivity;
		}
	}
	
	public DefaultEffectHttpListener(Activity myactivity) {
		onStartListener = null;
		onEndListener = null;
		onErrorListener = null;

		myActivity = myactivity;
	}

	private void missionBegins()
	{
		synchronized(stateMap) {
			if (myActivity != null)
			{
				State state = null;
				synchronized(stateMap) {
//					if (Log.D)
					{
						StringBuilder stringbuilder = new StringBuilder("state get with -->> ");
						String s = stringbuilder.append(myActivity).toString();
					}
				
					state = (State)stateMap.get(myActivity);

					if (state == null)
					{
						state = new State(myActivity);
						stateMap.put(myActivity, state);
					}				
				}
				
				state.addMission();
			}
		}
	}
	
	private void missionComplete()
	{
		synchronized(stateMap) {
			if (myActivity != null)
			{
				((State)stateMap.get(myActivity)).removeMission();
				//added
				stateMap.remove(myActivity);
				myActivity = null;
			}
		}
	}
	
//	public void onDestroy() {
//		synchronized(stateMap) {
//			stateMap.remove(myActivity);
//			myActivity = null;
//		}
//	}

	public void onEnd(HttpResponse httpresponse) {
		if (onEndListener != null)
			onEndListener.onEnd(httpresponse);
		missionComplete();
	}

	public void onError(HttpError httperror) {
		if (onErrorListener != null)
			onErrorListener.onError(httperror);
		missionComplete();
	}

	public void onStart() {
		missionBegins();
		if (onStartListener != null)
			onStartListener.onStart();
	}

}
