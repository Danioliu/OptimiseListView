package com.demo.listview.optimiselistview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/8/26.
 * 利用AsynTask实现图片的异步加载
 */

public class ImageLoaderByAsynTask {
    private String mUrl = "";
    private ImageView mImageView = null;
    //使用LruCash缓存图片，不必重复进行网络加载图片
    // 使用内存空间换取效率
    private LruCache<String, Bitmap> bitmapLruCache = null;
    private ListView mListView;
    private Set<MyAsynTsk> mTask;

    public ImageLoaderByAsynTask(ListView listView) {
        mListView = listView;
        mTask = new HashSet<>();
        int memorry = (int) Runtime.getRuntime().maxMemory();
        bitmapLruCache = new LruCache(memorry / 4);
    }


    private Bitmap getBitmapFromUrl(String urlString) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            //释放资源
            urlConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //释放资源
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void showImageByAsynTsk(ImageView imageView, String url) {
        mUrl = url;
        //判断内存当中是否含有该bitmap
        Bitmap bitmap = bitmapLruCache.get(url);
//        if (bitmap == null) {
//            imageView.setImageBitmap(bitmap);
//        } else {
//            imageView.setImageBitmap(bitmap);
//        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void loadImage(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = ListViewAdapterGetImageByAsynTask.urls[i];
            //判断内存当中是否含有该bitmap
            Bitmap bitmap = bitmapLruCache.get(url);
            if (bitmap == null) {
                MyAsynTsk myAsynTsk = new MyAsynTsk(url);
                myAsynTsk.execute(url);
                mTask.add(myAsynTsk);
            } else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 取消所有正在运行的任务
     */
    public void cncelAllTask() {
        if (mTask != null) {
            for (MyAsynTsk task : mTask) {
                task.cancel(false);
            }
        }
    }

    class MyAsynTsk extends AsyncTask<String, Void, Bitmap> {
        private String mUrl = "";


        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromUrl(params[0]);
        }

        public MyAsynTsk(String url) {
            mUrl = url;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && imageView.getTag() == mUrl) {
                imageView.setImageBitmap(bitmap);
                //将bitmp添加到内存当中
                bitmapLruCache.put(mUrl, bitmap);
            }
            mTask.remove(this);
        }
    }
}
