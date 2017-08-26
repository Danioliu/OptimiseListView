package com.demo.listview.optimiselistview;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private ListView mListView;
    private static final String URL = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        new myAsynTsk().execute(URL);
    }

    /**
     * 初始化数据
     */
    private void intiView() {
        mListView = (ListView) findViewById(R.id.listView_mian);
    }

    class myAsynTsk extends AsyncTask<String, Void, List<ItemBean>> {

        @Override
        protected List<ItemBean> doInBackground(String... params) {
            return getJsonFromUrl(params[0]);
        }

        @Override
        protected void onPostExecute(List<ItemBean> itemBeen) {
            super.onPostExecute(itemBeen);
            //通过多线程的方式获取数据
//            ListViewAdapterGetImageByThread adapter = new ListViewAdapterGetImageByThread(mContext,itemBeen,mListView);
            //通过AsynTask的方式获取数据
            ListViewAdapterGetImageByAsynTask adapter = new ListViewAdapterGetImageByAsynTask(mContext,itemBeen,mListView);
            mListView.setAdapter(adapter);
        }
    }

    private List<ItemBean> getJsonFromUrl(String url) {
        List<ItemBean> itemBeenList = new ArrayList<>();
        try {
            String jsonString = readStream(new URL(url).openStream());
            JSONObject object;
            ItemBean itemBean;
            object = new JSONObject(jsonString);
            JSONArray jsonArray = object.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                itemBean = new ItemBean();
                itemBean.icon = jsonObject.getString("picSmall");
                itemBean.tittle = jsonObject.getString("name");
                itemBean.content = jsonObject.getString("description");
                itemBeenList.add(itemBean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemBeenList;
    }

    private String readStream(InputStream is) {
        InputStreamReader isr;
        String result = "";
        String line = "";
        try {
            isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
