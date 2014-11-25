package com.hengxuan.eht.update;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/***
 * 升级的dialog
 *
 * @author fhsh
 *
 */
public class UpdateDialog extends DialogFragment {
    int updateStatus = -1; // 更新状态，0无更新，1小版本可选更新，2中大版本必需更新，每个接囗都返回
    String updateMessage = "";
    long serverTime = 0;
    String updateUrl = "";
    final static String DIALOG_TAG = "UpdateDialog";
    private String pathName;
    /**
     * Create a new instance of UpdateDialog, providing "num" as an argument.
     */
    private Button btn01, btn02;
    private ImageView iv_icon;
    private ImageView imageView_01;
    private TextView tv_title, tv_per;
    TextView customer_reply_msg;
    private ProgressBar mBar;
    private boolean isFrist = true;
    private GetUpdateTask mGetUpdateTask;
    private boolean downloadWeb = false;
    int progress1 = 0, progress2 = -1;
    Timer timer;
    /***
     * 单例模式
     */
    private static UpdateDialog instance = null;

    public static UpdateDialog newInstance() {
        if (instance == null) {
            synchronized (UpdateDialog.class) {
                if (instance == null)
                    instance = new UpdateDialog();
            }
        }
        return instance;
    }

    // public static UpdateDialog newInstance() {
    // UpdateDialog f = new UpdateDialog();
    // // if (f != null && f.isVisible()) {
    // // f.dismiss();
    // // }
    // return f;
    // }

    public void show(FragmentActivity transaction, String result) {
        if (null != result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                updateStatus = jsonObject.getInt("updateFlag");
                if (jsonObject.has("updateDetail")) {
                    updateMessage = jsonObject.getString("updateDetail");
                    serverTime = System.currentTimeMillis();
                    updateUrl = jsonObject.getString("softUrl");
                    Log.i("=========", updateUrl);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        updateStatus = 1;
        updateMessage = "1231341232515";
        updateUrl = "http://eht.hk/eht.apk";

        long timeLast = Util_TempDate.getServerTime(getActivity());
        long tmpLocalMilli = new Date().getTime() / 1000;
        if (updateStatus != 0 && updateStatus != -1 && updateStatus == 1 && (tmpLocalMilli - timeLast > 24 * 60 * 60)) {
            Fragment arg0 = transaction.getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
            if (arg0 != null) {
                try {
                    transaction.getSupportFragmentManager().beginTransaction().remove(arg0);
                } catch (Exception e) { // TODO: handle exception
                    e.printStackTrace();
                }
            }
            show(transaction.getSupportFragmentManager(), DIALOG_TAG);
        } else if (updateStatus != 0 && updateStatus != -1 && updateStatus == 2) {
            Fragment arg0 = transaction.getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
            if (arg0 != null) {
                try {
                    transaction.getSupportFragmentManager().beginTransaction().remove(arg0);
                } catch (Exception e) { // TODO: handle exception
                    e.printStackTrace();
                }
            }
            show(transaction.getSupportFragmentManager(), DIALOG_TAG);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//        setStyle(R.style.AliDialog, R.style.AliDialog);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.alert_dialog_update, container, false);

        btn01 = (Button) view.findViewById(R.id.alert_dialog_btn01);
        btn02 = (Button) view.findViewById(R.id.alert_dialog_btn02);
        iv_icon = (ImageView) view.findViewById(R.id.alert_dialog_iv_icon);
        imageView_01 = (ImageView) view.findViewById(R.id.id_imageView_01);
        tv_title = (TextView) view.findViewById(R.id.alert_dialog_tv_title);
        customer_reply_msg = (TextView) view.findViewById(R.id.customer_reply_msg);
        tv_per = (TextView) view.findViewById(R.id.customer_reply_per);
        customer_reply_msg.setText(updateMessage);
        tv_title.setText(R.string.app_name);
        mBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        btn01.setOnClickListener(clickListener);
        btn02.setOnClickListener(clickListener);
        if (updateStatus == 2) {
            btn02.setText(android.R.string.cancel);
        }
        return view;
    }

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int i = v.getId();
            if (i == R.id.alert_dialog_btn01) {
                Util_TempDate.SaveServerTime(getActivity(), serverTime);
                customer_reply_msg.setVisibility(View.GONE);

                if (updateStatus == 1) {
                    Intent intent = new Intent(getActivity(), UpdateVersionService.class);
                    intent.putExtra("apkPath", updateUrl);
                    getActivity().startService(intent);
                    dismiss();
                } else
                    // if (updateStatus == 2 || updateStatus == 1) {
                    if (updateStatus == 2) {
                        mBar.setVisibility(View.VISIBLE);
                        tv_per.setVisibility(View.VISIBLE);
                        String name = updateUrl.substring(updateUrl.lastIndexOf("/") + 1);
                        mGetUpdateTask.execute(updateUrl, name);
                        btn01.setVisibility(View.INVISIBLE);
                        btn02.setVisibility(View.INVISIBLE);
                        imageView_01.setVisibility(View.INVISIBLE);

                    } else {
                        dismiss();
                    }


            } else if (i == R.id.alert_dialog_btn02) {
                Util_TempDate.SaveServerTime(getActivity(),serverTime);
                dismiss();
                if (updateStatus == 2 && !downloadWeb) {
//                    AppManager.getAppManager().AppExit(getActivity(), false);
                } else if (updateStatus == 2 && downloadWeb) {
//                    String downloadUrl = ConstFuncId.DOWNLOADURL;
                    String downloadUrl = "http://eht.hk/downList.asp";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    getActivity().startActivity(intent);
                    timer.cancel();
//                    AppManager.getAppManager().AppExit(getActivity(), false);
                }

            } else {
            }
        }
    };

    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        setCancelable(false);
        mGetUpdateTask = new GetUpdateTask();
    };

