package com.jiuzhansoft.ehealthtec.iris.old;

import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController.PointInfo;
import com.jiuzhansoft.ehealthtec.lens.iris.MultiTouchController.PositionAndScale;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


public class TouchObject implements
		MultiTouchController.MultiTouchObjectCanvas {
	private static String TAG = "TouchObject";
	private static final float BOTTOM_FIX = 0F;
	private static float SCREEN_MARGIN_HEIGHT_TOP = 0F;
	private static float SCREEN_MARGIN_HEIGHT_BOTTOM = 0F;
	private static float SCREEN_MARGIN_WIDTH_LEFT = 0F;
	private static float SCREEN_MARGIN_WIDTH_RIGHT = 0F;
	private static final int UI_MODE_ROTATE = 1;
	private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
	private MultiTouchController.PointInfo currTouchPoint;
	private int galleryHeight;
	private Img img;
	private boolean mShowDebugInfo;
	private int mUIMode;
	private MultiTouchController multiTouchController;
	private TouchImageViewSelector imgViewSelector = null;
	
	private View view = null;
	private boolean isHandlerTouchEvent = false;
	private boolean isShow = true;

	static {
		SCREEN_MARGIN_WIDTH_LEFT = 100F;
		SCREEN_MARGIN_WIDTH_RIGHT = 100F;
		SCREEN_MARGIN_HEIGHT_TOP = 100F;
		SCREEN_MARGIN_HEIGHT_BOTTOM = 100F;
		
	}
	//consructor
	public TouchObject(View view) {
		this.view = view;
		this.img = null;
		this.multiTouchController = new MultiTouchController(this);
		this.currTouchPoint = multiTouchController.new PointInfo();
		this.mShowDebugInfo = false;
		this.mUIMode = UI_MODE_ROTATE;
		
	}
	

	public boolean isHandlerTouchEvent() {
		return isHandlerTouchEvent;
	}


	public void setHandlerTouchEvent(boolean isHandlerTouchEvent) {
		this.isHandlerTouchEvent = isHandlerTouchEvent;
	}
	

	public boolean isShow() {
		return isShow;
	}


	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}


	public void setImgViewSelector(TouchImageViewSelector imgViewSelector) {
		this.imgViewSelector = imgViewSelector;
	}

	/**
	 * @param context
	 * @param bitmap
	 * @param galleryHeight
	 */
	public void init(Context context, Bitmap bitmap, int galleryHeight, float scale) {
		Resources resources = context.getResources();
		this.galleryHeight = galleryHeight;
		SCREEN_MARGIN_HEIGHT_BOTTOM = SCREEN_MARGIN_HEIGHT_BOTTOM
				+ (float) galleryHeight + BOTTOM_FIX;
		this.img = new Img(bitmap, resources, scale);
		this.loadImages(context);
		//super.setBackgroundColor(Color.TRANSPARENT);
	}

	public void setGalleryHeight(Resources res,int galleryHeight){
		if(Log.D){
			Log.d(TAG, "reset gallery height according mainactivity metrics");
		}
		this.galleryHeight = galleryHeight;
		this.img.getMetrics(res);
		this.img.setPos(this.img.getDisplayWidth()/2, this.img.getDisplayHeight()/2, 1F, 1F);		
	}

	private void drawMultitouchDebugMarks(Canvas canvas) {
		currTouchPoint.isDown();
	}
	public Img getImg() {
		return img;
	}

	@Override
	public Img getDraggableObjectAtPoint(
			MultiTouchController.PointInfo pointInfo) {
		return img;
	}
	
	public void getPositionAndScale(Img img,
			MultiTouchController.PositionAndScale positionAndScale) {
		boolean rotateFlag = false;
		boolean isNoAnisotropic;
		float avgScale;
		boolean isAnisotropic;
		if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0)
			isNoAnisotropic = true;
		else
			isNoAnisotropic = false;

		avgScale = (img.getScaleX() + img.getScaleY()) / 2F;
		if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0)
			isAnisotropic = true;
		else
			isAnisotropic = false;
		if ((mUIMode & UI_MODE_ROTATE) != 0)
			rotateFlag = true;
		positionAndScale.set(img.getCenterX(), img.getCenterY(), isNoAnisotropic, avgScale,
				isAnisotropic, img.getScaleX(), img.getScaleY(), rotateFlag);
	}

	@Override
	public void getPositionAndScale(Object obj,
			PositionAndScale positionAndScale) {
		getPositionAndScale((Img) obj, positionAndScale);
	}

	public void loadImages(Context context) {
		img.load(context.getResources());
	}
	public void onDraw(Canvas canvas) {
		img.draw(canvas);
		if (mShowDebugInfo)
			drawMultitouchDebugMarks(canvas);
	}

	public boolean onTouchEvent(MotionEvent event) {
		return this.multiTouchController.onTouchEvent(event);
	}
	public void selectObject(Img img, PointInfo pointInfo) {
		currTouchPoint.set(pointInfo);
		this.view.invalidate();
	}

	@Override
	public void selectObject(Object obj, PointInfo pointInfo) {
		selectObject((Img) obj, pointInfo);

	}

	public boolean setPositionAndScale(Img img,
			PositionAndScale positionAndScale,
			PointInfo pointInfo) {
		currTouchPoint.set(pointInfo);
		boolean isSuccess = img.setPos(positionAndScale);
		if (isSuccess){
			this.view.invalidate();
		}
		return isSuccess;
	}

	@Override
	public boolean setPositionAndScale(Object obj,
			PositionAndScale positionAndScale, PointInfo pointInfo) {
		return setPositionAndScale((Img)obj, positionAndScale, pointInfo);
	}

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		this.view.invalidate();
	}

	public class Img {
		private Bitmap bitmap;
		private float centerX;
		private float centerY;
		private int displayHeight;
		private int displayWidth;
		private Drawable drawable;
		private boolean firstLoad;
		private int width;
		private int height;
		private float maxX;
		private float maxY;
		private float minX;
		private float minY;
		private float scaleX;
		private float scaleY;
		private float maxScale;

		// Constructor
		public Img(Bitmap bitmap, Resources resouces, float scale) {
			super();
			this.bitmap = bitmap;
			firstLoad = true;
			getMetrics(resouces);
			maxScale = scale;
		}

		private void resetScreenMargin() {
			if (width * scaleX > displayWidth) {
				SCREEN_MARGIN_WIDTH_LEFT = (float) displayWidth;
				SCREEN_MARGIN_WIDTH_RIGHT = (float) displayWidth;
			} else {
				SCREEN_MARGIN_WIDTH_LEFT = width * scaleX;
				SCREEN_MARGIN_WIDTH_RIGHT = width * scaleX;
			}
			if (height * scaleY > displayHeight) {
				SCREEN_MARGIN_HEIGHT_TOP = (float) (displayHeight - BOTTOM_FIX);
				SCREEN_MARGIN_HEIGHT_BOTTOM = (float) (displayHeight + BOTTOM_FIX);
			} else {
				SCREEN_MARGIN_HEIGHT_TOP = height * scaleY - BOTTOM_FIX;
				SCREEN_MARGIN_HEIGHT_BOTTOM = height * scaleY + BOTTOM_FIX;
			}
		}

		public boolean setPos(float screen_center_x, float screen_center_y, float scale_x, float scale_y) {
			if(scale_x >maxScale)
				scale_x = maxScale;
			if(scale_y > maxScale)
				scale_y = maxScale;
			if(scale_x < 0.5)
				scale_x = (float) 0.5;
			if(scale_y < 0.5)
				scale_y = (float) 0.5;
			scaleX = scale_x;
			scaleY = scale_y;
			
			resetScreenMargin();
			if (scale_x == scale_y && (double) scale_x >= 0.5D && scale_x <= maxScale) {
				float scaled_half_width = (float) (width / 2) * scale_x;
				float scaled_half_height = (float) (height / 2) * scale_y;
				float left_margin = screen_center_x - scaled_half_width;
				float top_margin = screen_center_y - scaled_half_height;
				float right_margin = screen_center_x + scaled_half_width;
				float bottom_margin = screen_center_y + scaled_half_height;
				if (left_margin > (displayWidth - SCREEN_MARGIN_WIDTH_RIGHT)) {
					minX = displayWidth - SCREEN_MARGIN_WIDTH_RIGHT;
					maxX = minX + scaled_half_width * 2F;
				} else {
					if (right_margin < SCREEN_MARGIN_WIDTH_LEFT) {
						maxX = SCREEN_MARGIN_WIDTH_LEFT;
						minX = maxX - scaled_half_width * 2F;
					} else {
						minX = left_margin;
						maxX = right_margin;
					}
				}
				if (top_margin > (displayHeight - SCREEN_MARGIN_HEIGHT_BOTTOM)) {
					minY = displayHeight - SCREEN_MARGIN_HEIGHT_BOTTOM;
					maxY = minY + scaled_half_height * 2F;
				} else {
					if (bottom_margin < SCREEN_MARGIN_HEIGHT_TOP) {
						maxY = SCREEN_MARGIN_HEIGHT_TOP;
						minY = maxY - scaled_half_height * 2F;
					} else {
						minY = top_margin;
						maxY = bottom_margin;
					}
				}
				/*minX = left_margin;
				maxX = right_margin;
				minY = top_margin;
				maxY = bottom_margin;*/
				centerX = minX + (maxX - minX) / 2F;
				centerY = minY + (maxY - minY) / 2F;
				//scaleX = scale_x;
				//scaleY = scale_y;
				return true;
			}
			return false;
		}
		/**
		 * @param positionAndScale
		 * @return
		 */
		public boolean setPos(PositionAndScale positionAndScale) {
			float scale_x;
			float scale_y;
			if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
				scale_x = positionAndScale.getScaleX();
			} else {
				scale_x = positionAndScale.getScale();
			}
			if ((mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0) {
				scale_y = positionAndScale.getScaleY();
			} else {
				scale_y = positionAndScale.getScale();
			}

			return setPos(positionAndScale.getXOff(),
					positionAndScale.getYOff(), scale_x, scale_y);
		}

		/**
		 * @param resources
		 */
		public void getMetrics(Resources resources) {
			DisplayMetrics metrics = resources.getDisplayMetrics();
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				this.displayWidth = Math.max(metrics.widthPixels,
						metrics.heightPixels);
			} else {
				this.displayWidth = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			}
			if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				this.displayHeight = Math.min(metrics.widthPixels,
						metrics.heightPixels);
			} else {
				this.displayHeight = Math.max(metrics.widthPixels,
						metrics.heightPixels)
						- TouchObject.this.galleryHeight;
			}
			
		}

		public boolean containsPoint(float f, float f1) {
			boolean flag = false;
			if ((f >= minX) && (f <= maxX) && (f1 >= minY) && (f1 <= maxY)) {
				flag = true;
			}
			return flag;
		}

		public void draw(Canvas canvas) {
			canvas.save();
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			try{
				drawable.draw(canvas);				
			}catch(Exception e){
				e.printStackTrace();
			}
			canvas.restore();
		}

		public void load(Resources resources) {
			getMetrics(resources);
			drawable = new BitmapDrawable(bitmap);
			width = drawable.getIntrinsicWidth();
			height = drawable.getIntrinsicHeight();
			if (firstLoad) {
				if(Log.D){
					Log.d(TAG, "first load------------++++++++++++");
				}
				firstLoad = false;
				setPos(displayWidth/2, displayHeight/2, 1F, 1F);
			}
		}

		public void unload() {
			drawable = null;
		}

		public void zoomIn() {
			if (setPos(centerX, centerY, scaleX - 0.5F, scaleY - 0.5F)){
				
			}
			TouchObject.this.view.invalidate();
		}

		public void zoomOut() {
			if (setPos(centerX, centerY, scaleX + 0.5F, scaleY + 0.5F)){
				
			}
			TouchObject.this.view.invalidate();
		}
		
		public void zoomToMerge(){
			if (setPos(centerX, centerY, 0.5F, 0.5F)){
				
			}
			TouchObject.this.view.invalidate();
		}
		
		public float getCenterX() {
			return centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public Drawable getDrawable() {
			return drawable;
		}

		public int getHeight() {
			return height;
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMaxY() {
			return maxY;
		}

		public float getMinX() {
			return minX;
		}

		public float getMinY() {
			return minY;
		}

		public float getScaleX() {
			return scaleX;
		}

		public float getScaleY() {
			return scaleY;
		}

		public int getWidth() {
			return width;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public int getDisplayHeight() {
			return displayHeight;
		}

		public int getDisplayWidth() {
			return displayWidth;
		}
		
		
	}
	
	public interface TouchImageViewSelector {
		public boolean getCurrentImageViewSelected();
		public boolean next();
	}

}
