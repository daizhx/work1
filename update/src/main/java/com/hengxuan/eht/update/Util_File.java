package com.hengxuan.eht.update;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * 文件保存在系统或SDCard，设置："Config.bFileOperationInSDCard;"+
 * "Config.fileOperationInSDCardPath;" 注：调试时，建议保存在SDCard，因为真机系统文件需要root才能看到；
 */
public class Util_File {
    private static String fileSavePath = null;

    private static String appFilePathInSDCard = null; // 当前应用所有文件存储的SDCard绝对路径（前提：如果文件系统定在SD卡，否则null）

    private static boolean bFileOperationInSDCard = true;
    private static String fileOperationInSDCardPath = "eht";

    // /mnt/sdcard

    /**
     * 注：务必在MyApplication里调用
     *
     * 校对：如config设置文件保存在SD卡，但SD卡不存在情况下，强置bFileOperationInSDCard = false;
     */
//    public static void checkFileOperationInSDCard() {
//        if (Config.bFileOperationInSDCard && !isSDCardExit()) {
//            Config.bFileOperationInSDCard = false;
//        }
//
//        if (Config.bFileOperationInSDCard && appFilePathInSDCard == null) {
//            // 组织好路径
//            File file = Environment.getExternalStorageDirectory();// SD card
//            // 的root
//            // path
//            appFilePathInSDCard = Util_G.strAddStr(file.getPath(), Config.fileOperationInSDCardPath);
//            debug("appFilePathInSDCard:" + appFilePathInSDCard);
//        }
//    }

    public static void checkFileOperationInSDCard(){
        if(!isSDCardExit()){
            bFileOperationInSDCard = false;
        }
        if(bFileOperationInSDCard && appFilePathInSDCard == null){
            File file = Environment.getExternalStorageDirectory();
            appFilePathInSDCard = strAddStr(file.getPath(), fileOperationInSDCardPath);
        }
    }

