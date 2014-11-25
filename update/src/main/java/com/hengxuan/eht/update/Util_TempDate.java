package com.hengxuan.eht.update;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Util_TempDate {
    /** 获取读写权限 */
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("TempData",
                Activity.MODE_PRIVATE);
    }

    /** 保存上次登錄時間 */
    public static void SaveLastUpdatedDate(Context context,String lastUpdatedDate) {
        // 如果注册成功，保存用户输入的帐号，密码，并打开登录界面
        Editor mEditor = getPreferences(context).edit();
        mEditor.putString("LastUpdatedDate", lastUpdatedDate);
        mEditor.commit(); // 将用户输入的用户名，密码保存
    }

    /** 獲取上次登錄時間 */
    public static String getLastUpdatedDate(Context context) {
        return getPreferences(context).getString("LastUpdatedDate", null);
    }

    /** 保存时间范围 */
    public static void SaveTimeLimitOptions(Context context,String timeLimitOptions) {
        // 如果注册成功，保存用户输入的帐号，密码，并打开登录界面
        Editor mEditor = getPreferences(context).edit();
        mEditor.putString("TimeLimitOptions", timeLimitOptions);
        mEditor.commit(); // 将用户输入的用户名，密码保存
    }

    /** 獲取时间范围 */
    public static String getTimeLimitOptions(Context context) {
        return getPreferences(context).getString("TimeLimitOptions", null);
    }

    /** 保存上次显示升级提示框时间 */
    public static void SaveServerTime(Context context,long serverTime) {
        // 如果注册成功，保存用户输入的帐号，密码，并打开登录界面
        Editor mEditor = getPreferences(context).edit();
        mEditor.putLong("ServerTime", serverTime);
        mEditor.commit(); // 将用户输入的用户名，密码保存
    }

    /** 獲取上次显示升级提示框时间 */
    public static long getServerTime(Context context) {
        return getPreferences(context).getLong("ServerTime", 0);
    }

    /***
     * 保存历史记录
     *
     * @param fialeName
     * @param hashMap
     * @param mList
     */
    public void SaveHistory(Context context,String fialeName, HashMap<String, Object> hashMap,
                            List<HashMap<String, Object>> mList) {
        if (mList == null) {
            mList = new ArrayList<HashMap<String, Object>>();
        }
        mList.remove(hashMap);
        mList.add(hashMap);
        if (mList.size() > 10) {
            mList.remove(0);
        }
        ObjectOutputStream out = null;
        try {
            FileOutputStream os = context.openFileOutput(
                    fialeName, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(os);
            out.writeObject(mList);
        } catch (Exception e) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }
            }
        }
    }

    /****
     * 获取历史记录
     *
     * @param fialeName
     * @return
     */
    @SuppressWarnings("unchecked")
    ArrayList<HashMap<String, Object>> getHistory(Context context,String fialeName) {
        ObjectInputStream in = null;
        try {
            InputStream is = context.openFileInput(
                    fialeName);
            in = new ObjectInputStream(is);
            return (ArrayList<HashMap<String, Object>>) in.readObject();
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                }
            }
        }
        return null;
    }
}
