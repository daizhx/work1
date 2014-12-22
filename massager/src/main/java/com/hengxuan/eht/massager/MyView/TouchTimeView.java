package com.hengxuan.eht.massager.MyView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hengxuan.eht.massager.R;

import java.util.Timer;
import java.util.TimerTask;

public class TouchTimeView extends View {
	
	private float r1;
	private float r2;

	private Bitmap bitmap;
	private Bitmap newBmp;
	//���ߵĿ��
	private float arcWidth;
	//0~360
	private float rotateAngle;
	//circle center point
	private float pivotX,pivotY;
	
	private boolean isTouchCancel = false;
	
	private float mTxtWidth;
	private float mTxtHeight;
	private int currentTimeColor = 0xFF18befe;

    private Timer mTimer;
    private Handler timeHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(rotateAngle <= 0){
                mTimer.cancel();
                return;
            }
            float eliminateAngle = (float)360/(30*12);
            rotateAngle -= eliminateAngle;
            invalidate();
        }
    };
	
	public interface OnTimeChangeListener{
		void onTimeChange(int time);
	}
	
	private OnTimeChangeListener mOnTimeChangeListener;
	
	public void setOnTimeChangeListener(OnTimeChangeListener l){
		mOnTimeChangeListener = l;
	}
	
	public TouchTimeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inScaled = false;
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timer_bg, options);
//        mTimer = new Timer();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		int w = getMeasuredWidth();
		int h = getMeasuredHeight();
		
		int w1 = bitmap.getWidth();
		int h1 = bitmap.getHeight();
		if(newBmp == null){
			Matrix m = new Matrix();
			m.setScale((float)w/w1, (float)h/h1);
			newBmp = Bitmap.createBitmap(bitmap, 0, 0, w1, h1, m, false);
		}
		canvas.drawBitmap(newBmp, 0, 0, paint);
		
		r1 = w;
		arcWidth = r1/8;
		pivotX = w/2;
		pivotY = h/2;
		
		RectF oval = new RectF(arcWidth/2, arcWidth/2, (float)w - arcWidth/2, (float)h - arcWidth/2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(getResources().getColor(R.color.main_color2));
		paint.setStrokeWidth(arcWidth);
		canvas.drawArc(oval, -90, rotateAngle, false, paint);

		Paint mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mTextPaint.setColor(currentTimeColor);
		mTextPaint.setTextSize(r1 / 2);
		mTextPaint.setStrokeWidth(3);

		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
		
		String txt = ""+(int)((rotateAngle/360)*30);
		mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
		canvas.drawText(txt,  pivotX - mTxtWidth / 2, pivotY + mTxtHeight / 4, mTextPaint);
	}
	
	
	float preX = 0,preY = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		int index = event.getActionIndex();
//		int action = event.getActionMasked();
//		int pointerId = event.getPointerId(index);
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			preX = event.getX();
			preY = event.getY();
			double angle3 = calculatorAngle(preX, preY);
			break;
		case MotionEvent.ACTION_MOVE:
			if(!isTouchCancel){
			float x = event.getX();
			float y = event.getY();
			double angle1 = calculatorAngle(preX, preY);
			double angle2 = calculatorAngle(x, y);
			double angle = angle2 - angle1;
			if(Math.abs(angle) >= (2*Math.PI - Math.PI/30)){
					isTouchCancel = true;
					return true;
			}
			
			
			rotateAngle += (angle/(2*Math.PI))*360;
			if(rotateAngle > 360){
				rotateAngle = 360;
				isTouchCancel = true;
				return true;
			}
			if(rotateAngle < 0){
				rotateAngle = 0;
				isTouchCancel = true;
				return true;
			}
			preX = x;
			preY = y;
			invalidate();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			isTouchCancel = false;
			if(mOnTimeChangeListener != null){
				int time = (int)((rotateAngle/360)*30);
				mOnTimeChangeListener.onTimeChange(time);
			}
            if(mTimer != null){
                mTimer.cancel();
                mTimer.purge();
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeHander.sendEmptyMessage(0);
                }
            }, 1000*5, 1000*5);
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * ����Ƕ�
	 * @param preX
	 * @param preY
	 * @param x
	 * @param y
	 * @return
	 */
//	private double calcuteAngle(float preX, float preY, float x, float y) {
//		double a = Math.sqrt((double)((preX - pivotX)*(preX - pivotX) + (preY - pivotY)*(preY - pivotY)));
//		double b = Math.sqrt((double)((x - pivotX)*(x - pivotX) + (y - pivotY)*(y - pivotY)));
//		double c = Math.sqrt((double)((preX - x)*(preX - x) + (preY - y)*(preY - y)));
//		double angle = Math.acos((a*a+ b*b -c*c)/(2*a*b));
//		return angle;
//	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return ���صĽǶ��������Ƕȣ�0-2pi
	 */
	private double calculatorAngle(float x, float y){
		double angle = 0;
		if(x > pivotX){
			if(y < pivotY){
				//��һ����
				angle = Math.atan((double)(x-pivotX)/(double)(pivotY - y));
			}else{//y>=pivotY �ڶ�����
				angle = Math.PI/2 + Math.atan((double)(y - pivotY)/(double)(x - pivotX));
			}
		}else{
			if(y > pivotY){
				angle = Math.PI + Math.atan((double)(pivotX - x)/(double)(y - pivotY));
			}else{//��������
				if(pivotX == x){
					angle = 2*Math.PI;//or 0;
				}else{
					angle = Math.PI + Math.PI/2 + Math.atan((double)(pivotY - y)/(double)(pivotX - x));
				}
			}
		}
		return angle;
	}
	
}
