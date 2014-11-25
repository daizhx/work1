package com.hengxuan.eht.logger;

import android.os.Environment;
import android.text.format.DateFormat;

import com.hengxuan.eht.Http.utils.Base64;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class Log {
	
	public static boolean V;
	public static boolean D = true;
	public static boolean I = true;
	public static boolean W;
	public static boolean E = true;

	
	public static void d(String paramString2) {
		if (D) {
			android.util.Log.d("eht", paramString2);
		}
	}
	public static void d(String paramString1, String paramString2) {
		if (D) {
			android.util.Log.d(paramString1, paramString2);
		}
	}

	public static void d(String paramString1, String paramString2,
			Throwable paramThrowable) {
		if (D) {
			// android.util.Log.d(paramString1, paramString2, paramThrowable);
			System.out.println(paramString1 + "--" + paramString2 + "throwable info: " + paramThrowable.getMessage());
			
		}
	}
	
	public static void i(String s1,String s2){
		if(I){
			android.util.Log.i(s1, s2);
		}
	}
	
	public static String getStackTraceString(Throwable e){
		return android.util.Log.getStackTraceString(e);
	}
	
	public static void e(String msg){
		e("eht", msg);
	}
	public static void e(String tag, String msg){
		if(E){
			android.util.Log.e(tag, msg);
		}
	}

    public static void writeLogToSd(String msg){
        long time = System.currentTimeMillis();
        DateFormat df = new DateFormat();
        CharSequence timeStr = df.format("yyyy MM dd hh:mm:ss", time);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = null;
            try {
                path = Environment.getExternalStorageDirectory().getCanonicalPath() + File.separator + "eht" + File.separator + "log";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            File file = new File(path);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(file, true));
                out.write(timeStr +" " +  msg + "\n");
            }catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else{
            //TODO 没有SD卡的情况写到文件系统中
        }
    }
}