    /**
     * 检查SD卡是否存在
     */
    public static boolean isSDCardExit() {
        return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 手机系统文件：【data】
     *
     * @param fileName
     *            ：文件名，并非路径
     * @param MODE
     * @return
     */
    private static FileOutputStream getStreamFileOutput(Context context,String fileName, int MODE) {
        try {
            return context.openFileOutput(fileName, MODE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 手机系统文件：【data】
     *
     * @param fileName
     *            ：文件名，并非路径
     * @return
     */
    private static FileInputStream getSystemFileInputStream(Context context,String fileName) {
        try {
            return context.openFileInput(fileName);
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteFile(String fileName) {
        FileOutputStream outStream = null;
        try {
            File path = new File(appFilePathInSDCard);
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(strAddStr(appFilePathInSDCard, fileName));
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (Exception e) {

        }

    }

    private static String strAddStr(String appFilePathInSDCard, String fileName) {
        return appFilePathInSDCard + File.separator + fileName;
    }

    private static File getCurProjectFileInSDCard(String fileName) {

        try {
            File path = new File(appFilePathInSDCard);
            if (!path.exists()) {
                path.mkdirs();
            }

            File file = new File(strAddStr(appFilePathInSDCard, fileName));
            fileSavePath = null;
            fileSavePath = strAddStr(appFilePathInSDCard, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFileSavePath() {

        return fileSavePath;
    }

    /**
     * 手机SD卡文件：【SD CARD】
     *
     * @param fileName
     *            ：文件名，并非路径
     * @param MODE
     * @return
     */
    private static FileOutputStream getSDCardFileOutput(String fileName, int MODE) {
        try {
            return new FileOutputStream(getCurProjectFileInSDCard(fileName), MODE == Context.MODE_APPEND ? true : false);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 手机SD卡文件：【SD CARD】
     *
     * @param fileName
     *            ：文件名，并非路径
     * @return
     */
    private static FileInputStream getSDCardFileInputStream(String fileName) {

        try {
            return new FileInputStream(getCurProjectFileInSDCard(fileName));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param data
     *            : byte[]
     * @param fileName
     *            : 文件名称，并不是文件路径，不能包含路径分隔符“/” ，如果文件不存在，Android
     *            会自动创建它。创建的文件保存在/data/data/<package name>/files目录，真机需要root才能看到；
     * @param MODE
     *            : Context.MODE_PRIVATE = 0 Context.MODE_APPEND = 32768 :
     *            系统、SDCard 都适用； Context.MODE_WORLD_READABLE = 1
     *            Context.MODE_WORLD_WRITEABLE = 2
     */

    public static boolean writeFile(Context context, byte[] data, String fileName, int MODE) {

        if (data == null) {
            return false;
        }
        FileOutputStream outStream = null;
        try {
            if (bFileOperationInSDCard) {
                // 判断文件是否保存在sd卡
                outStream = getSDCardFileOutput(fileName, MODE);
            } else {
                outStream = getStreamFileOutput(context,fileName, MODE);
            }
            outStream.write(data);

            // 保存数据加入换行
            String enter = " \n ";
            byte[] bytes = enter.getBytes();
            // byte[] bytes=new byte[]();
            outStream.write(bytes);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                    outStream = null;
                }
            } catch (Exception e) {
            }
        }
        return true;
    }

    public static boolean writeFile(Context context,byte[] data, String fileName, int MODE, boolean lined) {

        if (data == null) {
            return false;
        }
        FileOutputStream outStream = null;
        try {
            if (bFileOperationInSDCard) {
                // 判断文件是否保存在sd卡
                outStream = getSDCardFileOutput(fileName, MODE);
            } else {
                outStream = getStreamFileOutput(context,fileName, MODE);
            }
            outStream.write(data);

            // 保存数据加入换行
			/*
			 * String enter = " \n "; byte[] bytes = enter.getBytes(); //byte[]
			 * bytes=new byte[](); outStream.write(bytes);
			 */
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                    outStream = null;
                }
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * @param data
     *            : String
     * @param fileName
     *            : 文件名称，不能包含路径分隔符“/” ，如果文件不存在，Android
     *            会自动创建它。创建的文件保存在/data/data/<package name>/files目录
     * @param MODE
     *            : Context.MODE_PRIVATE = 0 Context.MODE_APPEND = 32768
     *            Context.MODE_WORLD_READABLE = 1 Context.MODE_WORLD_WRITEABLE =
     *            2
     */
    public static boolean writeFile(Context context,String data, String fileName, int MODE) {
        // Util_G.debug("writeFile(), fileName="+fileName+", "+"content="+data);
        // 写入文件到sd卡
        return writeFile(context,utf8Encode(data), fileName, MODE);

    }

    private static byte[] utf8Encode(String data) {
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param fileName
     *            ：文件名，并非路径
     * @return
     */
    public static byte[] readFile(Context context,String fileName) {
        byte[] d = null;
        FileInputStream inStream = null;
        try {
            if (bFileOperationInSDCard) {
                inStream = getSDCardFileInputStream(fileName);
            } else {
                inStream = getSystemFileInputStream(context, fileName);
            }

            d = getByteArrayFromInputstream(inStream, -1);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                    inStream = null;
                }
            } catch (Exception e) {
            }
        }
        return d;
    }

    private static byte[] getByteArrayFromInputstream(FileInputStream inStream, int i) {
        //TODO
        return null;
    }

    /**
     * UTF-8格式
     *
     * @param fileName
     *            ：文件名，并非路径
     * @return
     */
    public static String readFile2String(Context context,String fileName) {
        return utf8Decode(readFile(context,fileName));
    }

    private static String utf8Decode(byte[] bytes) {
        try {
            return new String(bytes,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 保存多行数据
     */

    public static void saveMutilLine(Context context,String data, String filename) {
        String olddata = readFile3String(context,filename);
        StringBuilder sb = new StringBuilder(olddata);
        sb.append(data);
        writeFile(context, sb.toString(), filename, 0);
    }

    /**
     * 每次读取一行
     */
    public static LinkedList<String> readSingleLine(Context context,String fileName) throws IOException, NullPointerException {

        InputStream inStream = null;

        if (bFileOperationInSDCard) {
            inStream = getSDCardFileInputStream(fileName);
        } else {
            inStream = getSystemFileInputStream(context,fileName);
        }
        LinkedList<String> linked = new LinkedList<String>();
        if (inStream.available() != -1) {
            String text = null;
            BufferedReader reader = null;
            try {

                // BufferedReader reader=new BufferedReader(new
                // FileReader(file));

                reader = new BufferedReader(new InputStreamReader(inStream));
                while ((text = reader.readLine()) != null) {
                    linked.add(text);
                }
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } finally {

                try {
                    if (inStream != null) {
                        inStream.close();
                        reader.close();
                    }
                } catch (Exception e) {
                }
            }

        }
        return linked;

    }

    public static String readFile3String(Context context,String fileName) {
        byte[] data = null;
        InputStream inStream = null;
        try {
            if (bFileOperationInSDCard) {
                inStream = getSDCardFileInputStream(fileName);
            } else {
                inStream = getSystemFileInputStream(context, fileName);
            }
            data = new byte[inStream.available()];
            BufferedInputStream bfInputStream = new BufferedInputStream(inStream);
            bfInputStream.read(data);
            bfInputStream.close();
            inStream.close();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                    inStream = null;
                }
            } catch (Exception e) {
            }
        }
        return new String(data);
    }

    private static void debug(String str) {
        System.out.println(str);
    }

    /*** --------保存图片到sdcard------------------- */
    /**
     * UTF-8格式
     *
     * @param bitName
     *            ：文件名，并非路径,不包含".jpg"
     * @param bitmap
     *            要保存的位图字节
     * @param MODE
     *            保存模式
     * @return
     */
    public static void saveMyBitmap(Context context,String bitName, Bitmap bitmap, int MODE) throws IOException {

        // Bitmap bm1 = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        bitName = bitName.trim();
        FileOutputStream outStream = null;
        if (bFileOperationInSDCard) {
            outStream = getSDCardFileOutput(bitName, MODE);
        } else {
            outStream = getStreamFileOutput(context,bitName, MODE);
        }
        // outStream.write(bitmap);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream); // 目前不使用
        try {
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/*	*//** --------------------- 自定义 文件存储功能 ------------------------- **/

    /**
     * @param path
     * @param content
     * @throws IOException
     */
    public static void saveSystemInfo(String path, String content) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path, false);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 读取文件
     *
     * @return
     */
    public static String readSystemInfo(String path) {
        byte[] buf = new byte[1024];
        String str = null;
        try {
            FileInputStream inputStream = new FileInputStream(path);
            int len = inputStream.read(buf); // 从流中读取内容
            str = new String(buf, 0, len);
            inputStream.close();
        } catch (FileNotFoundException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 检查文件是否存在,主要用于判断有数据的文件
     *
     * @param fileName
     *            文件名
     * @return
     */
    public static boolean isFileExists(String fileName) {
        File files = new File(fileName);
        if (files.exists() && files.length() > 0) {
            return true;
        }
        return false;
    }

	/*
	*//**
     * 检查文件是否存在
     *
     * @param fileName
     *            文件名
     * @return
     */
    public static boolean isFiles(String fileName) {
        File files = new File(fileName);
        if (files.exists()) {
            return true;
        }
        return false;
    }

}