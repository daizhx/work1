package com.jiuzhansoft.ehealthtec.lens.iris;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class IrisImageView extends View {
	private static final String TAG = "IrisImageView";

	public static final int OBJECT_COUNT = 2;
	public static final int OBJECT_CONTENT_INDEX = 0;
	public static final int OBJECT_MODELE_INDEX = 1;
	public static final int OBJECT_COMPONENT_INDEX = 2;

	private List<TouchObject> objList = null;
	private TouchObject[] completedObjList = null;
	private boolean isSetGalleryWidth[];
	private int galleryHeight[];
	private boolean needHandle = false;
	private boolean[] isMerged;
	private PopupWindow pop = null;
	// private Timer mTimer = null;
	// private MyTimerTask myTimerTask = null;

	private boolean isCompleted[];
	private int colorId;
	private float[] current_Raduis, current_MinRaduis, current_MidRaduis;
	private float[] x1, y1, x2, y2, x3, y3, x4, y4;

	private Canvas canvas;
	private int getindex;

	public void setIndex(int index) {
		getindex = index;
	}
	private float[] dXx, dYy;
	private float[] get_r, d2, d4;
	private float[] get_minr, get_midr;

	public void isCompleted(boolean isCompleted) {
		this.isCompleted[getindex] = isCompleted;
	}

	public void setColorId(int colorId) {
		this.colorId = colorId;
	}

	// Constructor
	public IrisImageView(Context context) {
		this(context, null);
	}

	public IrisImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// this.mTimer = new Timer();
		needHandle = false;
		this.completedObjList = new TouchObject[2];
		// this.initObjList();
		x1 = new float[2];
		y1 = new float[2];
		x2 = new float[2];
		y2 = new float[2];
		x3 = new float[2];
		y3 = new float[2];
		x4 = new float[2];
		y4 = new float[2];
		dXx = new float[2];
		dYy = new float[2];
		get_r = new float[2];
		d2 = new float[2];
		d4 = new float[2];
		get_minr = new float[2];
		get_midr = new float[2];
		current_Raduis = new float[2];
		current_MinRaduis = new float[2];
		current_MidRaduis = new float[2];
		isMerged = new boolean[2];
		isSetGalleryWidth = new boolean[2];
		isSetGalleryWidth[0] = true;
		isSetGalleryWidth[1] = true;
		galleryHeight = new int[2];
		galleryHeight[0] = -1;
		galleryHeight[1] = -1;
		isCompleted = new boolean[2];
		p = new int[2];
		reportxl = new int[2];
		reportxr = new int[2];
		reportyt = new int[2];
		reportyb = new int[2];

	}

	public boolean isMerge() {
		return isMerged[getindex];
	}

	public void initObjList(String[] iris_image_paths, int iris_index) {
		if (Log.D) {
			Log.d(TAG, "+++++++++++initObjList+++++++++++++++++");
		}
		if (this.objList == null) {
			this.objList = new ArrayList<TouchObject>();
		} else {
			this.objList.clear();
		}

		if (this.completedObjList[iris_index] == null) {
			TouchObject to = new TouchObject(this);
			if (iris_image_paths[iris_index] == null) {
				Bitmap getBm = BaseActivity.readBitmap(getContext(),
						R.drawable.eye_test);
				to.init(this.getContext(), getBm, iris_index, 4.0f);
			} else {

				BitmapFactory.Options bfOptions = new BitmapFactory.Options();
				bfOptions.inDither = false;
				bfOptions.inPurgeable = true;
				bfOptions.inInputShareable = true;
				bfOptions.inTempStorage = new byte[32 * 1024];

				File file = new File(iris_image_paths[iris_index]);
				FileInputStream fs = null;
				try {
					fs = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				try {
					if (fs != null) {
						Bitmap bm = BitmapFactory.decodeFileDescriptor(
								fs.getFD(), null, bfOptions);
						//test
						int w = bm.getWidth();
						int h = bm.getHeight();
						to.init(this.getContext(), bm, 0, 3.0f);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (fs != null) {
						try {
							fs.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			to.setHandlerTouchEvent(true);
			this.objList.add(to);
		} else {
			this.completedObjList[iris_index].setHandlerTouchEvent(true);
			this.objList.add(this.completedObjList[iris_index]);
		}
	}

	public void resetGalleryWidth(int height) {
		if (this.isSetGalleryWidth[getindex] == false) {
			return;
		}
		for (Iterator<TouchObject> it = this.objList.iterator(); it.hasNext();) {
			TouchObject to = it.next();
			to.setGalleryHeight(this.getResources(), height);
		}
		this.galleryHeight[getindex] = height;
		this.isSetGalleryWidth[getindex] = false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.rgb(170, 170, 170));
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		TouchObject to = this.objList.get(0);
		if (to.isShow()) {
			to.draw(canvas);
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isHandled = false;

		// return super.onTouchEvent(event);
		for (Iterator<TouchObject> it = this.objList.iterator(); it.hasNext();) {
			TouchObject to = it.next();
			if (to.isHandlerTouchEvent() == true) {
				isHandled = to.onTouchEvent(event);
			}
		}
		if (this.isMerged[getindex] == false) {
			return true;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			removePop();
			this.needHandle = true;
			break;
		case MotionEvent.ACTION_MOVE:
			this.needHandle = true;
			break;
		case MotionEvent.ACTION_CANCEL:
			this.needHandle = false;
			break;
		case MotionEvent.ACTION_UP:
			if (this.needHandle == true) {
				IrisDataCache dataCache = IrisDataCache.getInstance();
				dataCache.setContext(this.getContext());
				float scale_x = this.objList.get(0).getImg().getScaleX();
				float scale_y = this.objList.get(0).getImg().getScaleY();
				if (x3[getindex] != 0 && y3[getindex] != 0) {
					x3[getindex] = this.objList.get(0).getImg().getCenterX()
							- (dXx[getindex] * scale_x);
					y3[getindex] = this.objList.get(0).getImg().getCenterY()
							- (dYy[getindex] * scale_y);
					current_Raduis[getindex] = this.objList.get(0).getImg()
							.getCenterX()
							- this.objList.get(0).getImg().getMinX();
					current_Raduis[getindex] = (current_Raduis[getindex] * get_r[getindex])
							/ d2[getindex];
					current_MinRaduis[getindex] = (current_Raduis[getindex] * get_minr[getindex])
							/ get_r[getindex];
					current_MidRaduis[getindex] = (current_Raduis[getindex] * get_midr[getindex])
							/ get_r[getindex];
					Organ organ = dataCache.getOrganIdByPositionInfo(
							event.getX(), event.getY(), x3[getindex],
							y3[getindex], current_Raduis[getindex],
							current_MinRaduis[getindex],
							current_MidRaduis[getindex], scale_x, scale_y,
							getindex == 0 ? true : false);
					if (organ != null) {
						if (isCompleted[getindex])
							this.displayPopupInfo(organ, event.getX(),
									event.getY());
					}

				}
			}
			this.needHandle = false;
			break;
		}
		return true;
	}

	/**
	 * analysis result
	 * @param currentColor
	 * @param organ
	 */
	private void intentData(int currentColor, Organ organ) {
			Intent it = new Intent(IrisImageView.this.getContext(),
					IrisDetailInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("organ_info", organ);
			bundle.putInt("colorId", currentColor);
			it.putExtras(bundle);
			IrisImageView.this.getContext().startActivity(it);
	}
	
	
	private void showColorSelectorWindow(final Organ organ, int x, int y, int width, int height){
		Context context = getContext();
		View popView = ((Activity)this.getContext()).getLayoutInflater().inflate(R.layout.popwindow_list_menu, null);
		ListView listMenu = (ListView)popView.findViewById(R.id.list_menu);
		listMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long rowId) {
				// TODO Auto-generated method stub
				int colorId = position;
				setColorId(colorId);
				intentData(colorId, organ);
//				switch (position) {
//				case 0:
//					
//					break;
//				case 1:
//					break;
//				case 2:
//					break;
//				case 3:
//					break;
//
//				default:
//					break;
//				}
			}
		});
		TextView title = (TextView)popView.findViewById(R.id.title);
		title.setText(R.string.color);
		String[] strings = {
				context.getString(R.string.iris_color_light),
				context.getString(R.string.iris_color_dark_brown),
				context.getString(R.string.iris_color_dead_brown),
				context.getString(R.string.iris_color_deeply_black)
		};
		ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, strings);
		listMenu.setAdapter(arrayAdapter);
		popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		PopupWindow popWindow = new PopupWindow(popView, popView.getMeasuredWidth(), LayoutParams.WRAP_CONTENT, true);
		popWindow.setOutsideTouchable(false);
//		BitmapDrawable drawable = new BitmapDrawable(getResources(), ((BaseActivity)context).readBitmap(context, R.drawable.half_translucent));
		popWindow.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
		popWindow.showAtLocation(((Activity)context).findViewById(R.id.container), Gravity.CENTER, 0, 0);
		popWindow.update();
		
		
	}

	private void displayPopupInfo(final Organ organ, float x, float y) {
		y += IrisAnalysisActivity.actionBarHeight;
		y += IrisAnalysisActivity.statusBarHeight;
		// PopupWindow popWin = null;
		View popView = null;
		popView = LayoutInflater.from(this.getContext()).inflate(
				R.layout.overlay_pop, null);
		popView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showColorSelectorWindow(organ, 0, 0, 0, 0);
			}
		});
		TextView title = (TextView) popView.findViewById(R.id.map_bubbleTitle);
		TextView content = (TextView) popView.findViewById(R.id.map_bubbleText);
		title.setText(organ.getOrganId() + "");
		content.setText(organ.getName());
		popView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (this.pop == null) {
			try {
				this.pop = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				this.pop.setClippingEnabled(false);
				this.pop.showAtLocation(
						this,
						Gravity.LEFT | Gravity.TOP,
						(int) x - popView.getMeasuredWidth() / 2,
						(int) y + this.galleryHeight[getindex]
								- popView.getMeasuredHeight());
				this.pop.setFocusable(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (pop.isShowing()) {
				this.pop.dismiss();
				this.pop = null;
			} else {
				this.pop = null;
				this.pop = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				this.pop.setClippingEnabled(false);
				pop.showAtLocation(
						this,
						Gravity.LEFT | Gravity.TOP,
						(int) x - popView.getMeasuredWidth() / 2,
						(int) y + this.galleryHeight[getindex]
								- popView.getMeasuredHeight());
				this.pop.setFocusable(true);
			}
		}

	}

	private float getDoubleNum(float x) {
		return x * x;
	}

	public String getX_Y(float x1, float y1, float x2, float y2, float x4,
			float y4, float getScale) {
		return x3 + "," + y3;
	}

	public float getNumber_Minus(float x1, float x2) {
		return x2 - x1;
	}

	public float getNumber_Plus(float x1, float x2) {
		return x2 + x1;
	}

	public float getNumber_Division(float x1, float x2) {
		return x2 / x1;
	}

	private int p[];
	private int[] reportxl, reportxr, reportyt, reportyb;

	private void removeTransparent(Bitmap bmp) {
		int getw = bmp.getWidth();
		int geth = bmp.getHeight();
		reportxr[getindex] = getw - 1;
		reportyb[getindex] = geth - 1;
		for (int i = 0; i < getw; i++) {
			for (int j = 0; j < geth; j++) {
				p[getindex] = bmp.getPixel(i, j);
				if ((p[getindex] & 0xff000000) != 0) {
					reportxl[getindex] = i;
					break;
				}
			}
			if (0 != reportxl[getindex])
				break;
		}
		for (int i = getw - 1; i > reportxl[getindex]; i--) {
			for (int j = 0; j < geth; j++) {
				p[getindex] = bmp.getPixel(i, j);
				if ((p[getindex] & 0xff000000) != 0) {
					reportxr[getindex] = i;
					break;
				}
			}
			if (reportxr[getindex] != getw - 1)
				break;
		}
		for (int i = 0; i < geth; i++) {
			for (int j = 0; j < getw; j++) {
				p[getindex] = bmp.getPixel(j, i);
				if ((p[getindex] & 0xff000000) != 0) {
					reportyt[getindex] = i;
					break;
				}
			}
			if (reportyt[getindex] != 0)
				break;
		}
		for (int i = geth - 1; i > reportyt[getindex]; i--) {
			for (int j = 0; j < getw; j++) {
				p[getindex] = bmp.getPixel(j, i);
				if ((p[getindex] & 0xff000000) != 0) {
					reportyb[getindex] = i;
					break;
				}
			}
			if (reportyb[getindex] != geth - 1)
				break;
		}
	}

	private Bitmap getBitmapFromView(float getCenterX, float getCenterY,
			int getRLen, View mView) {
		/*
		 * Bitmap getBitmap = Bitmap.createBitmap((getRLen * 2), (getRLen * 2),
		 * Bitmap.Config.ARGB_8888); Canvas canvas = new Canvas(getBitmap);
		 * canvas.translate((getCenterX - getRLen), (getCenterY - getRLen));
		 * mView.draw(canvas);
		 */
		mView.setDrawingCacheEnabled(true);
		Bitmap getBitmap = mView.getDrawingCache();
		/*
		 * if(null == getBitmap) Log.i("getBitmap", "getBitmap is null");
		 */
		removeTransparent(getBitmap);
		Bitmap dropTransparent = Bitmap.createBitmap(getBitmap,
				reportxl[getindex], reportyt[getindex],
				(reportxr[getindex] - reportxl[getindex]),
				(reportyb[getindex] - reportyt[getindex]));
		return dropTransparent;
	}

	public void setTouchEventHandler(int index, int iris_index,
			View getCurrentView, PointF getP, float getR, float getMinR,
			float getMidR) {
		if (index == 2) {
			getindex = iris_index;
			TouchObject to = new TouchObject(this);
			Bitmap retBmp = this.mergeBitmap(getCurrentView, getP, getR);
			// this.saveBitmap(retBmp); //1-24
			if (this.isSetGalleryWidth[getindex] == false) {
				to.init(this.getContext(), retBmp,
						this.galleryHeight[getindex], 10.0f);
			} else {
				to.init(this.getContext(), retBmp, 0, 10.0f);
			}
			to.setHandlerTouchEvent(true); // 1-24

			x2[iris_index] = this.objList.get(0).getImg().getCenterX();
			y2[iris_index] = this.objList.get(0).getImg().getCenterY();
			float d1_leftx = this.objList.get(1).getImg().getMinX() - this.objList.get(0).getImg().getMinX();
			float d1_rightx = this.objList.get(0).getImg().getMaxX() - this.objList.get(1).getImg().getMaxX();
			float d2_topy = this.objList.get(0).getImg().getMaxY()
					- this.objList.get(1).getImg().getMaxY();
			float d2_bottomy = this.objList.get(1).getImg().getMinY()
					- this.objList.get(0).getImg().getMinY();
			get_r[getindex] = getR;
			get_minr[getindex] = getMinR;
			get_midr[getindex] = getMidR;
			d1_leftx = (getP.x - getR - 2)
					- this.objList.get(0).getImg().getMinX();
			d1_rightx = this.objList.get(0).getImg().getMaxX()
					- (getP.x + getR + 2);
			d2_topy = (getP.y - getR - 2)
					- this.objList.get(0).getImg().getMinY();
			d2_bottomy = this.objList.get(0).getImg().getMaxY()
					- (getP.y + getR + 2);
			x1[iris_index] = getP.x;
			float d1 = x1[iris_index] - this.objList.get(0).getImg().getMinX();
			d2[getindex] = x2[iris_index]
					- this.objList.get(0).getImg().getMinX();
			y1[iris_index] = getP.y;
			float h1 = this.objList.get(0).getImg().getMaxY() - y1[iris_index];
			float h2 = this.objList.get(0).getImg().getMaxY() - y2[iris_index];
			boolean isInternal = true;
			if (d1_leftx < 0 && d1_rightx > 0) {
				isInternal = false;
				d1 = getR;
				d2[getindex] = (this.objList.get(0).getImg().getMaxX()
						- this.objList.get(0).getImg().getMinX() - d1_leftx) / 2;
			} else if (d1_leftx < 0 && d1_rightx < 0) {
				isInternal = false;
				d2[getindex] = getR;
			} else if (d1_leftx > 0 && d1_rightx < 0) {
				isInternal = false;
				d1 = getR;
				d2[getindex] = (this.objList.get(0).getImg().getMaxX()
						- this.objList.get(0).getImg().getMinX() - d1_rightx) / 2;
			}

			if (!isInternal) {
				if (d2_bottomy > 0 && d2_topy > 0) {
				} else if (d2_bottomy < 0 && d2_topy > 0) {
					h1 = getR;
					h2 = (this.objList.get(0).getImg().getMaxY()
							- this.objList.get(0).getImg().getMinY() - d2_bottomy) / 2;
				} else if (d2_bottomy < 0 && d2_topy < 0) {
				} else if (d2_bottomy > 0 && d2_topy < 0) {
					h1 = getR;
					h2 = (this.objList.get(0).getImg().getMaxY()
							- this.objList.get(0).getImg().getMinY() - d2_topy) / 2;
				}
			}
			this.objList.remove(1);
			this.objList.remove(0);
			this.objList.add(to);

			x4[iris_index] = this.objList.get(0).getImg().getCenterX();
			y4[iris_index] = this.objList.get(0).getImg().getCenterY();
			d4[getindex] = x4[iris_index]
					- this.objList.get(0).getImg().getMinX();
			float h4 = this.objList.get(0).getImg().getMaxY() - y4[iris_index];


			if (isInternal) {
				if (d1_leftx > 0 && d1_rightx > 0 && d2_topy > 0
						&& d2_bottomy > 0) {
					x3[iris_index] = this.objList.get(0).getImg().getMinX()
							+ (d1 * d4[getindex]) / d2[getindex];
					y3[iris_index] = this.objList.get(0).getImg().getMaxY()
							- (h1 * h4) / h2;
				}
			} else if (d1_leftx < 0 && d1_rightx > 0) {
				x3[iris_index] = this.objList.get(0).getImg().getMinX()
						+ (d1 * d4[getindex]) / d2[getindex];
			} else if (d1_leftx < 0 && d1_rightx < 0) {
				x3[iris_index] = this.objList.get(0).getImg().getCenterX();
			} else if (d1_leftx > 0 && d1_rightx < 0) {
				x3[iris_index] = this.objList.get(0).getImg().getMaxX()
						- (d1 * d4[getindex]) / d2[getindex];
			}

			if (!isInternal) {
				if (d2_bottomy > 0 && d2_topy > 0) {
					y3[iris_index] = this.objList.get(0).getImg().getMaxY()
							- (h1 * h4) / h2;
				} else if (d2_bottomy < 0 && d2_topy > 0) {
					y3[iris_index] = this.objList.get(0).getImg().getMaxY()
							- (h1 * h4) / h2;
				} else if (d2_bottomy < 0 && d2_topy < 0) {
					y3[iris_index] = this.objList.get(0).getImg().getCenterY();
				} else if (d2_bottomy > 0 && d2_topy < 0) {
					y3[iris_index] = this.objList.get(0).getImg().getMinY()
							+ (h1 * h4) / h2;
				}
			}

			if (x3[iris_index] != 0 && y3[iris_index] != 0) {
				dXx[getindex] = this.objList.get(0).getImg().getCenterX()
						- x3[iris_index];
				dYy[getindex] = this.objList.get(0).getImg().getCenterY()
						- y3[iris_index];
			}
			this.completedObjList[iris_index] = to;
			this.postInvalidate();
			this.isMerged[getindex] = true;
			return;
		}
		int i = 0;
		for (Iterator<TouchObject> it = this.objList.iterator(); it.hasNext();) {
			TouchObject to = it.next();
			if (index == i) {
				to.setHandlerTouchEvent(true);
			} else {
				to.setHandlerTouchEvent(false);
			}
			i++;
		}
	}

	private Bitmap mergeBitmap(View getView, PointF getP, float getR) {

		View getStandardView = getView;
		PointF centerPoint = getP;
		float getStandardR = getR;
		Bitmap standardBmp = null;
		if (getStandardR != 0f) {
			standardBmp = getBitmapFromView(centerPoint.x, centerPoint.y,
					(int) getStandardR, getStandardView);
			TouchObject to = new TouchObject(this);
			if (standardBmp != null)
				to.init(getContext(), standardBmp, 0, 1.0f);
			to.setHandlerTouchEvent(true);
			this.objList.add(to);
		}

		x1[getindex] = getP.x;
		y1[getindex] = getP.y;
		float min_position_x = Math.min(this.objList.get(0).getImg().getMinX(),
				reportxl[getindex]);
		float max_position_x = Math.max(this.objList.get(0).getImg().getMaxX(),
				reportxr[getindex]);
		int bmpWidth = (int) (max_position_x - min_position_x);
		int min_position_y = (int) Math.min(this.objList.get(0).getImg()
				.getMinY(), reportyt[getindex]);
		int max_position_y = (int) Math.max(this.objList.get(0).getImg()
				.getMaxY(), reportyb[getindex]);
		int bmpHeight = (int) (max_position_y - min_position_y);
		Bitmap newb = Bitmap
				.createBitmap(bmpWidth, bmpHeight, Config.ARGB_8888);
		canvas = new Canvas(newb);
		float object_position_x = this.objList.get(0).getImg().getMinX();
		float object_position_y = this.objList.get(0).getImg().getMinY();
		float object_offset_x = 0.0f;
		float object_offset_y = 0.0f;
		float model_offset_x = 0.0f;
		float model_offset_y = 0.0f;
		if (object_position_x > reportxl[getindex]) {
			model_offset_x = 0.0f;
			object_offset_x = object_position_x - reportxl[getindex];
		} else {
			object_offset_x = 0.0f;
			model_offset_x = reportxl[getindex] - object_position_x;
		}
		if (object_position_y > reportyt[getindex]) {
			model_offset_y = 0.0f;
			object_offset_y = object_position_y - reportyt[getindex];
		} else {
			object_offset_y = 0.0f;
			model_offset_y = reportyt[getindex] - object_position_y;
		}
		Bitmap irisBmp = this.getScaledBitmap(0);
		canvas.drawBitmap(irisBmp, object_offset_x, object_offset_y, null);
		irisBmp.recycle();
		canvas.drawBitmap(standardBmp, model_offset_x, model_offset_y, null);
		standardBmp.recycle();

		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newb;
	}

	public void zoomIn() {
		for (Iterator<TouchObject> it = this.objList.iterator(); it.hasNext();) {
			TouchObject to = it.next();
			if (to.isHandlerTouchEvent() == true) {
				to.getImg().zoomIn();
			}
		}
	}

	public void zoomOut() {
		for (Iterator<TouchObject> it = this.objList.iterator(); it.hasNext();) {
			TouchObject to = it.next();
			if (to.isHandlerTouchEvent() == true) {
				to.getImg().zoomOut();
			}
		}
	}

	private Bitmap getScaledBitmap(int index) {
		Bitmap scaledBitmap = null;
		TouchObject to = this.objList.get(index);
		float dest_width = (int) (to.getImg().getMaxX() - to.getImg().getMinX());
		float dest_height = (int) (to.getImg().getMaxY() - to.getImg()
				.getMinY());
		if (Log.D) {
			Log.d(TAG, "dest_witdh=" + dest_width + ",dest_height="
					+ dest_height);
		}
		scaledBitmap = Bitmap.createScaledBitmap(to.getImg().getBitmap(),
				(int) dest_width, (int) dest_height, true);
		return scaledBitmap;
	}

	private Bitmap getScaledBitmapByModelScale(Bitmap bmp) {
		Bitmap returnBmp = null;
		float scale_x = this.objList.get(1).getImg().getScaleX();
		float scale_y = this.objList.get(1).getImg().getScaleY();
		if (Math.abs(scale_x - 1.0) > 0.1) {
			returnBmp = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth()
					/ scale_x + 0.5f),
					(int) (bmp.getHeight() / scale_y + 0.5f), true);
			bmp.recycle();
		} else {
			returnBmp = bmp;
		}
		return returnBmp;
	}

	private void saveBitmap(Bitmap bitmap) {
		String filename = Environment.getExternalStorageDirectory().getPath()
				+ "/huo.png";
		File f = new File(filename);
		FileOutputStream fos = null;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removePop() {
		if (IrisImageView.this.pop != null) {
			try {
				IrisImageView.this.pop.dismiss();
				IrisImageView.this.pop = null;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isMergeCompleted(int index) {
		if (this.completedObjList[index] != null) {
			return true;
		} else {
			return false;
		}
	}
}
