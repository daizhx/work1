package com.jiuzhansoft.ehealthtec.myview;

import com.jiuzhansoft.ehealthtec.log.Log;

import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * support drag and scale,copy from inet
 * @author daizhx
 *
 */
public class MyImageView extends ImageView{
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    private Bitmap bitmap = null;
    private DisplayMetrics dm;
    float minScaleR = 1.0f;
    static final float MAX_SCALE = 15f;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    
    public MyImageView(Context context) {
		super(context);
		setupView();
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupView();
	}
	
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}
	public void setupView(){
		Context context = getContext();
		dm = context.getResources().getDisplayMetrics();
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd != null){
			bitmap = bd.getBitmap();
		}
		this.setImageBitmap(bitmap);
//		this.setScaleType(ScaleType.MATRIX);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			savedMatrix.set(this.getImageMatrix());
            prev.set(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			if(mode == DRAG){
                matrix.set(savedMatrix);
                this.setScaleType(ScaleType.MATRIX);
                matrix.postTranslate(event.getX() - prev.x, event.getY()
                        - prev.y);
			}else if(mode == ZOOM){
				 float newDist = distance(event);
	                if (newDist > 10f) {
	                    matrix.set(savedMatrix);
	                    float scale = newDist / dist;
	                    this.setScaleType(ScaleType.MATRIX);
	                    matrix.postScale(scale, scale, mid.x, mid.y);
	                }
			}
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;	
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			dist = distance(event);
			if(dist > 10f){
				midPoint(mid, event);
                mode = ZOOM;
			}
			break;
		}
		
		
		this.setImageMatrix(matrix);
		return true;
	}

    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        
    }

    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}

