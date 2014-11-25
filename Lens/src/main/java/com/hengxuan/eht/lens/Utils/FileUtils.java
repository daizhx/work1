package com.hengxuan.eht.lens.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * android系统中文件操作
 * Created by daizhx on 2014/11/5.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 打开一个文件用于写入数据,如果存在外部存储，则把path对应的文件写在外部存储根目录下，否则写到手机内部存储当中
     * @param context
     * @param path
     * @param append 是否以追加的方式打开文件
     * @return
     * @throws FileNotFoundException eg.当path为已存在目录的时候会抛出异常
     */
    public static FileOutputStream openFile(Context context,String path,boolean append) throws FileNotFoundException {
        String filePath;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + path;
        }else{
            //getFilesDir获取的文件系统路径为/data/data/包名/files
            filePath = context.getFilesDir().toString()+ File.separator + path;
        }
        File file = new File(filePath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try {
            return new FileOutputStream(file, append);
        } catch (FileNotFoundException e) {
            throw e;
        }
    }



    /**
     * 判断文件是否已存在sd卡中，没有sd卡的情况下，判断是否存在于s手机内存中
     * @param context
     * @param path
     * @return
     */
    public static boolean isFileExist(Context context, String path){
        String filePath;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + path;
        }else{
            //getFilesDir获取的文件系统路径为/data/data/包名/files
            filePath = context.getFilesDir().toString()+ File.separator + path;
        }
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 获取文件绝对路径
     * @param context
     * @param path 相对路径
     * @return
     */
    public static String getFileAbsolutePath(Context context, String path){
        String filePath;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + path;
        }else{
            //getFilesDir获取的文件系统路径为/data/data/包名/files
            filePath = context.getFilesDir().toString()+ File.separator + path;
        }
        return filePath;
    }
}
