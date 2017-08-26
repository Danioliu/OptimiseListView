package com.demo.listview.optimiselistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/8/26.
 */

public class ListViewAdapterGetImageByThread extends BaseAdapter implements AbsListView.OnScrollListener {
    //布局填充器
    private LayoutInflater mInflater = null;
    private List<ItemBean> mItemBeen = null;
    private ImageLoaderByAsynTask imageLoader;
    private int mStart;
    private int mEnd;
    public static String [] urls;

    public ListViewAdapterGetImageByThread(Context context, List<ItemBean> data, ListView listView) {
        mInflater = LayoutInflater.from(context);
        mItemBeen = data;
        imageLoader = new ImageLoaderByAsynTask(listView);
        for (int i=0;i<data.size();i++){
            ItemBean itemBean = data.get(i);
            urls[i]=itemBean.icon;
        }
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mItemBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_layout, null);
            holder = new ViewHolder();
            holder.mPic_iv = (ImageView) convertView.findViewById(R.id.pic_iv);
            holder.mTittle_tv = (TextView) convertView.findViewById(R.id.tittle_tv);
            holder.mContent_tv = (TextView) convertView.findViewById(R.id.content_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ItemBean itemBean = mItemBeen.get(position);
        holder.mTittle_tv.setText(itemBean.tittle);
        holder.mContent_tv.setText(itemBean.content);
        holder.mPic_iv.setImageResource(R.mipmap.ic_launcher);
        String url = itemBean.icon;
        holder.mPic_iv.setTag(url);
        imageLoader.showImageByAsynTsk(holder.mPic_iv, url);
        return convertView;
    }

    /**
     * 为提高效率，解决滑动时候的卡顿问题，要在listview的滑动监听中做一些处理
     * 1.滑动时候不进行加载，取消所有的下载任务
     * 2.在滑动停止的时候再进行图片的加载
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
    class ViewHolder {
        ImageView mPic_iv;
        TextView mTittle_tv, mContent_tv;
    }

}
