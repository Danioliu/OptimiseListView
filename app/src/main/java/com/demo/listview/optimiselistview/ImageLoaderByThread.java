package com.demo.listview.optimiselistview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/8/26.
 * 利用Thread+handler实现图片的异步加载
 */

public class ImageLoaderByThread {
    private String mUrl = "";
    private ImageView mImageView = null;
    //使用LruCash缓存图片，不必重复进行网络加载图片
    // 使用内存空间换取效率
    private LruCache<String,Bitmap> bitmapLruCache=null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            if (bitmap != null) {
                if (mImageView.getTag().equals(mUrl)) {
                    //将bitmp添加到内存当中
                    bitmapLruCache.put(mUrl,bitmap);
                    mImageView.setImageBitmap(bitmap);
                }
            }
        }
    };
   public ImageLoaderByThread(){
       int memorry= (int) Runtime.getRuntime().maxMemory();
       bitmapLruCache=new LruCache(memorry/4);
   }

    public void showImageByThread(ImageView imageView, String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromUrl(mUrl);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }
    public void showImage(ImageView imageView, String url) {
        mImageView = imageView;
        mUrl = url;
        //判断内存当中是否含有该bitmap
        Bitmap bitmap = bitmapLruCache.get(url);
        if(bitmap==null){
            showImageByThread(imageView,url);
        }else {
            imageView.setImageBitmap(bitmap);
        }
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


}
