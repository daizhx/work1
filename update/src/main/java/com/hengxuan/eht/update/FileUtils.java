package com.hengxuan.eht.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;


public class FileUtils {
	private int totalSize = 0;
	private int currentSize = 0;
	private String SDPATH;
	private String pathName;
	public static String SD_CACHEDIR_NOFREE = "SD卡和手机存储空间不足升级失败,退出更新？";
	public static String CACHEDIR_NOFREE = "存储空间不足升级失败,退出更新？";
	public static String FREE = "SUCEESS";
	public static String ERROR = "error";

	public String getPathName() {
		return pathName;
	}

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtils() {
		if (Util_File.isSDCardExit()) {
			SDPATH = Environment.getExternalStorageDirectory().toString();
		} else {
			SDPATH = Environment.getDataDirectory().toString();
//			SDPATH = AppAplication.getInstance().getCacheDir().getAbsolutePath();
		}
	}

	/**
	 * 
	 * 
	 * @throws java.io.IOException
	 */
	public File creatSDFile(String path, String fileName) throws IOException {
		File file = new File(SDPATH + File.separator + fileName);
		file.createNewFile();
		pathName = file.getAbsolutePath();
		return file;
	}

	/**
	 * 
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	/**
	 * 
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	public interface DownloadListener {
		void updateProgress(int totalSize, int currentSize);
	}

	public String write2SDFromInput(String path, String fileName, HttpURLConnection conn, DownloadListener downloadListener) throws IOException {
		InputStream input = null;
		String flag = FREE;
		try {
			input = conn.getInputStream();
			totalSize = conn.getContentLength();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (totalSize > getFreeSizeSD()) {
				if (totalSize > getFreeSize()) {
					flag = SD_CACHEDIR_NOFREE;
				}
			}
		} else {
			if (totalSize > getFreeSize()) {
				flag = CACHEDIR_NOFREE;
			}
		}
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path, fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int temp = 0;
			while ((temp = input.read(buffer)) != -1) {
				currentSize += temp;
				output.write(buffer, 0, temp);
				if (downloadListener != null) {
					downloadListener.updateProgress(totalSize, currentSize);
				}
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			flag = ERROR;
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public int totalFileSize() {
		return totalSize;
	}

	public long getFreeSizeSD() {
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long freeBlocks = sf.getAvailableBlocks();
		return (freeBlocks * blockSize);
	}

	public long getFreeSize() {
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();
		return (availCount * blockSize);// bit
	}
}