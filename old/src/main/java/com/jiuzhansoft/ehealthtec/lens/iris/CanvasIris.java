package com.jiuzhansoft.ehealthtec.lens.iris;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.xml.XMLPullParserUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CanvasIris extends View implements Runnable {
	private Paint mPaint = null;
	private Path mPath;
	private PointF startPoint = new PointF();
	private PointF endPoint = new PointF();
	// private PointF endPoint2 = new PointF();
	private float center_x, center_y;
	private float radius1, radius2, radius3;
	private float r1, r2, r3, r4, r5, r6;
	private float getRadius1, getRadius2, getRadius3;
	private int count = -1;
	private float getx1, gety1;
	private float getx2, gety2;
	private List<Organ> list = null;
	private float getInRaduis[], getOutRaduis[];
	private float startAngle[];
	private float endAngle[];
	private int getCount;
	private int currentCount = -1;
	private Thread thread;
	private boolean flag = true;
	private boolean crossFlag = false;
	private float current_x, current_y;
	private float reportCurrentX, reportCurrentY;
	private boolean isFirst = true;
	private int getIndex;
	private float maxRadius;
	private float screenWidth;
	private float screenHeight;
	private boolean isMoving = false;
	
	public CanvasIris(Context context, int getIndex, float center_x, float center_y) {
		super(context);
		// TODO Auto-generated constructor stub
		setFocusable(true);
		this.center_x = center_x;
		this.center_y = center_y;
		this.getIndex = getIndex;
		mPaint = new Paint();
		mPath = new Path();
		list = new ArrayList<Organ>();
		screenWidth = center_x *2;
		screenHeight = center_y * 2;
	}
	
	public PointF getCenter(){
		PointF pointF = new PointF();
		pointF.set(current_x, current_y);
		return pointF;
	}
	
	public float getMinR(){
		return getMinRadius(radius1, radius2, radius3);
	}
	
	public float getMidR(){
		return getMidRadius(radius1, radius2, radius3);
	}
	
	public float getMaxR(){
		return getMaxRadius(radius1, radius2, radius3);
	}
	
	private void getInfo(){
		XMLPullParserUtils xmlUtils = null;
		InputStream is = null;
		try {
			if(getIndex == 0)
				is = getContext().getAssets().open("lefteye.xml");
			else if(getIndex == 1)
				is = getContext().getAssets().open("righteye.xml");
			
			xmlUtils = new XMLPullParserUtils(is);
			xmlUtils.parseXml();
			list = xmlUtils.getAll();
			getCount = list.size();
			getInRaduis = new float[getCount];
			getOutRaduis = new float[getCount];
			startAngle = new float[getCount];
			endAngle = new float[getCount];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		for(Organ organ: list){
			currentCount ++;
			if(currentCount > (getCount -1))
				break;
			getInRaduis[currentCount] = organ.getInRaduis();
			getOutRaduis[currentCount] = organ.getOutRaduis();
			startAngle[currentCount] = organ.getStartAngle();
			endAngle[currentCount] = organ.getEndAngle();
			startAngle[currentCount] = (startAngle[currentCount]+270)%360;
			endAngle[currentCount] = (endAngle[currentCount]+270)%360;
		}
	}
	
	private void currentLine(float inR, float outR, Path path, double mRad, Canvas mCanvas, Paint mP){
		float dx1 = (float) (inR * Math.cos(mRad));
		dx1 = dx1 + current_x;
		float dy1 = (float) (inR * Math.sin(mRad));
		dy1 = current_y - dy1;
		
		float dx2 = (float) (outR * Math.cos(mRad));
		dx2 = dx2 + current_x;
		float dy2 = (float) (outR * Math.sin(mRad));
		dy2 = current_y - dy2;
		
		mCanvas.save();
		mCanvas.drawLine(dx1, dy1, dx2, dy2, mP);
		mCanvas.restore();
	}
	
	private void drawLine(float firstR, float mr1, float secondR, float mr2, float mr3,
			float mr4, float mr5, Path path, Canvas mCanvas, Paint mP){
		for(int i=0; i<getCount; i++){
			
			double mRadian = getRadian(startAngle[i]);
			Log.i("lines", "getInRaduis ="+getInRaduis[5]+"\tgetOutRaduis ="+getOutRaduis[5]);
			if(getOutRaduis[i] == 20f){
				getInRaduis[i] = firstR;
				getOutRaduis[i] = mr1;
				currentLine(getInRaduis[i], getOutRaduis[i], path, mRadian, mCanvas, mP);
				getInRaduis[i] = 16f;
				getOutRaduis[i] = 20f;
			}
			
			if(getOutRaduis[i] == 50f){
				getInRaduis[i] = mr1;
				getOutRaduis[i] = secondR;
				currentLine(getInRaduis[i], getOutRaduis[i], path, mRadian, mCanvas, mP);
				getInRaduis[i] = 20f;
				getOutRaduis[i] = 50f;
			}
			
			if(getOutRaduis[i] == 51f){
				getInRaduis[i] = secondR;
				getOutRaduis[i] = mr2;
				currentLine(getInRaduis[i], getOutRaduis[i], path, mRadian, mCanvas, mP);
				getInRaduis[i] = 50f;
				getOutRaduis[i] = 51f;
			}
			
			if(getOutRaduis[i] == 55f){
				getInRaduis[i] = mr2;
				getOutRaduis[i] = mr3;
				currentLine(getInRaduis[i], getOutRaduis[i], path, mRadian, mCanvas, mP);
				getInRaduis[i] = 51f;
				getOutRaduis[i] = 55f;
			}
			
			if(getOutRaduis[i] == 70f){
				getInRaduis[i] = mr3;
				getOutRaduis[i] = mr4;
				currentLine(getInRaduis[i], getOutRaduis[i], path, mRadian, mCanvas, mP);
				getInRaduis[i] = 55f;
				getOutRaduis[i] = 70f;
			}
			
			if(getOutRaduis[i] == 80f){
				getInRaduis[i] = mr4;
				getOutRaduis[i] = mr5;
				currentLine(getInRaduis[i], getOutRaduis[i], path, mRadian, mCanvas, mP);
				getInRaduis[i] = 70f;
				getOutRaduis[i] = 80f;
			}
		}
	}
	
	private float getMaxRadius(float radius1, float radius2, float radius3){
		return Math.max(Math.max(radius1, radius2), radius3);
	}
	
	private float getMinRadius(float radius1, float radius2, float radius3){	
		return Math.min(Math.min(radius1, radius2), radius3);
	}
	
	private float getMidRadius(float radius1, float radius2, float radius3){
		float maxR = getMaxRadius(radius1, radius2, radius3);
		float minR = getMinRadius(radius1, radius2, radius3);
		float midR = radius1;
		if(midR == maxR){
			midR = radius2;
			if(midR == minR)
				midR = radius3;
		}else if(midR == minR){
			midR = radius2;
			if(midR == maxR)
				midR = radius3;
		}
		return midR;
	}
	
	private double getRadian(float angle){
		return ((angle * Math.PI) / 180);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(1f);
		canvas.save();		
		
		mPaint.setColor(Color.argb(255, 128, 255, 0));
		if(startPoint.x == 0 && endPoint.x == 0){
			canvas.save();
			canvas.drawLine(center_x - 10, center_y, center_x + 10, center_y, mPaint);
			canvas.restore();
			
			canvas.save();
			canvas.drawLine(center_x, center_y - 15, center_x, center_y + 15, mPaint);
			canvas.restore();
		}else{
			canvas.save();
			canvas.drawLine(reportCurrentX - 10, reportCurrentY, reportCurrentX + 10, reportCurrentY, mPaint);
			canvas.restore();
			
			canvas.save();
			canvas.drawLine(reportCurrentX, reportCurrentY - 15, reportCurrentX, reportCurrentY + 15, mPaint);
			canvas.restore();
		}		
		
		if(crossFlag){
			if(null == thread){
				thread = new Thread(this);
				thread.start();				
			}
			if(count == 0){
				if(radius1 >= maxRadius && isMoving == true)
					mPaint.setColor(Color.argb(255, 255, 0, 0));
				canvas.drawCircle(current_x, current_y, radius1, mPaint);
				canvas.restore();
			}
			if(count == 1){
				canvas.save();
				canvas.drawCircle(current_x, current_y, radius1, mPaint);
				canvas.restore();
				canvas.save();
				if(radius2 >= maxRadius && isMoving == true)
					mPaint.setColor(Color.argb(255, 255, 0, 0));
				canvas.drawCircle(current_x, current_y, radius2, mPaint);
				canvas.restore();
				
				mPaint.setColor(Color.argb(255, 23, 168, 255));
				if(radius2 > radius1){
					r1 = (float) ((radius2 - radius1) / 8.5);
					r1 = r1 + radius1;
					canvas.save();
					canvas.drawCircle(current_x, current_y, r1, mPaint);
					canvas.restore();
				}else if(radius2 < radius1){
					r1 = (float) ((radius1 - radius2) / 8.5);
					r1 = r1 + radius2;
					canvas.save();
					canvas.drawCircle(current_x, current_y, r1, mPaint);
					canvas.restore();
				}
			}
			if(count == 2){
				float getMaxR = getMaxRadius(radius1, radius2, radius3);
				float getMinR = getMinRadius(radius1, radius2, radius3);
				float getMidR = getMidRadius(radius1, radius2, radius3);
				mPaint.setColor(Color.argb(255, 128, 255, 0));
				canvas.save();
				canvas.drawCircle(current_x, current_y, getMinR, mPaint);
				canvas.restore();
				canvas.save();
				canvas.drawCircle(current_x, current_y, getMidR, mPaint);
				canvas.restore();
				
				mPaint.setColor(Color.argb(255, 23, 168, 255));
				r1 = (float) ((getMidR - getMinR) * 2) / 17;
				r1 = r1 + getMinR;
				canvas.save();
				canvas.drawCircle(current_x, current_y, r1, mPaint);
				canvas.restore();
				
				mPaint.setColor(Color.argb(255, 23, 168, 255));
				r2 = (float) ((getMaxR - getMidR) / 40);
				r2 = r2 + getMidR;
				canvas.save();
				canvas.drawCircle(current_x, current_y, r2, mPaint);
				canvas.restore();
				
				r3 = (float) ((getMaxR - getMidR) / 8);
				r3 = r3 + getMidR;
				canvas.save();
				canvas.drawCircle(current_x, current_y, r3, mPaint);
				canvas.restore();
				
				r4 = (float) ((getMaxR - getMidR) / 2);
				r4 = r4 + getMidR;
				canvas.save();
				canvas.drawCircle(current_x, current_y, r4, mPaint);
				canvas.restore();
				Log.i("getR", "r3= " + r3);
				
				r5 = (float) (((getMaxR - getMidR) * 3) / 4);
				r5 = r5 + getMidR;
				canvas.save();
				canvas.drawCircle(current_x, current_y, r5, mPaint);
				canvas.restore();
				
				r6 = (float) (((getMaxR - getMidR) * 7) / 8);
				r6 = r6 + getMidR;
				canvas.save();
				canvas.drawCircle(current_x, current_y, r6, mPaint);
				canvas.restore();
				
				mPaint.setColor(Color.argb(255, 128, 255, 0));
				canvas.save();
				if(radius3 >= maxRadius && isMoving == true)
					mPaint.setColor(Color.argb(255, 255, 0, 0));
				canvas.drawCircle(current_x, current_y, getMaxR, mPaint);
				canvas.restore();
				Log.i("getR", "getMaxR= " + getMaxR);

				if(getRadius1 != 0 && getRadius2 != 0 && getRadius3 != 0){
					float getMaxR2 = getMaxRadius(getRadius1, getRadius2, getRadius3);
					float getMinR2 = getMinRadius(getRadius1, getRadius2, getRadius3);
					float getMidR2 = getMidRadius(getRadius1, getRadius2, getRadius3);
					
					float r_1 = (float) ((getMidR2 - getMinR2) * 2) / 17;
					r_1 = r_1 + getMinR2;
					
					float r_2 = (float) ((getMaxR2 - getMidR2) / 40);
					r_2 = r_2 + getMidR2;
					
					float r_3 = (float) ((getMaxR2 - getMidR2) / 8);
					r_3 = r_3 + getMidR2;
					
					float r_4 = (float) ((getMaxR2 - getMidR2) / 2);
					r_4 = r_4 + getMidR2;
					
					float r_5 = (float) (((getMaxR2 - getMidR2) * 3) / 4);
					r_5 = r_5 + getMidR2;
					
					/*float r_6 = (float) (((getMaxR2 - getMidR2) * 7) / 8);
				r_6 = r_6 + getMidR2;*/
					
					mPaint.setColor(Color.argb(255, 23, 168, 255));
					if(flag){
						getInfo();
						flag = false;
					}
					if(null == thread){}
					else if(!thread.isInterrupted()){
						thread.interrupt();
						thread = null;
					}
					drawLine(getMinR2, r_1, getMidR2, r_2, r_3, r_4, r_5, mPath, canvas, mPaint);
				}				
			}
			
		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			isMoving = false;
			startPoint.set(event.getX(), event.getY());
			postInvalidate();
			if(crossFlag){
				count ++;
				if(count == 3){
					count = 0;	
					getRadius1 = 0;
					getRadius2 = 0;
					getRadius3 = 0;
					radius1 = 0;
					radius2 = 0;
					radius3 = 0;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			isMoving = true;
			endPoint.set(event.getX(), event.getY());
			postInvalidate();
			if(crossFlag){
				float radius = (float) Math.sqrt((startPoint.x - endPoint.x) * (startPoint.x - endPoint.x) +
						(startPoint.y - endPoint.y) * (startPoint.y - endPoint.y));
				radius = Math.min(radius, maxRadius);
				if(count == 0)
					radius1 = radius;
				else if(count == 1)
					radius2 = radius;
				else if(count == 2)
					radius3 = radius;	
			}
			if(isFirst && !crossFlag){
				current_x = center_x + event.getX() - startPoint.x;
				current_y = center_y + event.getY() - startPoint.y;
				reportCurrentX = current_x;
				reportCurrentY = current_y;
				Log.i("isExe", "reportCurrentX1 ="+reportCurrentX+"\treportCurrentY1 ="+reportCurrentY);
			}else if(isFirst && !crossFlag){
				Log.i("isExe", "reportCurrentX2 ="+reportCurrentX+"\treportCurrentY2 ="+reportCurrentY);
				current_x = reportCurrentX + event.getX() - startPoint.x;
				current_y = reportCurrentY + event.getY() - startPoint.y;
				/*reportCurrentX = current_x;
				reportCurrentY = current_y;*/
				Log.i("isExe", "Is Execution? reportCurrentX ="+reportCurrentX+"\treportCurrentY ="+reportCurrentY);
				Log.i("isFirst", "reportCurrentX3 ="+reportCurrentX+"\treportCurrentY3 ="+reportCurrentY);
			}
			break;
		case MotionEvent.ACTION_UP:
			isMoving = false;
			endPoint.set(event.getX(), event.getY());
			postInvalidate();
			Log.i("getpoint", "x = "+ event.getX());
			Log.i("getpoint", "y = "+ event.getY());
			if(isFirst && !crossFlag){
				current_x = center_x + event.getX() - startPoint.x;
				current_y = center_y + event.getY() - startPoint.y;
				reportCurrentX = current_x;
				reportCurrentY = current_y;
				isFirst = false;
				Log.i("isExe", "reportCurrentX4 ="+reportCurrentX+"\treportCurrentY4 ="+reportCurrentY);
			}else if(!isFirst && !crossFlag){
				reportCurrentX = current_x;
				reportCurrentY = current_y;
			}
			
			if(crossFlag){
				float radius = (float) Math.sqrt((startPoint.x - endPoint.x) * (startPoint.x - endPoint.x) +
						(startPoint.y - endPoint.y) * (startPoint.y - endPoint.y));
				radius = Math.min(radius, maxRadius);
				if(count == 0){
					radius1 = radius;
				}
				else if(count == 1){
					radius2 = radius;
				}
				else if(count == 2){
					radius3 = radius;
					getRadius1 = radius1;
					getRadius2 = radius2;
					getRadius3 = radius3;
				}
			}else{
				final AlertDialog dialog = (new AlertDialog.Builder(getContext())).create();
				dialog.setMessage(getResources().getString(R.string.isOrNot));
				dialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						crossFlag = true;
						isFirst = false;
						maxRadius = Math.min(Math.min(current_x, screenWidth - current_x) ,Math.min(current_y, screenHeight - current_y));
						dialog.dismiss();
					}
					
				});
				dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						crossFlag = false;
//						isCurrent = true;
						isFirst = true;
						center_x = reportCurrentX;
						center_y = reportCurrentY;
						dialog.dismiss();
					}
					
				});
				dialog.show();
			}
			break;
		default: break;
		}
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			postInvalidate();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(null != thread){
			if(!thread.isInterrupted()){
				thread.interrupt();
				thread = null;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