    class GetUpdateTask extends AsyncTask<String, Integer, String> {
        FileUtils fileUitl = new FileUtils();
        HttpURLConnection conn = null;
        String flag = null;

        @Override
        protected String doInBackground(String... params) {
            publishProgress(0);
            URL urlStr = null;
            if (params[0] != null) {
                try {
                    urlStr = new URL(params[0]);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    conn = (HttpURLConnection) urlStr.openConnection();
                    conn.setConnectTimeout(40 * 1000);
                    conn.setReadTimeout(40 * 1000);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    flag = FileUtils.ERROR;
                }
                try {
                    flag = fileUitl.write2SDFromInput("apk", params[1], conn, new FileUtils.DownloadListener() {

                        @Override
                        public void updateProgress(int totalSize, int currentSize) {
                            // TODO Auto-generated method stub
                            int index = currentSize * 100 / totalSize;
                            publishProgress(index);
                        }
                    });
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    flag = FileUtils.ERROR;
                }
            } else {
                return null;
            }

            return flag;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (result.equals(FileUtils.FREE)) {
                    pathName = fileUitl.getPathName();
                    update();
                    timer.cancel();
                    if (conn != null) {
                        conn.disconnect();
                    }
                } else if (result.equals(FileUtils.ERROR)) {

                } else {
                    mBar.setVisibility(View.INVISIBLE);
                    tv_per.setText(result);
                    // btn01.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layoutParams = btn02.getLayoutParams();
                    layoutParams.width = LayoutParams.MATCH_PARENT;
                    btn02.setGravity(Gravity.CENTER);
                    btn02.setLayoutParams(layoutParams);
                    btn02.setVisibility(View.VISIBLE);
                    btn02.setText(R.string.konw_info);
                    imageView_01.setVisibility(View.VISIBLE);
                    timer.cancel();
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mBar != null) {
                mBar.setProgress(0);
                tv_per.setText("当前下载了：" + 0 + "%");
            }
            initTimer();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mBar.setProgress(values[0]);
            tv_per.setText("当前下载了：" + values[0] + "%");
        }

        public void update() throws Exception {
            // chmod 755 /* 755 权限是对apk自身应用具有所有权限， 对组和其他用户具有读和执行权限 */
            if (!Util_File.isSDCardExit() && fileUitl.totalFileSize() > fileUitl.getFreeSizeSD()) {
                String cmd = "chmod 777 " + pathName;
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Intent it = new Intent(Intent.ACTION_VIEW);
            File file = new File(pathName);
            it.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        }

        /**
         * 第二个参数的意思是，当你调用该方法后，该方法必然会调用 TimerTask 类 TimerTask 类 中的 run()
         * 方法，这个参数就是这两者之间的差值，转换成汉语的意思就是说，用户调用 schedule() 方法后，要等待这么长的时间才可以第一次执行
         * run() 方法。 第三个参数的意思就是，第一次调用之后，从第二次开始每隔多长的时间调用一次 run() 方法。
         * schedule(TimerTask task, long delay)只执行一次，schedule(TimerTask task,
         * long delay, long period)才是重复的执行。
         */
        private void initTimer() {

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putInt("progress", mBar.getProgress());
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }, 0, 10000);
        }
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {

                case 1:
                    if (isFrist) {
                        progress1 = message.getData().getInt("progress");
                        isFrist = false;
                    } else {
                        progress2 = message.getData().getInt("progress");
                        isFrist = true;
                    }
                    if (progress1 == progress2) {
                        downloadWeb = true;
                        mGetUpdateTask.cancel(true);
                        mBar.setVisibility(View.INVISIBLE);
                        tv_per.setText("服务器忙，请到官网下载更新");
                        // btn01.setVisibility(View.VISIBLE);
                        btn02.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams layoutParams = btn02.getLayoutParams();
                        layoutParams.width = LayoutParams.MATCH_PARENT;
                        btn02.setGravity(Gravity.CENTER);
                        btn02.setLayoutParams(layoutParams);
                        btn02.setText(R.string.konw_info);
                        imageView_01.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
}
