package com.hengxuan.eht.massager2;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2014/11/4.
 */
public class ImageListAdapter extends BaseAdapter {
        //must be Activity
        Context mContext;
        AssetManager am;
        String[] files;
        String path;
        int count;
        int screenW;
        Activity mActivity;
        int listWidth;

        public ImageListAdapter(Context context, int w, String path) {
            // TODO Auto-generated constructor stub
            this.path = path;
            mActivity = (Activity)context;
            listWidth = w;
            am = context.getResources().getAssets();
            try {
                files = am.list(path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            count = files.length;
            screenW = mActivity.getWindowManager().getDefaultDisplay().getWidth();
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return count;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressWarnings("static-access")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if(convertView == null){
//                convertView = new ImageView(getActivity());
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.imageview, parent, false);
            }else{

            }
//            Bitmap bmp = getImageFromAssetsFile(path + File.separator + files[position]);
//            int imageWidth = bmp.getWidth();
//            int imageHeight = bmp.getHeight();
//            Matrix m = new Matrix();
//            m.postScale(((float)listWidth)/imageWidth, ((float)listWidth)/imageWidth);
//            Bitmap bitmap = bmp.createBitmap(bmp, 0, 0, imageWidth, imageHeight, m, true);
//            int w = bitmap.getWidth();
//            holder.iv.setImageBitmap(bmp);

            //异步加载图片
            loadBitmap(path + File.separator + files[position], (ImageView)convertView);
            return convertView;
        }

        private void loadBitmap(String path, ImageView imageView) {
            if(cancelPotentialWork(path, imageView)){
                BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                Holder holder = new Holder(task);
                imageView.setTag(holder);
                task.execute(path);
            }
        }

        class Holder{
            private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
            Holder(BitmapWorkerTask task){
                bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(task);
            }
            public BitmapWorkerTask getBitmapWorkerTask(){
                return bitmapWorkerTaskReference.get();
            }
        }

        private boolean cancelPotentialWork(String data,ImageView imageView) {
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
            if(bitmapWorkerTask != null){
                final String bitmapdata = bitmapWorkerTask.data;
                if(bitmapdata == null || !bitmapdata.equals(data)){
                    bitmapWorkerTask.cancel(true);
                }else{
                    return false;
                }
            }
            return true;
        }
        public  BitmapWorkerTask getBitmapWorkerTask(ImageView iv){
            if(iv != null){
                if(iv.getTag() instanceof Holder){
                    return ((Holder)iv.getTag()).getBitmapWorkerTask();
                }
            }
            return null;
        }
        private Bitmap getImageFromAssetsFile(String filePath){
            Bitmap bitmap = null;
            AssetManager am = mActivity.getResources().getAssets();
            try {
                InputStream is = am.open(filePath);
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return bitmap;
        }

        private class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap> {
            private final WeakReference<ImageView> imageViewReference;
            //数据源
            private String data;

            public BitmapWorkerTask(ImageView imageView) {
                imageViewReference = new WeakReference<ImageView>(imageView);
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                data = strings[0];
                Bitmap bmp = getImageFromAssetsFile(data);
                int imageWidth = bmp.getWidth();
                int imageHeight = bmp.getHeight();
                Matrix m = new Matrix();
                m.postScale(((float)listWidth)/imageWidth, ((float)listWidth)/imageWidth);
                Bitmap bitmap = bmp.createBitmap(bmp, 0, 0, imageWidth, imageHeight, m, true);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(isCancelled()){
                    bitmap = null;
                }
                if(imageViewReference !=null && bitmap != null){
                    ImageView imageView = imageViewReference.get();
                    final BitmapWorkerTask task = getBitmapWorkerTask(imageView);
                    if(task == this && imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
}
