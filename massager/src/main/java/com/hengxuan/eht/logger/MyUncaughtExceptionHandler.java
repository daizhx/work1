package com.hengxuan.eht.logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import com.hengxuan.eht.utils.StatisticsReportUtil;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

    //报告错误信息给服务器
	private static StringBuffer errorDataBuffer = new StringBuffer();
	private Context context;
	private UncaughtExceptionHandler mOldUncaughtExceptionHandler;

	public MyUncaughtExceptionHandler(Context context1)
	{
		context = context1;
		mOldUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

    /**
     * 处理未捕捉的异常
     * @param thread
     * @param throwable
     * @return
     */
	private boolean myUncaughtException(Thread thread, Throwable throwable)
	{
        //TODO intent发送程序错误信息到后台服务器
        return handleException(throwable);
	}

    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false ，必须返回false交给系统处理，要不然会很麻烦
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        StackTraceElement[] arr = ex.getStackTrace();
        String report = ex.toString() + "\n\n";
        report += "--------------Stack trace----------\n\n";
        for(int i=0;i<arr.length;i++){
            report += "		" + arr[i].toString() + "\n";
        }
        report += "---------------------------------------\n\n";
        //if the exception was thrown in a background thread inside
        //AsyncTask,then the actual exception can be found with getCause
        report += "-------------cause-------------\n\n";
        Throwable cause = ex.getCause();
        if(cause != null){
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for(int i=0; i<arr.length ; i++){
                report += "     " + arr[i].toString() + "\n";
            }
        }
        report += "-------------cause end-------------\n\n";

//        try {
//            FileOutputStream fos = context.openFileOutput("stact.trace", Context.MODE_PRIVATE);
//            fos.write(report.getBytes());
//            fos.close();
//
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        //错误日志写到SD卡中
        Log.writeLogToSd(report);
        //TODO 上传到后台
        return false;
    }

	public static void resetErrorInfo(String s){
		errorDataBuffer.setLength(0);
		errorDataBuffer.append(s);
	}
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		if (!myUncaughtException(thread, throwable) && mOldUncaughtExceptionHandler != null)
		{
            //交给系统处理
			mOldUncaughtExceptionHandler.uncaughtException(thread, throwable);
		} else
		{
            //退出不了应用，会不断重启
//			Process.killProcess(Process.myPid());
//			System.exit(0);
		}		
	}

}
