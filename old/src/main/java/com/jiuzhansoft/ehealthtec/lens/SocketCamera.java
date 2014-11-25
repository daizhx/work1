package com.jiuzhansoft.ehealthtec.lens;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.jiuzhansoft.ehealthtec.log.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class SocketCamera implements CameraSource {

	private final Rect bounds;
	private final boolean preserveAspectRatio;
	private final Paint paint = new Paint();
	private Bitmap bitmap = null;
	private String startStr = "Content-type: image/jpeg\n\n";
	private String endStr = "\n--arflebarfle\n";
	
	public SocketCamera(int width, int height, boolean preserveAspectRatio) {
		bounds = new Rect(0, 0, width, height);
		this.preserveAspectRatio = preserveAspectRatio;
		
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
	}
	
	public int getWidth() {
		return bounds.right;
	}
	
	public int getHeight() {
		return bounds.bottom;
	}
	
	public boolean open(HttpURLConnection httpURLconnection) {
		/* nothing to do */
		/*try {
			httpURLconnection.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return true;
	}
	
	public void getImage(HttpURLConnection httpURLconnection, final Canvas canvas){
		// if(httpURLconnection.getResponseCode() == 200){
		int getcode = 0;
		InputStream in = null;
		try {
			getcode = httpURLconnection.getResponseCode();
			in = httpURLconnection.getInputStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(getcode == 200){
			try {
				canvasBmp(canvas, readStream(in,canvas));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				getcode = 0;
				e.printStackTrace();
			}
		}
	}
	private Matrix matrix;

	private void canvasBmp(Canvas canvas, byte[] buffer){
		if(bitmap != null) {
			if(!bitmap.isRecycled()){   
				bitmap.recycle();
		        System.gc();
			}
		}
		bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
		// Log.e("isrun", "bitmap width:"+bitmap.getWidth()+"; bitmap height"+bitmap.getHeight());
		//render it to canvas, scaling if necessary
		if (bounds.right == bitmap.getWidth() &&
				bounds.bottom == bitmap.getHeight()) {
			canvas.save();
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.restore();
		} else {
			Rect dest;
			// dest = bounds;
			if (preserveAspectRatio) {
				/*dest = new Rect(bounds);
				dest.bottom = bitmap.getHeight() * bounds.right / bitmap.getWidth();
				dest.offset(0, (bounds.bottom - dest.bottom)/2);*/
				dest = bounds;
			} else {
				dest = bounds;
			}
			canvas.save();
			// canvas.drawBitmap(bitmap, null, dest, paint);
			matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap tempBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
			canvas.drawBitmap(tempBmp, null, dest, paint);
			if(!tempBmp.isRecycled()){   
				tempBmp.recycle();
		        System.gc();
			}
			canvas.restore();
		}
	}
	
	private int startIndex = -1;
	private int endIndex = -1;
	private byte[] storeB;
	private byte startByte[] = new byte[startStr.length()];
	private byte endByte[] = new byte[endStr.length()];
	private byte b[];
	private String tempStr;
	private byte[] readStream(InputStream in, Canvas canvas) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[10240];
		int len = -1;
		byte retbyte[] = null;
		while((len = in.read(buffer)) != -1){
			baos.write(buffer, 0, len);
			b = baos.toByteArray();
			tempStr = new String(b, "utf-8");
			if(-1 != startIndex && tempStr.contains(endStr)){
				for(int i=b.length-endStr.length()-1; i>0; i--){
					for(int j=i; j<(endStr.length()+i); j++){
						endByte[j-i] = b[j];
					}
					if((new String(endByte, "utf-8")).equals(endStr)){
						endIndex = i;
						retbyte = new byte[storeB.length+endIndex+1];
						System.arraycopy(storeB, 0, retbyte, 0, storeB.length);
						System.arraycopy(b, 0, retbyte, storeB.length, endIndex);
						for(int j=endIndex+endStr.length(); j<(b.length-startStr.length()-1); j++){
							for(int k=j; k<(startStr.length()+j); k++)
								startByte[k-j] = b[k];
							if((new String(startByte, "utf-8")).equals(startStr)){
								startIndex = j+startStr.length();
								storeB = new byte[b.length-startIndex];
								System.arraycopy(b, startIndex, storeB, 0, storeB.length);
								break;
							}
						}
						return retbyte;
					}					
				}
			}else if(tempStr.contains(startStr) && tempStr.contains(endStr)){
				for(int i=0; i<b.length; i++){
					for(int j=i; j<(startStr.length()+i) & j<b.length; j++)
						startByte[j-i] = b[j];
					if((new String(startByte, "utf-8")).equals(startStr)){
						startIndex = i+startStr.length();
						break;
					}
				}
				for(int i=b.length-endStr.length()-1; i>startIndex; i--){
					for(int j=i; j<(endStr.length()+i); j++){
						endByte[j-i] = b[j];
					}
					if((new String(endByte, "utf-8")).equals(endStr)){
						endIndex = i;
						break;
					}
				}
				if(-1 != startIndex && endIndex > startIndex){
					retbyte = new byte[endIndex - startIndex];
					System.arraycopy(b, startIndex, retbyte, 0, (endIndex - startIndex));
					
					for(int i=endIndex+endStr.length(); i<(b.length-startStr.length()); i++){
						for(int j=i; j<(startStr.length()+i); j++)
							startByte[j-i] = b[j];
						if((new String(startByte, "utf-8")).equals(startStr)){
							startIndex = i+startStr.length();
							storeB = new byte[b.length-startIndex];
							System.arraycopy(b, startIndex, storeB, 0, storeB.length);
							break;
						}
					}
					baos.close();
					return retbyte;
				}
			}
		}
		return null;
	}

	public void capture(Canvas canvas, HttpURLConnection httpURLconnection) {
		if (canvas == null) throw new IllegalArgumentException("null canvas");
		
		getImage(httpURLconnection, canvas);
	}

	public void close() {
		/* nothing to do */
		if(bitmap != null) {
			if(!bitmap.isRecycled()){   
				bitmap.recycle();
		        System.gc();
			}
		}
	}

	public boolean saveImage(String savePath, String fileName) {

		//obtain the bitmap
		try {
			if(bitmap != null)
			{
				FileOutputStream fos = new FileOutputStream(savePath + "/" + fileName);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			}
		} catch (IOException e) {
			e.printStackTrace();

			return false;
		}
		
		return true;
	}
	
	public Bitmap getCaptureImage()
	{
		matrix = new Matrix();
		matrix.postRotate(90);
		if(bitmap == null){
			return null;
		}
		Bitmap tempBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return tempBmp;
	}

}
