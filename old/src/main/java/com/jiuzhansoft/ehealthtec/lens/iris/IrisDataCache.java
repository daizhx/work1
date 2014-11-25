package com.jiuzhansoft.ehealthtec.lens.iris;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.hengxuan.eht.Http.utils.LanguageUtil;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.xml.XMLPullParserUtils;

import android.content.Context;

public class IrisDataCache {

	private static final String TAG = "IrisDataCache";
	private static final String LEFT_EYE_DATA = "lefteye.xml";
	private static final String RIGHT_EYE_DATA = "righteye.xml";
	private static final String LEFT_EYE_DATA_EN = "lefteye_en.xml";
	private static final String RIGHT_EYE_DATA_EN = "righteye_en.xml";
	private static IrisDataCache _instance = new IrisDataCache();
	
	private static final int EYE_MODULE_RADIUS = 232;
	private static final int STANDARD_EYE_MODULE_RADIUS = 90;
	private List<Organ> leftIrisData = null;
	private List<Organ> rightIrisData = null;
	private Context context = null;
	private float currentRaduis;
	private float getMaxRaduis, getScale;
	
	private IrisDataCache(){
		
	}
	public static IrisDataCache getInstance(){
		return _instance;
	}

	public float getMaxRaduis(List list){
		Iterator<Organ> iter = list.iterator();
		while(iter.hasNext()){
			Organ currentOrgan = iter.next();
			// getMaxRaduis = currentRaduis;
			currentRaduis = currentOrgan.getOutRaduis();
			if(currentRaduis > getMaxRaduis)
				getMaxRaduis = currentRaduis;
		}
		return getMaxRaduis;
	}

