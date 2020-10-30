package com.tang.chinesepoemv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CollectFragment extends Fragment implements Runnable{
    private String TAG = "CollectFragment";

    private ListView lv;

    private SimpleAdapter listItemAdapter;
    private ArrayList<HashMap<String, String>> listItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collect_fragment, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        lv = (ListView) getActivity().findViewById(R.id.collect_list);

        Thread thread = new Thread(this);
        thread.start();

        //lv绑定事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = lv.getItemAtPosition(position);
                HashMap<String, String> map = (HashMap<String, String>) itemAtPosition;
                String poemid = map.get("poem_id");

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("poemid", poemid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                //布局显示
                String[] data = (String[]) msg.obj;
                listItems = new ArrayList<HashMap<String, String>>();
                for(int i = 0;i<data.length;i++){
                    String[] splitdata = data[i].split("#");

                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("poem_id",splitdata[0]);
                    map.put("poem_title",splitdata[1]);
                    map.put("poem_content",splitdata[2]);
                    listItems.add(map);
//                    Log.i(TAG, "handleMessage: add the "+i+" item");
                }
                listItemAdapter = new SimpleAdapter(getActivity(),
                        listItems,
                        R.layout.poems_list_item,
                        new String[]{"poem_id","poem_title","poem_content"},
                        new int[]{R.id.poem_id, R.id.poem_title,R.id.poem_content}
                );
                lv.setAdapter(listItemAdapter);

                TextView emptv = (TextView) getActivity().findViewById(R.id.collect_noresult);
                emptv.setText("还没有收藏哦。");
                lv.setEmptyView(emptv);
            }
        }
    };

    public void run(){//重写run()

        getActivity().runOnUiThread(new Runnable() {//更新ui 在主线程中
            @Override
            public void run() {

                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        showList();//是查询数据库
                    }

                }).start();
            }
        });
    }

    private  void showList(){
        List<String> data = new ArrayList<String>();
        PoemsManager poemsManager = new PoemsManager(getContext());
        for(PoemsItem poemsItem : poemsManager.listAll()){
            data.add(poemsItem.getPoemid() + "#" + poemsItem.getPoemtitle()+"#"+poemsItem.getPoemcontent());
        }
        String[] datastr = data.toArray(new String[]{});

        Message msg = handler.obtainMessage(0);
        msg.obj = datastr;
        handler.sendMessage(msg);
    }

}