	public Organ getOrganIdByPositionInfo(float x, float y,
			final float center_x, final float center_y, final float outRaduis, 
			final float minRaduis, final float midRaduis, final float scale_x,
			final float scale_y, final boolean isLeftIris) {
		XMLPullParserUtils utils = null;
		InputStream is = null;
		if (isLeftIris) {
			if (this.leftIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + LEFT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(LEFT_EYE_DATA_EN);
					else
						is = context.getAssets().open(LEFT_EYE_DATA);
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.leftIrisData = utils.getAll();
					getMaxRaduis(leftIrisData);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else {
			if (this.rightIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + RIGHT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(RIGHT_EYE_DATA_EN);
					else
						is = context.getAssets().open(RIGHT_EYE_DATA);						
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.rightIrisData = utils.getAll();
					getMaxRaduis(rightIrisData);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		double angle = Math.atan2(center_y - y, x - center_x) * 180 / Math.PI;
		if (angle < 0) {
			angle = 360 + angle;
		}

		double distance = Math.sqrt((x - center_x) * (x - center_x)
				+ (y - center_y) * (y - center_y));
		return this.findOrganInfo(isLeftIris, angle, distance,outRaduis, minRaduis, midRaduis, scale_x,scale_y);
	}

	private float[] calculateInAndOutR(float getinr, 
			float getoutr, float current_R, float getscale,
			float thisinr, float thismidr){
		float getvalues[] = new float[2];
		float inr = 0f;//getRealRadius(getinr,getscale)*(current_R/(getMaxRaduis*getscale));
		float outr = 0f;//getRealRadius(getoutr,getscale)*(current_R/(getMaxRaduis*getscale));
		if(getinr == 16f && getoutr == 20f){
			inr = thisinr;
			outr = thisinr + ((thismidr - thisinr)*2)/17;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}else if(getinr == 20f && getoutr == 50f){
			inr = thisinr + ((thismidr - thisinr)*2)/17;
			outr = thismidr;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}else if(getinr == 50f && getoutr == 51f){
			inr = thismidr;
			outr = thismidr + (current_R - thismidr)/40;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		else if(getinr == 51f && getoutr == 55f){
			inr = thismidr + (current_R - thismidr)/40;
			outr = thismidr + (current_R - thismidr)/8;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		else if(getinr == 55f && getoutr == 70f){
			inr = thismidr + (current_R - thismidr)/8;
			outr = thismidr + (current_R - thismidr)/2;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		else if(getinr == 70f && getoutr == 80f){
			inr = thismidr + (current_R - thismidr)/2;
			outr = thismidr + ((current_R - thismidr)*3)/4;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}else if(getinr == 80f && getoutr == 85f){
			inr = thismidr + ((current_R - thismidr)*3)/4;
			outr = thismidr + ((current_R - thismidr)*7)/8;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		} else if(getinr == 85f && getoutr == 90f){
			inr = thismidr + ((current_R - thismidr)*7)/8;
			// outr = thismidr + ((current_R - thismidr)*7)/8;
			outr = current_R;
			getvalues[0] = inr;
			getvalues[1] = outr;
			return getvalues;
		}
		return getvalues;
		// Log.d(TAG, "getRealInRadius:"+ inr+"\tgetRealOutRadius:"+outr+"\tgetinr:"+getinr+"\tgetour:"+getoutr);
	}

	private Organ findOrganInfo(boolean isLeftIris,double angle,double distance,
			float out_Raduis, float in_Raduis, float mid_Raduis, float scale_x,float scale_y) {
		Organ organ = null;
		List<Organ> findedIrisData = null;
		if(isLeftIris){
			findedIrisData = this.leftIrisData;
		}else{
			findedIrisData = this.rightIrisData;
		}

		float getOranValues[] = new float[2];
		float start_angle = 0f, end_angle = 0f;
		for (Organ item : findedIrisData) {
			if(getMaxRaduis != 0){
				getOranValues = calculateInAndOutR(item.getInRaduis(), item.getOutRaduis(), 
						out_Raduis, scale_x, in_Raduis, mid_Raduis);
				float getRealInRadius = getOranValues[0], getRealOutRadius=getOranValues[1];
				// Log.d(TAG, "getRealInRadius:"+ getRealInRadius+"\tgetRealOutRadius:"+getRealOutRadius);
				start_angle = item.getStartAngle();
				end_angle = item.getEndAngle();
				start_angle = (start_angle+270)%360;
				end_angle = (end_angle+270)%360;
				if(end_angle < start_angle)
					end_angle = end_angle+360;
				else if(end_angle == start_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}
				if(angle > start_angle){
					if(angle < end_angle && distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}else if((angle+360) < end_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}
			}else{
				getMaxRaduis = 90;
				getOranValues = calculateInAndOutR(item.getInRaduis(), item.getOutRaduis(), 
						out_Raduis, scale_x, in_Raduis, mid_Raduis);
				float getRealInRadius = getOranValues[0], getRealOutRadius=getOranValues[1];
				start_angle = item.getStartAngle();
				end_angle = item.getEndAngle();
				start_angle = (start_angle+270)%360;
				end_angle = (end_angle+270)%360;
				if(end_angle < start_angle)
					end_angle = end_angle+360;
				else if(end_angle == start_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}
				if(angle > start_angle){
					if(angle < end_angle && distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}else if((angle+360) < end_angle){
					if(distance > getRealInRadius && distance < getRealOutRadius){
						organ = item;
						break;
					}
				}
			}
		}
		
		return organ;
	}
	public void setContext(Context context) {
		this.context = context;
	}

	public float getRealRadius(float radius,float scale_factor){
		// return ((float)EYE_MODULE_RADIUS/(float)STANDARD_EYE_MODULE_RADIUS)*radius*scale_factor;
		return radius*scale_factor;
	}
	
	public void initIrisDataByIndex(int index){
		XMLPullParserUtils utils = null;
		InputStream is = null;
		if (index == IrisInspectionActivity.LEFT_IRIS_EYE_ID) {
			if (this.leftIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + LEFT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(LEFT_EYE_DATA_EN);
					else
						is = context.getAssets().open(LEFT_EYE_DATA);
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.leftIrisData = utils.getAll();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else if(index == IrisInspectionActivity.RIGHT_IRIS_EYE_ID) {
			if (this.rightIrisData == null) {
				try {
					if (Log.D) {
						Log.d(TAG, "add iris data from file (" + RIGHT_EYE_DATA
								+ ")");
					}
					if(LanguageUtil.getLanguage() == 3)
						is = context.getAssets().open(RIGHT_EYE_DATA_EN);
					else
						is = context.getAssets().open(RIGHT_EYE_DATA);
					utils = new XMLPullParserUtils(is);
					utils.parseXml();
					this.rightIrisData = utils.getAll();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

}
